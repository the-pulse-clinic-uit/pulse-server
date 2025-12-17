package com.pulseclinic.pulse_server.modules.patients.controller;

import com.pulseclinic.pulse_server.mappers.impl.PatientMapper;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientRequestDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientSearchDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    // for staff
    @PostMapping
    @PreAuthorize("hasAnyRole('staff', 'admin')")
    public ResponseEntity<PatientDto> registerPatient(@RequestBody PatientRequestDto patientRequestDto) {
        Patient patient = this.patientService.registerPatient(patientRequestDto);
        return new ResponseEntity<>(this.patientMapper.mapTo(patient), HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<List<PatientDto>> searchPatient(@RequestBody PatientSearchDto patientSearchDto) {
        List<Patient> patients = this.patientService.search(patientSearchDto);
        return new ResponseEntity<>(patients.stream().map(patient -> patientMapper.mapTo(patient)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") UUID id) {
        Optional<Patient> patient = this.patientService.findById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patientMapper.mapTo(patient.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PatchMapping("/me")
    public ResponseEntity<PatientDto> updatePatientMe(Authentication authentication,@RequestBody PatientDto patientDto) {
        String email = authentication.getName();
        Patient patient = this.patientService.updatePatientMe(email, patientDto);
        return new ResponseEntity<>(this.patientMapper.mapTo(patient), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('staff', 'admin')")
    public ResponseEntity<PatientDto> updatePatient(@RequestBody PatientDto patientDto, @PathVariable UUID id) {
        Patient patient = this.patientService.updatePatient(id, patientDto);
        return new ResponseEntity<>(this.patientMapper.mapTo(patient), HttpStatus.OK);
    }

}
