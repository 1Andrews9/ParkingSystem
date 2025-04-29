package com.parking.repository;

import com.parking.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByActiveTrue();
    List<Vehicle> findByExitTimeIsNotNull();
    List<Vehicle> findByEntryTimeGreaterThanEqualAndExitTimeLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);
    List<Vehicle> findByExitTimeGreaterThanEqualAndExitTimeLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);
}