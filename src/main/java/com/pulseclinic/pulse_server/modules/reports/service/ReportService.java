package com.pulseclinic.pulse_server.modules.reports.service;

import com.pulseclinic.pulse_server.modules.reports.dto.AppointmentReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.FinancialReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.PatientReportDto;
import com.pulseclinic.pulse_server.modules.reports.dto.PharmacyReportDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    // Patient Reports
    PatientReportDto getPatientReportByDate(LocalDate date);
    List<PatientReportDto> getPatientReportByRange(LocalDate startDate, LocalDate endDate);

    // Appointment Reports
    AppointmentReportDto getAppointmentReportByDate(LocalDate date);
    List<AppointmentReportDto> getAppointmentReportByRange(LocalDate startDate, LocalDate endDate);
    AppointmentReportDto getAppointmentReportByDoctor(UUID doctorId, LocalDate startDate, LocalDate endDate);
    AppointmentReportDto getAppointmentReportByDepartment(UUID departmentId, LocalDate startDate, LocalDate endDate);

    // Financial Reports
    FinancialReportDto getRevenueReportByDate(LocalDate date);
    FinancialReportDto getRevenueReportByMonth(int year, int month);
    FinancialReportDto getRevenueReportByYear(int year);
    FinancialReportDto getRevenueByDepartment(UUID departmentId, LocalDate startDate, LocalDate endDate);
    FinancialReportDto getRevenueByDoctor(UUID doctorId, LocalDate startDate, LocalDate endDate);

    // Pharmacy Reports
    List<PharmacyReportDto> getLowStockDrugs();
    List<PharmacyReportDto> getExpiringDrugs(Integer days);
    List<PharmacyReportDto> getOutOfStockDrugs();
}
