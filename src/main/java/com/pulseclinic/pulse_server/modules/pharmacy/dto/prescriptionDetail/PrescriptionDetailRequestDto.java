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
    private BigDecimal unitPrice;

    @Positive(message = "Unit Price must be Positive")
    private BigDecimal itemTotalPrice;

    private String dose; // eg 1 tablet

    private String timing; // before meal, after meal

    private String instructions;

    private String frequency; // 2 times per day

    // relationships => 2
    @NotNull(message = "Drug ID is required")
    private UUID drugId;

    @NotNull(message="Prescription ID is required")
    private UUID prescriptionId;
}
