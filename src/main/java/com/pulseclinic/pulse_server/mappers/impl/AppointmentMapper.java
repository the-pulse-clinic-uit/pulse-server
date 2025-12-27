package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentRequestDto;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper implements Mapper<Appointment, AppointmentDto> {
    private final ModelMapper modelMapper;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final ShiftAssignmentMapper shiftAssignmentMapper;

    public AppointmentMapper(ModelMapper modelMapper, PatientMapper patientMapper,
                           DoctorMapper doctorMapper, ShiftAssignmentMapper shiftAssignmentMapper) {
        this.modelMapper = modelMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
        this.shiftAssignmentMapper = shiftAssignmentMapper;
    }

    @Override
    public AppointmentDto mapTo(Appointment appointment) {
        return AppointmentDto.builder()
                .id(appointment.getId())
                .startsAt(appointment.getStartsAt())
                .endsAt(appointment.getEndsAt())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .description(appointment.getDescription())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .patientDto(appointment.getPatient() != null ? patientMapper.mapTo(appointment.getPatient()) : null)
                .doctorDto(appointment.getDoctor() != null ? doctorMapper.mapTo(appointment.getDoctor()) : null)
                .shiftAssignmentDto(appointment.getShiftAssignment() != null ? shiftAssignmentMapper.mapTo(appointment.getShiftAssignment()) : null)
                // Use ModelMapper for followUpPlan to avoid circular dependency
                .followUpPlanDto(appointment.getFollowUpPlan() != null ? modelMapper.map(appointment.getFollowUpPlan(), com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto.class) : null)
                .build();
    }

    @Override
    public Appointment mapFrom(AppointmentDto appointmentDto) {
        return this.modelMapper.map(appointmentDto, Appointment.class);
    }

    public Appointment mapFrom(AppointmentRequestDto appointmentRequestDto) {
        return this.modelMapper.map(appointmentRequestDto, Appointment.class);
    }
}
