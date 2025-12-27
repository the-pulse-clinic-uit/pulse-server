package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceDto;
import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceRequestDto;
import com.pulseclinic.pulse_server.modules.billing.entity.Invoice;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper implements Mapper<Invoice, InvoiceDto> {
    private final ModelMapper modelMapper;
    private final EncounterMapper encounterMapper;

    public InvoiceMapper(ModelMapper modelMapper, EncounterMapper encounterMapper) {
        this.modelMapper = modelMapper;
        this.encounterMapper = encounterMapper;
    }

    @Override
    public InvoiceDto mapTo(Invoice invoice) {
        return InvoiceDto.builder()
                .id(invoice.getId())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .amountPaid(invoice.getAmountPaid())
                .totalAmount(invoice.getTotalAmount())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .encounterDto(invoice.getEncounter() != null ? encounterMapper.mapTo(invoice.getEncounter()) : null)
                .build();
    }

    @Override
    public Invoice mapFrom(InvoiceDto invoiceDto) {
        return this.modelMapper.map(invoiceDto, Invoice.class);
    }

    public Invoice mapFrom(InvoiceRequestDto invoiceRequestDto) {
        return this.modelMapper.map(invoiceRequestDto, Invoice.class);
    }
}
