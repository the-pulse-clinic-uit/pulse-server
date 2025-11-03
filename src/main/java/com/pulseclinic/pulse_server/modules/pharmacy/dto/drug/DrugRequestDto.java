package com.pulseclinic.pulse_server.modules.pharmacy.dto.drug;

import com.pulseclinic.pulse_server.enums.DrugDosageForm;
import com.pulseclinic.pulse_server.enums.DrugUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class DrugRequestDto {
    @NotNull(message = "Drug's name is required")
    private String name;

    private DrugDosageForm dosage_form;

    private DrugUnit unit;

    private String strength;

    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unit_price;
}
