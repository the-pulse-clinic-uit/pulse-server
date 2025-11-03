package com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PrescriptionDetailRequestDto {
    private String strength_text;

    @NotNull(message = "QUantity is required")
    private Integer quantity;

    @Positive(message = "Unit Price must be Positive")
    private BigDecimal unit_price;

    @Positive(message = "Unit Price must be Positive")
    private BigDecimal item_total_price;

    private String dose; // eg 1 tablet

    private String timing; // before meal, after meal

    private String instructions;

    private String frequency; // 2 times per day

    // relationships => 2
    @NotNull(message = "Drug ID is required")
    private UUID drug_id;

    @NotNull(message="Prescription ID is required")
    private UUID prescription_id;
}
