package com.pulseclinic.pulse_server.modules.staff.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.mappers.impl.DoctorMapper;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.staff.service.DoctorService;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                            StaffRepository staffRepository,
                            DepartmentRepository departmentRepository,
                            DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
        this.doctorMapper = doctorMapper;
    }

    @Override
    @Transactional
    public DoctorDto createDoctor(DoctorRequestDto doctorRequestDto) {
        // Kiểm tra license ID đã tồn tại
        if (doctorRepository.existsByLicenseId(doctorRequestDto.getLicense_id())) {
            throw new RuntimeException("License ID already exists");
        }

        // Tìm staff
        Optional<Staff> staffOpt = staffRepository.findById(doctorRequestDto.getStaff_id());
        if (staffOpt.isEmpty()) {
            throw new RuntimeException("Staff not found");
        }

        // Kiểm tra staff đã là doctor chưa
        Optional<Doctor> existingDoctor = doctorRepository.findByStaffId(doctorRequestDto.getStaff_id());
        if (existingDoctor.isPresent()) {
            throw new RuntimeException("Staff is already a doctor");
        }

        // Tìm department
        Optional<Department> departmentOpt = departmentRepository.findById(doctorRequestDto.getDepartment_id());
        if (departmentOpt.isEmpty()) {
            throw new RuntimeException("Department not found");
        }

        Doctor doctor = Doctor.builder()
                .license_id(doctorRequestDto.getLicense_id())
                .is_verified(Boolean.TRUE.equals(doctorRequestDto.getIs_verified()))
                .staff(staffOpt.get())
                .department(departmentOpt.get())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.mapTo(savedDoctor);
    }

    @Override
    @Transactional
    public boolean updateSpecialization(UUID doctorId, UUID departmentId) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            Optional<Department> departmentOpt = departmentRepository.findById(departmentId);

            if (doctorOpt.isEmpty() || departmentOpt.isEmpty()) {
                return false;
            }

            Doctor doctor = doctorOpt.get();
            doctor.setDepartment(departmentOpt.get());
            doctorRepository.save(doctor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean verifyLicense(UUID doctorId) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return false;
            }

            Doctor doctor = doctorOpt.get();
            doctor.setIs_verified(true);
            doctorRepository.save(doctor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkLicenseValidity(UUID doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return false;
        }

        Doctor doctor = doctorOpt.get();
        return doctor.getIs_verified() != null && doctor.getIs_verified();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getAppointments(UUID doctorId, LocalDate date) {
        // TODO: Implement logic lấy các cuộc hẹn cho ngày cụ thể
        // Cần kết hợp với module appointments
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getUpcomingAppointments(UUID doctorId) {
        // TODO: Implement logic lấy các cuộc hẹn sắp tới
        // Cần kết hợp với module appointments
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getEncounters(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        // TODO: Implement logic lấy các bệnh án trong khoảng thời gian
        // Cần kết hợp với module encounters
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public Object prescribeMedication(UUID doctorId, UUID encounterId) {
        // TODO: Implement logic tạo đơn thuốc cho bệnh án
        // Cần kết hợp với module pharmacy
        return null;
    }

    @Override
    @Transactional
    public boolean recordDiagnosis(UUID doctorId, UUID encounterId, String diagnosis) {
        // TODO: Implement logic ghi lại chẩn đoán cho bệnh án
        // Cần kết hợp với module encounters
        return false;
    }

    @Override
    @Transactional
    public Object createFollowUpPlan(UUID doctorId, UUID encounterId) {
        // TODO: Implement logic tạo kế hoạch tái khám
        // Cần kết hợp với module encounters
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getShiftSchedule(UUID doctorId, LocalDate date) {
        // TODO: Implement logic lấy lịch ca làm việc cho ngày cụ thể
        // Cần kết hợp với module scheduling
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(UUID doctorId, LocalDateTime dateTime) {
        // TODO: Implement logic kiểm tra bác sĩ có sẵn sàng hay không
        // Cần kết hợp với module scheduling và appointments
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Object> getDepartment(UUID doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return Optional.empty();
        }

        Doctor doctor = doctorOpt.get();
        return Optional.ofNullable(doctor.getDepartment());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getPatients(UUID doctorId) {
        // TODO: Implement logic lấy tất cả bệnh nhân đã được bác sĩ khám
        // Cần kết hợp với module encounters
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getActiveAdmissions(UUID doctorId) {
        // TODO: Implement logic lấy bệnh nhân hiện đang nhập viện dưới quyền bác sĩ
        // Cần kết hợp với module admissions
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorDto> getDoctorById(UUID doctorId) {
        return doctorRepository.findById(doctorId)
                .map(doctorMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> findAll() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> findByDepartment(UUID departmentId) {
        return doctorRepository.findByDepartmentId(departmentId).stream()
                .map(doctorMapper::mapTo)
                .collect(Collectors.toList());
    }
}
