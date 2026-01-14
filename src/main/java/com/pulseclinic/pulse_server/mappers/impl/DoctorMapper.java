package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper implements Mapper<Doctor, DoctorDto> {
    private final ModelMapper modelMapper;
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    public DoctorMapper(ModelMapper modelMapper, StaffRepository staffRepository, StaffMapper staffMapper){
        this.modelMapper = modelMapper;
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    @Override
    public DoctorDto mapTo(Doctor doctor) {
        DoctorDto doctorDto = DoctorDto.builder()
                .id(doctor.getId())
                .licenseId(doctor.getLicenseId())
                .isVerified(doctor.getIsVerified())
                .createdAt(doctor.getCreatedAt())
                .averageRating(doctor.getAverageRating())
                .ratingCount(doctor.getRatingCount())
                .staffDto(doctor.getStaff() != null ? staffMapper.mapTo(doctor.getStaff()) : null)
                // Don't map department to avoid StackOverflow - accessible via staffDto.department if needed
                .departmentDto(null)
                .build();
        return doctorDto;
    }

    public DoctorDto mapToWithDepartment(Doctor doctor) {
        DoctorDto doctorDto = DoctorDto.builder()
                .id(doctor.getId())
                .licenseId(doctor.getLicenseId())
                .isVerified(doctor.getIsVerified())
                .createdAt(doctor.getCreatedAt())
                .averageRating(doctor.getAverageRating())
                .ratingCount(doctor.getRatingCount())
                .departmentDto(doctor.getDepartment() != null ?
                    DepartmentDto.builder()
                        .id(doctor.getDepartment().getId())
                        .name(doctor.getDepartment().getName())
                        .description(doctor.getDepartment().getDescription())
                        .createdAt(doctor.getDepartment().getCreatedAt())
                        .build()
                    : null)
                .staffDto(doctor.getStaff() != null ? staffMapper.mapTo(doctor.getStaff()) : null)
                .build();
        return doctorDto;
    }

    @Override
    public Doctor mapFrom(DoctorDto doctorDto) {
        return this.modelMapper.map(doctorDto, Doctor.class);
    }

    public Doctor mapFrom(DoctorRequestDto doctorRequestDto) {
        if (doctorRequestDto.getStaffId() == null) {
            throw new IllegalArgumentException("Staff ID cannot be null");
        }

        // Fetch the Staff entity from the database using staffId
        Staff staff = staffRepository.findById(doctorRequestDto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + doctorRequestDto.getStaffId()));

        // Build the Doctor entity manually
        return Doctor.builder()
                .licenseId(doctorRequestDto.getLicenseId())
                .isVerified(doctorRequestDto.getIsVerified())
                .staff(staff)
                .build();
    }
}
