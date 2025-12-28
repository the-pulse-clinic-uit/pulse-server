package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper implements Mapper<Prescription, PrescriptionDto> {
    private final ModelMapper modelMapper;
    private final EncounterMapper encounterMapper;

    public PrescriptionMapper(ModelMapper modelMapper, EncounterMapper encounterMapper) {
        this.modelMapper = modelMapper;
        this.encounterMapper = encounterMapper;
    }

    @Override
    public PrescriptionDto mapTo(Prescription prescription) {
        return PrescriptionDto.builder()
                .id(prescription.getId())
                .totalPrice(prescription.getTotalPrice())
                .notes(prescription.getNotes())
                .createdAt(prescription.getCreatedAt())
                .status(prescription.getStatus())
                .encounterDto(prescription.getEncounter() != null ? encounterMapper.mapTo(prescription.getEncounter()) : null)
                .build();
    }

    @Override
    public Prescription mapFrom(PrescriptionDto prescriptionDto) {
        return this.modelMapper.map(prescriptionDto, Prescription.class);
    }

    public Prescription mapFrom(PrescriptionRequestDto prescriptionRequestDto) {
        return this.modelMapper.map(prescriptionRequestDto, Prescription.class);
    }
}
