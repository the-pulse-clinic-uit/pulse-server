package com.pulseclinic.pulse_server.modules.pharmacy.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionDetailRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.service.DrugService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionWithDetailsDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionService;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final EncounterRepository encounterRepository;
    private final com.pulseclinic.pulse_server.mappers.impl.PrescriptionMapper prescriptionMapper;
    private final com.pulseclinic.pulse_server.mappers.impl.PrescriptionDetailMapper prescriptionDetailMapper;
    private final com.pulseclinic.pulse_server.mappers.impl.EncounterMapper encounterMapper;
    private final com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository appointmentRepository;
    private final DrugService drugService;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                  PrescriptionDetailRepository prescriptionDetailRepository,
                                  com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository encounterRepository,
                                  com.pulseclinic.pulse_server.mappers.impl.PrescriptionMapper prescriptionMapper,
                                  com.pulseclinic.pulse_server.mappers.impl.PrescriptionDetailMapper prescriptionDetailMapper,
                                  com.pulseclinic.pulse_server.mappers.impl.EncounterMapper encounterMapper,
                                  com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository appointmentRepository,
                                  DrugService drugService) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionDetailRepository = prescriptionDetailRepository;
        this.encounterRepository = encounterRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionDetailMapper = prescriptionDetailMapper;
        this.encounterMapper = encounterMapper;
        this.appointmentRepository = appointmentRepository;
        this.drugService = drugService;
    }

    @Override
    @Transactional
    public PrescriptionDto createPrescription(PrescriptionRequestDto prescriptionRequestDto) {
        var encounterOpt = encounterRepository.findById(prescriptionRequestDto.getEncounterId());
        if (encounterOpt.isEmpty()) {
            throw new RuntimeException("Encounter not found");
        }

        com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription prescription = 
            com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes(prescriptionRequestDto.getNotes() != null ? prescriptionRequestDto.getNotes() : "")
                .status(com.pulseclinic.pulse_server.enums.PrescriptionStatus.DRAFT)
                .encounter(encounterOpt.get())
                .build();

        var savedPrescription = prescriptionRepository.save(prescription);
        return prescriptionMapper.mapTo(savedPrescription);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<PrescriptionDto> getPrescriptionById(UUID prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .map(prescriptionMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDto> getPrescriptionsByPatientId(UUID patientId) {
        return prescriptionRepository.findByPatientId(patientId)
                .stream()
                .map(prescriptionMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDetailDto> getDetails(UUID prescriptionId) {
        return prescriptionDetailRepository.findByPrescriptionIdAndDeletedAtIsNullOrderByCreatedAtAsc(prescriptionId)
                .stream()
                .map(prescriptionDetailMapper::mapTo)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public boolean finalizePrescription(UUID prescriptionId) {
        try {
            var prescriptionOpt = prescriptionRepository.findById(prescriptionId);
            if (prescriptionOpt.isEmpty()) {
                return false;
            }

            var prescription = prescriptionOpt.get();

            if (prescription.getStatus() != com.pulseclinic.pulse_server.enums.PrescriptionStatus.DRAFT) {
                return false;
            }

            prescription.setTotalPrice(calculateTotal(prescriptionId));
            prescription.setStatus(com.pulseclinic.pulse_server.enums.PrescriptionStatus.DISPENSED);
            prescriptionRepository.save(prescription);

            // Auto-end encounter when prescription is finalized
            var encounter = prescription.getEncounter();
            if (encounter != null && encounter.getEndedAt() == null) {
                encounter.setEndedAt(java.time.LocalDateTime.now());
                encounterRepository.save(encounter);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean dispenseMedication(UUID prescriptionId) {
        try {
            var prescriptionOpt = prescriptionRepository.findById(prescriptionId);
            if (prescriptionOpt.isEmpty()) {
                return false;
            }

            var prescription = prescriptionOpt.get();

            if (prescription.getStatus() != com.pulseclinic.pulse_server.enums.PrescriptionStatus.DISPENSED) {
                return false;
            }

            prescription.setStatus(com.pulseclinic.pulse_server.enums.PrescriptionStatus.DISPENSED);
            prescriptionRepository.save(prescription);
            autoCompleteAppointment(prescription);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void autoCompleteAppointment(com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription prescription) {
        try {
            var encounter = prescription.getEncounter();
            if (encounter != null && encounter.getAppointment() != null) {
                var appointment = encounter.getAppointment();
                // only mark as done if not already done or cancelled
                if (appointment.getStatus() != com.pulseclinic.pulse_server.enums.AppointmentStatus.DONE &&
                    appointment.getStatus() != com.pulseclinic.pulse_server.enums.AppointmentStatus.CANCELLED) {
                    appointment.setStatus(com.pulseclinic.pulse_server.enums.AppointmentStatus.DONE);
                    appointmentRepository.save(appointment);
                }
            }
        } catch (Exception e) {
            System.err.println("Error auto-completing appointment: " + e.getMessage());
        }
    }

    @Override
    //@Transactional(readOnly = true)
    public BigDecimal calculateTotal(UUID prescriptionId) {
        List<com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail> details = 
            prescriptionDetailRepository.findByPrescriptionIdAndDeletedAtIsNullOrderByCreatedAtAsc(prescriptionId);
        
        BigDecimal totalPrice = details.stream()
                .map(com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Optional<Prescription> prescription = prescriptionRepository.findById(prescriptionId);
        if (prescription.isPresent()) {
            prescription.get().setTotalPrice(totalPrice);
            prescriptionRepository.save(prescription.get());
        }
        return totalPrice;
    }

    @Override
    @Transactional(readOnly = true)
    public String printPrescription(UUID prescriptionId) {
        var prescriptionOpt = prescriptionRepository.findById(prescriptionId);
        if (prescriptionOpt.isEmpty()) {
            return "";
        }

        var prescription = prescriptionOpt.get();
        var details = prescriptionDetailRepository.findByPrescriptionIdAndDeletedAtIsNullOrderByCreatedAtAsc(prescriptionId);

        StringBuilder printout = new StringBuilder();
        printout.append("===== ĐƠN THUỐC =====\n\n");
        printout.append("Mã đơn: ").append(prescription.getId()).append("\n");
        printout.append("Ngày kê: ").append(prescription.getCreatedAt()).append("\n");
        printout.append("Trạng thái: ").append(prescription.getStatus()).append("\n");
        printout.append("Bệnh nhân: ").append(prescription.getEncounter().getPatient().getUser().getFullName()).append("\n");
        printout.append("Bác sĩ: ").append(prescription.getEncounter().getDoctor().getStaff().getUser().getFullName()).append("\n\n");
        
        printout.append("Danh sách thuốc:\n");
        printout.append("------------------------------------------------\n");
        
        int index = 1;
        for (var detail : details) {
            printout.append(index++).append(". ");
            printout.append(detail.getDrug().getName());
            if (detail.getStrengthText() != null) {
                printout.append(" (").append(detail.getStrengthText()).append(")");
            }
            printout.append("\n");
            printout.append("   Liều dùng: ").append(detail.getDose()).append("\n");
            printout.append("   Thời gian: ").append(detail.getTiming()).append("\n");
            printout.append("   Số lượng: ").append(detail.getQuantity()).append("\n");
            printout.append("   Đơn giá: ").append(detail.getUnitPrice()).append(" VNĐ\n");
            printout.append("   Thành tiền: ").append(detail.getItemTotalPrice()).append(" VNĐ\n\n");
        }
        
        printout.append("------------------------------------------------\n");
        printout.append("Tổng cộng: ").append(prescription.getTotalPrice()).append(" VNĐ\n\n");
        
        if (prescription.getNotes() != null && !prescription.getNotes().isEmpty()) {
            printout.append("Ghi chú: ").append(prescription.getNotes()).append("\n");
        }

        return printout.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionWithDetailsDto> getPrescriptionsByDoctorId(UUID doctorId) {
        List<Prescription> prescriptions = prescriptionRepository.findByDoctorIdWithDetails(doctorId);
        return prescriptions.stream()
                .map(this::mapToPrescriptionWithDetails)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionWithDetailsDto> getAllPrescriptionsWithDetails() {
        List<Prescription> prescriptions = prescriptionRepository.findAllWithDetails();
        return prescriptions.stream()
                .map(this::mapToPrescriptionWithDetails)
                .toList();
    }

    private PrescriptionWithDetailsDto mapToPrescriptionWithDetails(Prescription prescription) {
        List<PrescriptionDetailDto> details = prescriptionDetailRepository
                .findByPrescriptionIdAndDeletedAtIsNullOrderByCreatedAtAsc(prescription.getId())
                .stream()
                .map(prescriptionDetailMapper::mapTo)
                .toList();

        return PrescriptionWithDetailsDto.builder()
                .id(prescription.getId())
                .totalPrice(prescription.getTotalPrice())
                .notes(prescription.getNotes())
                .createdAt(prescription.getCreatedAt())
                .status(prescription.getStatus())
                .encounterDto(prescription.getEncounter() != null
                    ? encounterMapper.mapTo(prescription.getEncounter())
                    : null)
                .prescriptionDetails(details)
                .build();
    }
}