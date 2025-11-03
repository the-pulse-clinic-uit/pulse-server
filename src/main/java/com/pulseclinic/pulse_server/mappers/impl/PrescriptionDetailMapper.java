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

    public PrescriptionDetailMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public PrescriptionDetailDto mapTo(PrescriptionDetail prescriptionDetail) {
        return this.modelMapper.map(prescriptionDetail, PrescriptionDetailDto.class);
    }

    @Override
    public PrescriptionDetail mapFrom(PrescriptionDetailDto prescriptionDetailDto) {
        return this.modelMapper.map(prescriptionDetailDto, PrescriptionDetail.class);
    }

    public PrescriptionDetail mapFrom(PrescriptionRequestDto prescriptionRequestDto) {
        return this.modelMapper.map(prescriptionRequestDto, PrescriptionDetail.class);
    }
}
