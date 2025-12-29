package com.pulseclinic.pulse_server.modules.staff.service.impl;

import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.staff.service.StaffService;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public StaffServiceImpl(StaffRepository staffRepository,
                            UserRepository userRepository,
                            DepartmentRepository departmentRepository) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Staff createStaff(StaffRequestDto staffRequestDto) {
        User user = this.userRepository.findById(staffRequestDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Department department = null;
        if (staffRequestDto.getDepartmentId() != null) {
            department = this.departmentRepository.findById(staffRequestDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Staff staff = Staff.builder()
                .position(staffRequestDto.getPosition())
                .user(user)
                .department(department)
                .build();
        return this.staffRepository.save(staff);
    }

    @Override
    public List<Staff> searchByPosition(Position position) {
        return this.staffRepository.findByPosition(position);
    }

    @Override
    public Optional<Staff> findById(UUID id) {
        return this.staffRepository.findById(id);
    }

    @Override
    public Optional<Staff> findByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isPresent()){
            return this.staffRepository.findByUser(user.get());
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public Staff update(UUID id, StaffDto staffDto) {
        Staff staff = this.staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Staff not found"));
        if (staffDto.getPosition() != null) {
            staff.setPosition(staffDto.getPosition());
        }
        if (staffDto.getUserDto() != null) {
            if (staffDto.getUserDto().getId() != null) {
                User user = this.userRepository.findById(staffDto.getUserDto().getId()).orElseThrow(() -> new RuntimeException("User not found"));
                staff.setUser(user);
            }
            else if (staffDto.getUserDto().getEmail() != null) {
                User user = this.userRepository.findByEmail(staffDto.getUserDto().getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
                staff.setUser(user);
            }
        }
        return this.staffRepository.save(staff);
    }

    @Override
    public Staff updateMe(String email, StaffDto staffDto) {
        Staff staff = this.findByEmail(email).orElseThrow(() -> new RuntimeException("Staff not found"));
        if (staffDto.getPosition() != null) {
            staff.setPosition(staffDto.getPosition());
        }
        if (staffDto.getUserDto() != null) {
            if (staffDto.getUserDto().getId() != null) {
                User user = this.userRepository.findById(staffDto.getUserDto().getId()).orElseThrow(() -> new RuntimeException("User not found"));
                staff.setUser(user);
            }
            else if (staffDto.getUserDto().getEmail() != null) {
                User user = this.userRepository.findByEmail(staffDto.getUserDto().getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
                staff.setUser(user);
            }
        }
        return this.staffRepository.save(staff);
    }
}
