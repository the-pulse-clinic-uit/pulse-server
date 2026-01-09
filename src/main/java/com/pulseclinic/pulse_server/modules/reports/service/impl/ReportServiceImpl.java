package com.pulseclinic.pulse_server.modules.reports.service.impl;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.enums.AppointmentType;
import com.pulseclinic.pulse_server.enums.InvoiceStatus;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.billing.repository.InvoiceRepository;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.reports.dto.AppointmentReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.FinancialReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.PatientReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.PharmacyReportDto;
import com.pulseclinic.pulse_server.modules.reports.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final DrugRepository drugRepository;

    public ReportServiceImpl(PatientRepository patientRepository,
                             AppointmentRepository appointmentRepository,
                             InvoiceRepository invoiceRepository,
                             DrugRepository drugRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.drugRepository = drugRepository;
    }

    @Override
    public PatientReportDto getPatientReportByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Count new patient registrations
        Long newRegistrations = patientRepository.countByCreatedAtBetween(startOfDay, endOfDay);

        // Count follow-up visits (appointments with FOLLOW_UP type)
        Long followUpVisits = appointmentRepository.countByTypeAndStartsAtBetween(
                AppointmentType.FOLLOW_UP,
                startOfDay,
                endOfDay
        );

        return PatientReportDto.builder()
                .reportDate(date)
                .newRegistrations(newRegistrations.intValue())
                .followUpVisits(followUpVisits.intValue())
                .totalPatients(newRegistrations.intValue() + followUpVisits.intValue())
                .build();
    }

    @Override
    public List<PatientReportDto> getPatientReportByRange(LocalDate startDate, LocalDate endDate) {
        List<PatientReportDto> reports = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            reports.add(getPatientReportByDate(current));
            current = current.plusDays(1);
        }

        return reports;
    }

    @Override
    public AppointmentReportDto getAppointmentReportByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        Long total = appointmentRepository.countByStartsAtBetween(startOfDay, endOfDay);
        Long confirmed = appointmentRepository.countByStatusAndStartsAtBetween(
                AppointmentStatus.CONFIRMED, startOfDay, endOfDay);
        Long completed = appointmentRepository.countByStatusAndStartsAtBetween(
                AppointmentStatus.DONE, startOfDay, endOfDay);
        Long cancelled = appointmentRepository.countByStatusAndStartsAtBetween(
                AppointmentStatus.CANCELLED, startOfDay, endOfDay);
        Long noShow = appointmentRepository.countByStatusAndStartsAtBetween(
                AppointmentStatus.NO_SHOW, startOfDay, endOfDay);

        return AppointmentReportDto.builder()
                .reportDate(date)
                .totalAppointments(total.intValue())
                .confirmed(confirmed.intValue())
                .completed(completed.intValue())
                .cancelled(cancelled.intValue())
                .noShow(noShow.intValue())
                .byDepartment(new HashMap<>())
                .byDoctor(new HashMap<>())
                .build();
    }

    @Override
    public AppointmentReportDto getAppointmentReportByDoctor(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        Long total = appointmentRepository.countByDoctorIdAndStartsAtBetween(doctorId, start, end);
        Long confirmed = appointmentRepository.countByDoctorIdAndStatusAndStartsAtBetween(
                doctorId, AppointmentStatus.CONFIRMED, start, end);
        Long completed = appointmentRepository.countByDoctorIdAndStatusAndStartsAtBetween(
                doctorId, AppointmentStatus.DONE, start, end);
        Long cancelled = appointmentRepository.countByDoctorIdAndStatusAndStartsAtBetween(
                doctorId, AppointmentStatus.CANCELLED, start, end);
        Long noShow = appointmentRepository.countByDoctorIdAndStatusAndStartsAtBetween(
                doctorId, AppointmentStatus.NO_SHOW, start, end);

        return AppointmentReportDto.builder()
                .reportDate(startDate)
                .totalAppointments(total.intValue())
                .confirmed(confirmed.intValue())
                .completed(completed.intValue())
                .cancelled(cancelled.intValue())
                .noShow(noShow.intValue())
                .byDepartment(new HashMap<>())
                .byDoctor(new HashMap<>())
                .build();
    }

    @Override
    public AppointmentReportDto getAppointmentReportByDepartment(UUID departmentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        Long total = appointmentRepository.countByDoctorDepartmentIdAndStartsAtBetween(departmentId, start, end);
        Long confirmed = appointmentRepository.countByDoctorDepartmentIdAndStatusAndStartsAtBetween(
                departmentId, AppointmentStatus.CONFIRMED, start, end);
        Long completed = appointmentRepository.countByDoctorDepartmentIdAndStatusAndStartsAtBetween(
                departmentId, AppointmentStatus.DONE, start, end);
        Long cancelled = appointmentRepository.countByDoctorDepartmentIdAndStatusAndStartsAtBetween(
                departmentId, AppointmentStatus.CANCELLED, start, end);
        Long noShow = appointmentRepository.countByDoctorDepartmentIdAndStatusAndStartsAtBetween(
                departmentId, AppointmentStatus.NO_SHOW, start, end);

        return AppointmentReportDto.builder()
                .reportDate(startDate)
                .totalAppointments(total.intValue())
                .confirmed(confirmed.intValue())
                .completed(completed.intValue())
                .cancelled(cancelled.intValue())
                .noShow(noShow.intValue())
                .byDepartment(new HashMap<>())
                .byDoctor(new HashMap<>())
                .build();
    }

    @Override
    public FinancialReportDto getRevenueReportByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        BigDecimal totalRevenue = invoiceRepository.sumTotalAmountByCreatedAtBetween(startOfDay, endOfDay);
        BigDecimal paidAmount = invoiceRepository.sumAmountPaidByCreatedAtBetween(startOfDay, endOfDay);
        BigDecimal outstandingDebt = invoiceRepository.sumOutstandingDebt();

        return FinancialReportDto.builder()
                .startDate(date)
                .endDate(date)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .paidAmount(paidAmount != null ? paidAmount : BigDecimal.ZERO)
                .outstandingDebt(outstandingDebt != null ? outstandingDebt : BigDecimal.ZERO)
                .revenueByDepartment(new HashMap<>())
                .revenueByDoctor(new HashMap<>())
                .build();
    }

    @Override
    public FinancialReportDto getRevenueReportByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        BigDecimal totalRevenue = invoiceRepository.sumTotalAmountByCreatedAtBetween(start, end);
        BigDecimal paidAmount = invoiceRepository.sumAmountPaidByCreatedAtBetween(start, end);
        BigDecimal outstandingDebt = invoiceRepository.sumOutstandingDebt();

        return FinancialReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .paidAmount(paidAmount != null ? paidAmount : BigDecimal.ZERO)
                .outstandingDebt(outstandingDebt != null ? outstandingDebt : BigDecimal.ZERO)
                .revenueByDepartment(new HashMap<>())
                .revenueByDoctor(new HashMap<>())
                .build();
    }

    @Override
    public FinancialReportDto getRevenueReportByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        BigDecimal totalRevenue = invoiceRepository.sumTotalAmountByCreatedAtBetween(start, end);
        BigDecimal paidAmount = invoiceRepository.sumAmountPaidByCreatedAtBetween(start, end);
        BigDecimal outstandingDebt = invoiceRepository.sumOutstandingDebt();

        return FinancialReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .paidAmount(paidAmount != null ? paidAmount : BigDecimal.ZERO)
                .outstandingDebt(outstandingDebt != null ? outstandingDebt : BigDecimal.ZERO)
                .revenueByDepartment(new HashMap<>())
                .revenueByDoctor(new HashMap<>())
                .build();
    }

    @Override
    public FinancialReportDto getRevenueByDepartment(UUID departmentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        BigDecimal totalRevenue = invoiceRepository.sumTotalAmountByDepartmentIdAndCreatedAtBetween(departmentId, start, end);
        BigDecimal paidAmount = invoiceRepository.sumAmountPaidByDepartmentIdAndCreatedAtBetween(departmentId, start, end);
        BigDecimal outstandingDebt = invoiceRepository.sumOutstandingDebt();

        return FinancialReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .paidAmount(paidAmount != null ? paidAmount : BigDecimal.ZERO)
                .outstandingDebt(outstandingDebt != null ? outstandingDebt : BigDecimal.ZERO)
                .revenueByDepartment(new HashMap<>())
                .revenueByDoctor(new HashMap<>())
                .build();
    }

    @Override
    public FinancialReportDto getRevenueByDoctor(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        BigDecimal totalRevenue = invoiceRepository.sumTotalAmountByDoctorIdAndCreatedAtBetween(doctorId, start, end);
        BigDecimal paidAmount = invoiceRepository.sumAmountPaidByDoctorIdAndCreatedAtBetween(doctorId, start, end);
        BigDecimal outstandingDebt = invoiceRepository.sumOutstandingDebt();

        return FinancialReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .paidAmount(paidAmount != null ? paidAmount : BigDecimal.ZERO)
                .outstandingDebt(outstandingDebt != null ? outstandingDebt : BigDecimal.ZERO)
                .revenueByDepartment(new HashMap<>())
                .revenueByDoctor(new HashMap<>())
                .build();
    }

    @Override
    public List<PharmacyReportDto> getLowStockDrugs() {
        List<Drug> drugs = drugRepository.findAll();
        LocalDate today = LocalDate.now();

        return drugs.stream()
                .filter(drug -> drug.getDeletedAt() == null)
                .filter(drug -> drug.getQuantity() < drug.getMinStockLevel() && drug.getQuantity() > 0)
                .map(drug -> mapToPharmacyReportDto(drug, "LOW_STOCK", today))
                .collect(Collectors.toList());
    }

    @Override
    public List<PharmacyReportDto> getExpiringDrugs(Integer days) {
        if (days == null || days <= 0) {
            days = 30; // Default to 30 days
        }

        List<Drug> drugs = drugRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalDate expiryThreshold = today.plusDays(days);

        return drugs.stream()
                .filter(drug -> drug.getDeletedAt() == null)
                .filter(drug -> drug.getExpiryDate() != null)
                .filter(drug -> !drug.getExpiryDate().isBefore(today) && !drug.getExpiryDate().isAfter(expiryThreshold))
                .map(drug -> mapToPharmacyReportDto(drug, "EXPIRING_SOON", today))
                .collect(Collectors.toList());
    }

    @Override
    public List<PharmacyReportDto> getOutOfStockDrugs() {
        List<Drug> drugs = drugRepository.findAll();
        LocalDate today = LocalDate.now();

        return drugs.stream()
                .filter(drug -> drug.getDeletedAt() == null)
                .filter(drug -> drug.getQuantity() == 0)
                .map(drug -> mapToPharmacyReportDto(drug, "OUT_OF_STOCK", today))
                .collect(Collectors.toList());
    }

    private PharmacyReportDto mapToPharmacyReportDto(Drug drug, String status, LocalDate today) {
        Integer daysUntilExpiry = null;
        if (drug.getExpiryDate() != null) {
            daysUntilExpiry = (int) ChronoUnit.DAYS.between(today, drug.getExpiryDate());
        }

        return PharmacyReportDto.builder()
                .drugId(drug.getId())
                .drugName(drug.getName())
                .dosageForm(drug.getDosageForm())
                .unit(drug.getUnit())
                .strength(drug.getStrength())
                .currentQuantity(drug.getQuantity())
                .minStockLevel(drug.getMinStockLevel())
                .expiryDate(drug.getExpiryDate())
                .batchNumber(drug.getBatchNumber())
                .unitPrice(drug.getUnitPrice())
                .daysUntilExpiry(daysUntilExpiry)
                .status(status)
                .build();
    }
}
