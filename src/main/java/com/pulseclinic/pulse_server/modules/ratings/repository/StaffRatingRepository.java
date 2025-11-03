package com.pulseclinic.pulse_server.modules.ratings.repository;

import com.pulseclinic.pulse_server.modules.ratings.entity.StaffRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaffRatingRepository extends JpaRepository<StaffRating, UUID> {
}
