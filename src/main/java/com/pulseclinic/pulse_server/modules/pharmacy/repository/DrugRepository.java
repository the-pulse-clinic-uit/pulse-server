package com.pulseclinic.pulse_server.modules.pharmacy.repository;

import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrugRepository extends JpaRepository<Drug, UUID> {
    @Query("SELECT d FROM Drug d WHERE d.deletedAt IS NULL")
    List<Drug> findAll();
}
