package com.parking.controller;

import com.parking.model.ParkingLot;
import com.parking.service.ParkingLotService;
import com.parking.service.VehicleService; 
import com.parking.service.VehicleService.RevenueReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parking-lots")
@CrossOrigin
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private VehicleService vehicleService; 

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAllParkingLots() {
        return ResponseEntity.ok(parkingLotService.getAllParkingLots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getParkingLotById(@PathVariable Long id) {
        Optional<ParkingLot> parkingLot = parkingLotService.getParkingLotById(id);
        return parkingLot.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ParkingLot> createParkingLot(@RequestBody ParkingLot parkingLot) {
        ParkingLot createdLot = parkingLotService.createParkingLot(parkingLot);
        return new ResponseEntity<>(createdLot, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLot> updateParkingLot(@PathVariable Long id, @RequestBody ParkingLot updatedLot) {
        ParkingLot updated = parkingLotService.updateParkingLot(id, updatedLot);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable Long id) {
        parkingLotService.deleteParkingLot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report/occupancy")
    public ResponseEntity<List<OccupancyReport>> getOccupancyReport() {
        List<ParkingLot> allParkingLots = parkingLotService.getAllParkingLots();
        List<OccupancyReport> occupancyReports = allParkingLots.stream()
                .map(parkingLot -> new OccupancyReport(
                        parkingLot.getName(),
                        parkingLot.getCapacity(),
                        parkingLot.getAvailableSpaces(),
                        parkingLot.getCapacity() - parkingLot.getAvailableSpaces() // Espacios ocupados
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(occupancyReports);
    }

    @GetMapping("/report/revenue")
    public ResponseEntity<List<RevenueReport>> getRevenueReport(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<RevenueReport> revenueReports = vehicleService.getRevenueReport(startDate, endDate); // Llama al método del VehicleService
        return ResponseEntity.ok(revenueReports);
    }

    private static class OccupancyReport {
        private String name;
        private Integer capacity;
        private Integer availableSpaces;
        private Integer occupiedSpaces;

        public OccupancyReport(String name, Integer capacity, Integer availableSpaces, Integer occupiedSpaces) {
            this.name = name;
            this.capacity = capacity;
            this.availableSpaces = availableSpaces;
            this.occupiedSpaces = occupiedSpaces;
        }

        public String getName() {
            return name;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public Integer getAvailableSpaces() {
            return availableSpaces;
        }

        public Integer getOccupiedSpaces() {
            return occupiedSpaces;
        }
    }
}