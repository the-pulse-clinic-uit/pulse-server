package com.pulseclinic.pulse_server.modules.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreatePaymentResponse {
    public String code;
    public String message;
    public String paymentUrl;
}