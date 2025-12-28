package com.pulseclinic.pulse_server.modules.staff.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.mappers.impl.*;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import com.pulseclinic.pulse_server.modules.admissions.repository.AdmissionRepository;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionRepository;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftAssignmentRepository;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.staff.service.DoctorService;

import javax.swing.text.html.Option;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorMapper doctorMapper;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final ShiftAssignmentMapper shiftAssignmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final EncounterRepository encounterRepository;
    private final PatientMapper patientMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;
    private final StaffMapper staffMapper;
    private final UserMapper userMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             StaffRepository staffRepository,
                             DepartmentRepository departmentRepository,
                             DoctorMapper doctorMapper,
                             RoleRepository roleRepository,
                             ShiftAssignmentRepository shiftAssignmentRepository,
                             ShiftAssignmentMapper shiftAssignmentMapper,
                             AppointmentRepository appointmentRepository,
                             AppointmentMapper appointmentMapper,
                             EncounterRepository encounterRepository,
                             AdmissionRepository admissionRepository,
                             AdmissionMapper admissionMapper,
                             PrescriptionMapper prescriptionMapper,
                             PrescriptionRepository prescriptionRepository,
                             EncounterMapper encounterMapper,
                             PatientMapper patientMapper,
                             com.pulseclinic.pulse_server.modules.encounters.repository.FollowUpPlanRepository followUpPlanRepository,
                             FollowUpPlanMapper followUpPlanMapper, UserRepository userRepository, DepartmentMapper departmentMapper, StaffMapper staffMapper, UserMapper userMapper) {
        this.doctorRepository = doctorRepository;
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
        this.doctorMapper = doctorMapper;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.shiftAssignmentMapper = shiftAssignmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.encounterRepository = encounterRepository;
        this.patientMapper = patientMapper;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.departmentMapper = departmentMapper;
        this.staffMapper = staffMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public DoctorDto createDoctor(DoctorRequestDto doctorRequestDto) {
        // Kiểm tra license ID đã tồn tại
        if (doctorRepository.existsByLicenseId(doctorRequestDto.getLicenseId())) {
            throw new RuntimeException("License ID already exists");
        }

        // Tìm staff
        Optional<Staff> staffOpt = staffRepository.findById(doctorRequestDto.getStaffId());
        if (staffOpt.isEmpty()) {
            throw new RuntimeException("Staff not found");
        }

        // Kiểm tra staff đã là doctor chưa
        Optional<Doctor> existingDoctor = doctorRepository.findByStaffId(doctorRequestDto.getStaffId());
        if (existingDoctor.isPresent()) {
            throw new RuntimeException("Staff is already a doctor");
        }

        // Tìm department và gán cho staff
        Optional<Department> departmentOpt = departmentRepository.findById(doctorRequestDto.getDepartmentId());
        if (departmentOpt.isEmpty()) {
            throw new RuntimeException("Department not found");
        }

        Optional<Role> roleOpt = roleRepository.findByName("doctor");
        if (roleOpt.isEmpty()) {
            throw new RuntimeException("Role not found");
        }

        Staff staff = staffOpt.get();
        staff.setDepartment(departmentOpt.get());
        staff.setPosition(Position.DOCTOR);
        staffRepository.save(staff);

        Optional<User> userOpt = userRepository.findById(staff.getUser().getId());
        if(userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        user.setRole(roleOpt.get());

        Doctor doctor = Doctor.builder()
                .licenseId(doctorRequestDto.getLicenseId())
                .isVerified(Boolean.TRUE.equals(doctorRequestDto.getIsVerified()))
                .staff(staff)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        return this.mapTo(savedDoctor, user);
    }

    @Override
    @Transactional
    public DoctorDto updateDoctor(UUID doctorId, DoctorRequestDto doctorRequestDto) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Doctor doctor = doctorOpt.get();

        if (!doctor.getLicenseId().equals(doctorRequestDto.getLicenseId()) &&
            doctorRepository.existsByLicenseId(doctorRequestDto.getLicenseId())) {
            throw new RuntimeException("License ID already exists");
        }

        // Cập nhật thông tin cơ bản
        doctor.setLicenseId(doctorRequestDto.getLicenseId());
        if (doctorRequestDto.getIsVerified() != null) {
            doctor.setIsVerified(doctorRequestDto.getIsVerified());
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);
        return doctorMapper.mapTo(updatedDoctor);
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
            Staff staff = doctor.getStaff();
            if (staff == null) {
                return false;
            }

            staff.setDepartment(departmentOpt.get());
            staffRepository.save(staff);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getShiftSchedule(UUID doctorId, LocalDate date) {
        List<ShiftAssignment> assignments = shiftAssignmentRepository
                .findByDoctorIdAndDutyDate(doctorId, date);

        return assignments.stream()
                .map(shiftAssignmentMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(UUID doctorId, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        
        List<ShiftAssignment> assignments = shiftAssignmentRepository
                .findByDoctorIdAndDutyDate(doctorId, date);
        
        if (assignments.isEmpty()) {
            return false;
        }
        
        boolean isInShift = assignments.stream()
                .anyMatch(assignment -> {
                    LocalDateTime shiftStart = assignment.getShift().getStartTime();
                    LocalDateTime shiftEnd = assignment.getShift().getEndTime();
                    return !dateTime.isBefore(shiftStart) && !dateTime.isAfter(shiftEnd);
                });
        
        if (!isInShift) {
            return false;
        }

        LocalDateTime endTime = dateTime.plusMinutes(30);
        List<com.pulseclinic.pulse_server.modules.appointments.entity.Appointment> appointments = 
                appointmentRepository.findByDoctorIdAndStartsAtBetweenAndDeletedAtIsNull(
                        doctorId, dateTime.minusMinutes(30), endTime);
        
        return appointments.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getPatients(UUID doctorId) {
        List<Encounter> encounters = encounterRepository
                .findByDoctorIdAndDeletedAtIsNullOrderByStartedAtDesc(doctorId);

        return encounters.stream()
                .map(Encounter::getPatient)
                .distinct()
                .map(patientMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto getDoctorById(UUID doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }


        Doctor doctor = doctorOpt.get();
        Optional<User> userOpt = userRepository.findById(doctor.getStaff().getUser().getId());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        DoctorDto doctorDto = this.mapTo(doctor, userOpt.get());

        return doctorDto;
    }

    @Transactional(readOnly = true)
    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::mapToWithDepartment)
                .collect(Collectors.toList());
    }

    private DoctorDto mapTo(Doctor doctor, User user) {
        DoctorDto resultDto = doctorMapper.mapTo(doctor);

        resultDto.setDepartmentDto(departmentMapper.mapTo(doctor.getDepartment()));
        resultDto.setStaffDto(staffMapper.mapTo(doctor.getStaff()));
        resultDto.getStaffDto().setUserDto(userMapper.mapTo(user));

        return resultDto;
    }
}
