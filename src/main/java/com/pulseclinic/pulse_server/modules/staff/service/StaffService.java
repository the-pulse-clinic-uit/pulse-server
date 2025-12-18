package com.pulseclinic.pulse_server.modules.staff.service;

import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffService {
    Staff createStaff(StaffRequestDto staffRequestDto);

    List<Staff> searchByPosition(Position pos);

    Optional<Staff> findById (@PathVariable UUID id);
    Optional<Staff> findByEmail (String email);

    Staff update(UUID id, StaffDto staffDto);
    Staff updateMe(String email, StaffDto staffDto);
}
