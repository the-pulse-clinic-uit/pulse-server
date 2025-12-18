package com.pulseclinic.pulse_server.modules.ratings.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pulseclinic.pulse_server.modules.ratings.entity.StaffRating;

@Repository
public interface StaffRatingRepository extends JpaRepository<StaffRating, UUID> {
    List<StaffRating> findByStaffIdAndDeletedAtIsNull(UUID staffId);
}
