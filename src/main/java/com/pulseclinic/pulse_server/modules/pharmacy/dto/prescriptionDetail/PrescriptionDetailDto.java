package com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PrescriptionDetailDto {
    private UUID id;

    private String strength_text;

    private Integer quantity;

    private BigDecimal unit_price;

    private BigDecimal item_total_price;

    private String dose; // eg 1 tablet

    private String timing; // before meal, after meal

    private String instructions;

    private LocalDateTime created_at = LocalDateTime.now();

    private String frequency; // 2 times per day

    // relationships => 2
    private DrugDto drug_dto;

    private PrescriptionDto prescription_dto;
}
