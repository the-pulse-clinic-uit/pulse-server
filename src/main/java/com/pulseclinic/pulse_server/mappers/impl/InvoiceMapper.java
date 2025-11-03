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

    public InvoiceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public InvoiceDto mapTo(Invoice invoice) {
        return this.modelMapper.map(invoice, InvoiceDto.class);
    }

    @Override
    public Invoice mapFrom(InvoiceDto invoiceDto) {
        return this.modelMapper.map(invoiceDto, Invoice.class);
    }

    public Invoice mapFrom(InvoiceRequestDto invoiceRequestDto) {
        return this.modelMapper.map(invoiceRequestDto, Invoice.class);
    }
}
