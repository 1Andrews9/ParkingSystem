package com.parking.service;

import com.parking.model.ParkingLot;
import com.parking.model.Vehicle;
import com.parking.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ParkingLotService parkingLotService;

    private static final double RATE_PER_HOUR = 3000.0; 

    public Vehicle registerEntry(Long parkingLotId, Vehicle vehicle) {
        System.out.println("Intentando registrar entrada para el vehículo: " + vehicle.getLicensePlate() + " en el parqueadero ID: " + parkingLotId);
        Optional<ParkingLot> parkingLotOptional = parkingLotService.getParkingLotById(parkingLotId);
        if (parkingLotOptional.isPresent()) {
            ParkingLot parkingLot = parkingLotOptional.get();
            System.out.println("Parqueadero encontrado: " + parkingLot.getName() + ", Espacios disponibles: " + parkingLot.getAvailableSpaces());
            if (parkingLotService.decrementAvailableSpaces(parkingLotId)) {
                vehicle.setEntryTime(LocalDateTime.now());
                vehicle.setActive(true);
                vehicle.setParkingLot(parkingLot);
                Vehicle savedVehicle = vehicleRepository.save(vehicle);
                System.out.println("Vehículo registrado con ID: " + savedVehicle.getId());
                return savedVehicle;
            } else {
                System.out.println("No hay espacios disponibles en el parqueadero ID: " + parkingLotId);
                return null; 
            }
        } else {
            System.out.println("No se encontró el parqueadero con ID: " + parkingLotId);
            return null; 
        }
    }

    public Vehicle registerExit(Long vehicleId) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            vehicle.setExitTime(LocalDateTime.now());

            Duration duration = Duration.between(vehicle.getEntryTime(), vehicle.getExitTime());
            long hours = duration.toHours();
            if (hours == 0) hours = 1;

            vehicle.setAmountCharged((double) hours * RATE_PER_HOUR); 
            vehicle.setActive(false);
            vehicleRepository.save(vehicle);

            if (vehicle.getParkingLot() != null) {
                parkingLotService.incrementAvailableSpaces(vehicle.getParkingLot().getId());
            }
            return vehicle;
        }
        return null; 
    }

    public List<Vehicle> getActiveVehicles() {
        return vehicleRepository.findByActiveTrue();
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> getVehicleHistory(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return vehicleRepository.findByEntryTimeGreaterThanEqualAndExitTimeLessThanEqual(startDate, endDate);
        } else {
            return vehicleRepository.findByExitTimeIsNotNull(); 
        }
    }

    public List<RevenueReport> getRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Vehicle> exitedVehicles;
        if (startDate != null && endDate != null) {
            exitedVehicles = vehicleRepository.findByExitTimeGreaterThanEqualAndExitTimeLessThanEqual(startDate, endDate);
        } else {
            exitedVehicles = vehicleRepository.findByExitTimeIsNotNull();
        }

        return exitedVehicles.stream()
                .filter(vehicle -> vehicle.getParkingLot() != null && vehicle.getAmountCharged() != null)
                .collect(Collectors.groupingBy(
                        vehicle -> vehicle.getParkingLot().getName(),
                        Collectors.summingDouble(Vehicle::getAmountCharged)
                ))
                .entrySet().stream()
                .map(entry -> new RevenueReport(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public long getAvailableSpaces() {
        return parkingLotService.getAllParkingLots().stream()
                .mapToInt(ParkingLot::getAvailableSpaces)
                .sum();
    }

    public static class RevenueReport {
        private String parkingLotName;
        private Double totalRevenue;

        public RevenueReport(String parkingLotName, Double totalRevenue) {
            this.parkingLotName = parkingLotName;
            this.totalRevenue = totalRevenue;
        }

        public String getParkingLotName() {
            return parkingLotName;
        }

        public Double getTotalRevenue() {
            return totalRevenue;
        }
    }
}