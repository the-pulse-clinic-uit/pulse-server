package com.pulseclinic.pulse_server.modules.staff.service.impl;

import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.billing.repository.InvoiceRepository;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentRequestDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentStatisticsDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.staff.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                 StaffRepository staffRepository,
                                 DoctorRepository doctorRepository,
                                 AppointmentRepository appointmentRepository,
                                 InvoiceRepository invoiceRepository) {
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Department create(Department department) {
        return this.departmentRepository.save(department);
    }

    @Override
    public Optional<Department> findById(UUID id) {
        return this.departmentRepository.findById(id);
    }

    @Override
    public List<Department> findAll() {
        return this.departmentRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        Optional<Department> department = this.departmentRepository.findById(id);
        if (department.isPresent()) {
            Department d = department.get();
            d.setDeletedAt(LocalDateTime.now());
            this.departmentRepository.save(d);
        }
        else throw new RuntimeException("Department not found");
    }

    @Override
    public Department update(UUID id, DepartmentDto departmentDto) {
        Optional<Department> department = this.departmentRepository.findById(id);
        if (department.isPresent()) {
            Department d = department.get();
            if (departmentDto.getName() != null) {
                d.setName(departmentDto.getName());
            }
            if (departmentDto.getDescription() != null) {
                d.setDescription(departmentDto.getDescription());
            }
            return this.departmentRepository.save(d);
        }
        throw new RuntimeException("Department not found");
    }

    @Override
    public List<Staff> findAllStaff(UUID id) {
        Department department = this.departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return department.getStaff();
    }

    @Override
    public Boolean assignStaff(UUID id, UUID staffId) {
        Department department = this.departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Staff staff = this.staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        staff.setDepartment(department);
        this.staffRepository.save(staff);
        return true;
    }

    @Override
    public Boolean unassignStaff(UUID id, UUID staffId) {
        Department department = this.departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Staff staff = this.staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (staff.getDepartment() == null || !staff.getDepartment().getId().equals(id)) {
            throw new RuntimeException("Staff is not assigned to this department");
        }

        staff.setDepartment(null);
        this.staffRepository.save(staff);
        return true;
    }

    @Override
    public DepartmentStatisticsDto getDepartmentStatistics(UUID id) {
        Department department = this.departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // count doctors
        Integer doctorCount = this.doctorRepository.countByDepartment(department);

        // count appointments for doctors
        Integer totalAppointments = this.appointmentRepository.countByDoctorDepartment(department);

        // sum revenue from invoices of encounters by doctors
        BigDecimal totalRevenue = this.invoiceRepository.sumTotalAmountByDoctorDepartment(department);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return DepartmentStatisticsDto.builder()
                .doctorCount(doctorCount)
                .totalRevenue(totalRevenue.doubleValue())
                .totalAppointments(totalAppointments)
                .build();
    }
}
