package com.pulseclinic.pulse_server.modules.reports.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.reports.dto.AppointmentReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.FinancialReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.PatientReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.PharmacyReportDto;
import com.pulseclinic.pulse_server.modules.reports.service.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Patient Reports
    @GetMapping("/patients/daily")
    public ResponseEntity<PatientReportDto> getPatientReportByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        PatientReportDto report = reportService.getPatientReportByDate(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/patients/range")
    public ResponseEntity<List<PatientReportDto>> getPatientReportByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PatientReportDto> reports = reportService.getPatientReportByRange(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    // Appointment Reports
    @GetMapping("/appointments/daily")
    public ResponseEntity<AppointmentReportDto> getAppointmentReportByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AppointmentReportDto report = reportService.getAppointmentReportByDate(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/appointments/range")
    public ResponseEntity<List<AppointmentReportDto>> getAppointmentReportByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AppointmentReportDto> reports = reportService.getAppointmentReportByRange(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/appointments/by-doctor/{doctorId}")
    public ResponseEntity<AppointmentReportDto> getAppointmentReportByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AppointmentReportDto report = reportService.getAppointmentReportByDoctor(doctorId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/appointments/by-department/{departmentId}")
    public ResponseEntity<AppointmentReportDto> getAppointmentReportByDepartment(
            @PathVariable UUID departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AppointmentReportDto report = reportService.getAppointmentReportByDepartment(departmentId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    // Financial Reports
    @GetMapping("/revenue/daily")
    public ResponseEntity<FinancialReportDto> getRevenueReportByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        FinancialReportDto report = reportService.getRevenueReportByDate(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<FinancialReportDto> getRevenueReportByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        FinancialReportDto report = reportService.getRevenueReportByMonth(year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/revenue/yearly")
    public ResponseEntity<FinancialReportDto> getRevenueReportByYear(
            @RequestParam int year) {
        FinancialReportDto report = reportService.getRevenueReportByYear(year);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/revenue/by-department/{departmentId}")
    public ResponseEntity<FinancialReportDto> getRevenueByDepartment(
            @PathVariable UUID departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        FinancialReportDto report = reportService.getRevenueByDepartment(departmentId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/revenue/by-doctor/{doctorId}")
    public ResponseEntity<FinancialReportDto> getRevenueByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        FinancialReportDto report = reportService.getRevenueByDoctor(doctorId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    // Pharmacy Reports
    @GetMapping("/pharmacy/low-stock")
    public ResponseEntity<List<PharmacyReportDto>> getLowStockDrugs() {
        List<PharmacyReportDto> report = reportService.getLowStockDrugs();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/pharmacy/expiring")
    public ResponseEntity<List<PharmacyReportDto>> getExpiringDrugs(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        List<PharmacyReportDto> report = reportService.getExpiringDrugs(days);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/pharmacy/out-of-stock")
    public ResponseEntity<List<PharmacyReportDto>> getOutOfStockDrugs() {
        List<PharmacyReportDto> report = reportService.getOutOfStockDrugs();
        return ResponseEntity.ok(report);
    }
}
