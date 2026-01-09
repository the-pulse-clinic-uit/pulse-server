package com.pulseclinic.pulse_server.modules.pharmacy.service.impl;

import com.pulseclinic.pulse_server.enums.AllergySeverity;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy.AllergyWarning;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.service.AllergyCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AllergyCheckServiceImpl implements AllergyCheckService {

    private final PatientRepository patientRepository;
    private final DrugRepository drugRepository;

    // common high-risk drug allergens mapping
    private static final Map<String, AllergySeverity> HIGH_RISK_ALLERGENS = Map.of(
            "penicillin", AllergySeverity.HIGH,
            "amoxicillin", AllergySeverity.HIGH,
            "aspirin", AllergySeverity.HIGH,
            "nsaid", AllergySeverity.HIGH,
            "ibuprofen", AllergySeverity.HIGH,
            "sulfa", AllergySeverity.HIGH);

    public AllergyCheckServiceImpl(PatientRepository patientRepository, DrugRepository drugRepository) {
        this.patientRepository = patientRepository;
        this.drugRepository = drugRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllergyWarning> checkPatientAllergies(UUID patientId, List<UUID> drugIds) {
        List<AllergyWarning> warnings = new ArrayList<>();

        // Get patient
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            log.warn("Patient not found with ID: {}", patientId);
            return warnings;
        }

        Patient patient = patientOpt.get();
        String allergiesText = patient.getAllergies();

        if (allergiesText == null || allergiesText.trim().isEmpty() ||
                allergiesText.equalsIgnoreCase("None") || allergiesText.equalsIgnoreCase("N/A")) {
            return warnings;
        }

        // parse patient allergies (comma-separated)
        List<String> patientAllergies = parseAllergies(allergiesText);

        List<Drug> drugs = drugRepository.findAllById(drugIds);

        for (Drug drug : drugs) {
            for (String allergen : patientAllergies) {
                if (isDrugAllergyMatch(drug, allergen)) {
                    AllergySeverity severity = determineSeverity(allergen, drug);
                    AllergyWarning warning = AllergyWarning.builder()
                            .drugId(drug.getId())
                            .drugName(drug.getName())
                            .allergen(allergen)
                            .severity(severity)
                            .message(buildWarningMessage(drug.getName(), allergen, severity))
                            .build();
                    warnings.add(warning);
                    log.warn("Allergy warning: Patient {} allergic to {} - Drug: {}",
                            patientId, allergen, drug.getName());
                }
            }
        }

        return warnings;
    }

    private List<String> parseAllergies(String allergiesText) {
        return Arrays.stream(allergiesText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private boolean isDrugAllergyMatch(Drug drug, String allergen) {
        String drugNameLower = drug.getName().toLowerCase();
        String allergenLower = allergen.toLowerCase();

        if (drugNameLower.contains(allergenLower) || allergenLower.contains(drugNameLower)) {
            return true;
        }

        // check for common drug class matches
        if (allergenLower.contains("penicillin") && isPenicillinDerivative(drugNameLower)) {
            return true;
        }

        if (allergenLower.contains("nsaid") && isNSAID(drugNameLower)) {
            return true;
        }

        return false;
    }

    private boolean isPenicillinDerivative(String drugName) {
        String[] penicillinDrugs = { "amoxicillin", "ampicillin", "penicillin", "oxacillin", "cloxacillin" };
        for (String pen : penicillinDrugs) {
            if (drugName.contains(pen)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNSAID(String drugName) {
        String[] nsaids = { "ibuprofen", "aspirin", "naproxen", "diclofenac", "celecoxib", "meloxicam" };
        for (String nsaid : nsaids) {
            if (drugName.contains(nsaid)) {
                return true;
            }
        }
        return false;
    }

    private AllergySeverity determineSeverity(String allergen, Drug drug) {
        String allergenLower = allergen.toLowerCase();

        for (Map.Entry<String, AllergySeverity> entry : HIGH_RISK_ALLERGENS.entrySet()) {
            if (allergenLower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // default to MEDIUM for drug allergies
        return AllergySeverity.MEDIUM;
    }

    private String buildWarningMessage(String drugName, String allergen, AllergySeverity severity) {
        switch (severity) {
            case HIGH:
                return String.format("⚠️ CRITICAL ALLERGY WARNING: Patient is allergic to '%s'. " +
                        "Drug '%s' may cause severe allergic reaction. Consider alternative medication.",
                        allergen, drugName);
            case MEDIUM:
                return String.format("⚠️ ALLERGY WARNING: Patient is allergic to '%s'. " +
                        "Drug '%s' may cause allergic reaction. Monitor patient closely.",
                        allergen, drugName);
            case LOW:
                return String.format("⚠️ Possible allergy: Patient has recorded allergy to '%s'. " +
                        "Drug '%s' may require caution.", allergen, drugName);
            default:
                return String.format("Allergy alert for drug '%s' and allergen '%s'", drugName, allergen);
        }
    }
}
