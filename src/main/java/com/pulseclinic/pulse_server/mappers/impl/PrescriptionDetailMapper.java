package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionDetailMapper implements Mapper<PrescriptionDetail, PrescriptionDetailDto> {
    private final ModelMapper modelMapper;
    private final DrugMapper drugMapper;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionDetailMapper(ModelMapper modelMapper, DrugMapper drugMapper, PrescriptionMapper prescriptionMapper) {
        this.modelMapper = modelMapper;
        this.drugMapper = drugMapper;
        this.prescriptionMapper = prescriptionMapper;
    }

    @Override
    public PrescriptionDetailDto mapTo(PrescriptionDetail prescriptionDetail) {
        return PrescriptionDetailDto.builder()
                .id(prescriptionDetail.getId())
                .strengthText(prescriptionDetail.getStrengthText())
                .quantity(prescriptionDetail.getQuantity())
                .unitPrice(prescriptionDetail.getUnitPrice())
                .itemTotalPrice(prescriptionDetail.getItemTotalPrice())
                .dose(prescriptionDetail.getDose())
                .timing(prescriptionDetail.getTiming())
                .instructions(prescriptionDetail.getInstructions())
                .createdAt(prescriptionDetail.getCreatedAt())
                .frequency(prescriptionDetail.getFrequency())
                .drugDto(prescriptionDetail.getDrug() != null ? drugMapper.mapTo(prescriptionDetail.getDrug()) : null)
                .prescriptionDto(prescriptionDetail.getPrescription() != null ? prescriptionMapper.mapTo(prescriptionDetail.getPrescription()) : null)
                .build();
    }

    @Override
    public PrescriptionDetail mapFrom(PrescriptionDetailDto prescriptionDetailDto) {
        return this.modelMapper.map(prescriptionDetailDto, PrescriptionDetail.class);
    }

    public PrescriptionDetail mapFrom(PrescriptionRequestDto prescriptionRequestDto) {
        return this.modelMapper.map(prescriptionRequestDto, PrescriptionDetail.class);
    }
}
