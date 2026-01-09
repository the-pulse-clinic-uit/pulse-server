package com.pulseclinic.pulse_server.modules.patients.service.impl;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.enums.InvoiceStatus;
import com.pulseclinic.pulse_server.enums.ViolationLevel;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.billing.entity.Invoice;
import com.pulseclinic.pulse_server.modules.billing.repository.InvoiceRepository;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientViolationSummary;
import com.pulseclinic.pulse_server.modules.patients.service.PatientViolationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PatientViolationServiceImpl implements PatientViolationService {

    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;

    private static final int NO_SHOW_LOW_THRESHOLD = 2;
    private static final int NO_SHOW_MEDIUM_THRESHOLD = 4;
    private static final int NO_SHOW_HIGH_THRESHOLD = 5;

    private static final BigDecimal DEBT_LOW_THRESHOLD = new BigDecimal("500000");
    private static final BigDecimal DEBT_MEDIUM_THRESHOLD = new BigDecimal("2000000");

    public PatientViolationServiceImpl(AppointmentRepository appointmentRepository,
            InvoiceRepository invoiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PatientViolationSummary getViolationSummary(UUID patientId) {
        int noShowCount = calculateNoShowCount(patientId);
        BigDecimal outstandingDebt = calculateOutstandingDebt(patientId);
        LocalDateTime lastViolationDate = getLastViolationDate(patientId);
        ViolationLevel riskLevel = calculateRiskLevel(noShowCount, outstandingDebt);
        boolean hasViolations = riskLevel != ViolationLevel.NONE;
        String message = buildViolationMessage(noShowCount, outstandingDebt, riskLevel);

        return PatientViolationSummary.builder()
                .noShowCount(noShowCount)
                .outstandingDebt(outstandingDebt)
                .lastViolationDate(lastViolationDate)
                .riskLevel(riskLevel)
                .hasViolations(hasViolations)
                .message(message)
                .build();
    }

    private int calculateNoShowCount(UUID patientId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        List<Appointment> appointments = appointmentRepository
                .findByPatientIdAndDeletedAtIsNullOrderByStartsAtDesc(patientId);

        long count = appointments.stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.NO_SHOW)
                .filter(apt -> apt.getStartsAt().isAfter(sixMonthsAgo))
                .count();

        return (int) count;
    }

    private BigDecimal calculateOutstandingDebt(UUID patientId) {
        List<Invoice> invoices = invoiceRepository.findByPatientId(patientId);

        return invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.UNPAID ||
                        inv.getStatus() == InvoiceStatus.PARTIAL ||
                        inv.getStatus() == InvoiceStatus.OVERDUE)
                .map(inv -> inv.getTotalAmount().subtract(inv.getAmountPaid()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDateTime getLastViolationDate(UUID patientId) {
        LocalDateTime lastNoShow = getLastNoShowDate(patientId);
        LocalDateTime lastOverdue = getLastOverdueDate(patientId);

        if (lastNoShow == null && lastOverdue == null) {
            return null;
        } else if (lastNoShow == null) {
            return lastOverdue;
        } else if (lastOverdue == null) {
            return lastNoShow;
        } else {
            return lastNoShow.isAfter(lastOverdue) ? lastNoShow : lastOverdue;
        }
    }

    private LocalDateTime getLastNoShowDate(UUID patientId) {
        List<Appointment> appointments = appointmentRepository
                .findByPatientIdAndDeletedAtIsNullOrderByStartsAtDesc(patientId);

        return appointments.stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.NO_SHOW)
                .map(Appointment::getStartsAt)
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime getLastOverdueDate(UUID patientId) {
        List<Invoice> invoices = invoiceRepository.findByPatientId(patientId);

        return invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.OVERDUE)
                .map(Invoice::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private ViolationLevel calculateRiskLevel(int noShowCount, BigDecimal outstandingDebt) {
        ViolationLevel noShowLevel = getNoShowRiskLevel(noShowCount);
        ViolationLevel debtLevel = getDebtRiskLevel(outstandingDebt);

        if (noShowLevel == ViolationLevel.HIGH || debtLevel == ViolationLevel.HIGH) {
            return ViolationLevel.HIGH;
        } else if (noShowLevel == ViolationLevel.MEDIUM || debtLevel == ViolationLevel.MEDIUM) {
            return ViolationLevel.MEDIUM;
        } else if (noShowLevel == ViolationLevel.LOW || debtLevel == ViolationLevel.LOW) {
            return ViolationLevel.LOW;
        } else {
            return ViolationLevel.NONE;
        }
    }

    private ViolationLevel getNoShowRiskLevel(int noShowCount) {
        if (noShowCount >= NO_SHOW_HIGH_THRESHOLD) {
            return ViolationLevel.HIGH;
        } else if (noShowCount >= NO_SHOW_MEDIUM_THRESHOLD) {
            return ViolationLevel.MEDIUM;
        } else if (noShowCount > 0) {
            return ViolationLevel.LOW;
        } else {
            return ViolationLevel.NONE;
        }
    }

    private ViolationLevel getDebtRiskLevel(BigDecimal outstandingDebt) {
        if (outstandingDebt.compareTo(DEBT_MEDIUM_THRESHOLD) > 0) {
            return ViolationLevel.HIGH;
        } else if (outstandingDebt.compareTo(DEBT_LOW_THRESHOLD) > 0) {
            return ViolationLevel.MEDIUM;
        } else if (outstandingDebt.compareTo(BigDecimal.ZERO) > 0) {
            return ViolationLevel.LOW;
        } else {
            return ViolationLevel.NONE;
        }
    }

    private String buildViolationMessage(int noShowCount, BigDecimal outstandingDebt, ViolationLevel riskLevel) {
        if (riskLevel == ViolationLevel.NONE) {
            return "No violations - patient in good standing";
        }

        StringBuilder message = new StringBuilder();

        if (noShowCount > 0) {
            message.append(String.format("%d no-show(s) in last 6 months. ", noShowCount));
        }

        if (outstandingDebt.compareTo(BigDecimal.ZERO) > 0) {
            message.append(String.format("Outstanding debt: %,d VND. ", outstandingDebt.longValue()));
        }

        switch (riskLevel) {
            case HIGH:
                message.append("HIGH RISK - Immediate attention required.");
                break;
            case MEDIUM:
                message.append("MEDIUM RISK - Monitor closely.");
                break;
            case LOW:
                message.append("LOW RISK - Minor concerns.");
                break;
        }

        return message.toString();
    }
}
