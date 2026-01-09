package com.pulseclinic.pulse_server.modules.patients.controller;

import com.pulseclinic.pulse_server.mappers.impl.PatientMapper;
import com.pulseclinic.pulse_server.mappers.impl.UserMapper;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientRequestDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientSearchDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientViolationSummary;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.service.PatientService;
import com.pulseclinic.pulse_server.modules.patients.service.PatientViolationService;
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
    private final UserMapper userMapper;
    private final PatientViolationService patientViolationService;

    public PatientController(PatientService patientService,
            PatientMapper patientMapper,
            UserMapper userMapper,
            PatientViolationService patientViolationService) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.userMapper = userMapper;
        this.patientViolationService = patientViolationService;
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<PatientDto>> searchPatient(
            @RequestBody PatientSearchDto patientSearchDto,
            @RequestParam(value = "withViolations", required = false, defaultValue = "false") boolean withViolations) {
        List<Patient> patients = this.patientService.search(patientSearchDto);
        List<PatientDto> patientDtos = patients.stream()
                .map(patient -> {
                    PatientDto dto = patientMapper.mapTo(patient);
                    if (withViolations) {
                        enrichWithViolationData(dto, patient.getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(patientDtos, HttpStatus.OK);
    }

    // for staff
    @PostMapping
    @PreAuthorize("hasAnyAuthority('staff', 'doctor')")
    public ResponseEntity<PatientDto> registerPatient(@RequestBody PatientRequestDto patientRequestDto) {
        Patient patient = this.patientService.registerPatient(patientRequestDto);
        PatientDto patientDto = this.patientMapper.mapTo(patient);
        patientDto.setUserDto(this.userMapper.mapTo(patient.getUser()));

        return new ResponseEntity<>(patientDto, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<PatientDto> getPatientMe(Authentication authentication) {
        String email = authentication.getName();
        Optional<Patient> patient = this.patientService.findByEmail(email);
        if (patient.isPresent()) {
            return new ResponseEntity<>(this.patientMapper.mapTo(patient.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<PatientDto>> getPatients(
            @RequestParam(value = "withViolations", required = false, defaultValue = "false") boolean withViolations) {
        List<Patient> patients = this.patientService.getPatients();
        List<PatientDto> patientDtos = patients.stream()
                .map(patient -> {
                    PatientDto dto = patientMapper.mapTo(patient);
                    if (withViolations) {
                        enrichWithViolationData(dto, patient.getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(patientDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") UUID id) {
        Optional<Patient> patient = this.patientService.findById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patientMapper.mapTo(patient.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/me")
    public ResponseEntity<PatientDto> updatePatientMe(Authentication authentication,
            @RequestBody PatientDto patientDto) {
        String email = authentication.getName();
        Patient patient = this.patientService.updatePatientMe(email, patientDto);
        return new ResponseEntity<>(this.patientMapper.mapTo(patient), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<PatientDto> updatePatient(@RequestBody PatientDto patientDto, @PathVariable UUID id) {
        Patient patient = this.patientService.updatePatient(id, patientDto);
        return new ResponseEntity<>(this.patientMapper.mapTo(patient), HttpStatus.OK);
    }

    @GetMapping("/{id}/violations")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<PatientViolationSummary> getPatientViolations(@PathVariable("id") UUID id) {
        try {
            PatientViolationSummary summary = patientViolationService.getViolationSummary(id);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void enrichWithViolationData(PatientDto dto, UUID patientId) {
        try {
            PatientViolationSummary summary = patientViolationService.getViolationSummary(patientId);
            dto.setHasViolations(summary.isHasViolations());
            dto.setViolationLevel(summary.getRiskLevel());
            dto.setNoShowCount(summary.getNoShowCount());
            dto.setOutstandingDebt(summary.getOutstandingDebt());
        } catch (Exception e) {
            dto.setHasViolations(false);
            dto.setViolationLevel(null);
            dto.setNoShowCount(0);
            dto.setOutstandingDebt(null);
        }
    }
}
