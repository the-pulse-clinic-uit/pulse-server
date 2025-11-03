package com.pulseclinic.pulse_server.modules.users.repository;

import com.pulseclinic.pulse_server.modules.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
