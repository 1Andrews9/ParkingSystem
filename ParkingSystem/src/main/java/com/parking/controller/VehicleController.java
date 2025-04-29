package com.parking.controller;

import com.parking.model.Vehicle;
import com.parking.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/entry/{parkingLotId}")
    public ResponseEntity<Vehicle> registerEntry(@PathVariable Long parkingLotId, @RequestBody Vehicle vehicle) {
        Vehicle registeredVehicle = vehicleService.registerEntry(parkingLotId, vehicle);
        if (registeredVehicle != null) {
            return new ResponseEntity<>(registeredVehicle, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().body(null); 
        }
    }

    @PostMapping("/exit/{id}")
    public ResponseEntity<Vehicle> registerExit(@PathVariable Long id) {
        Vehicle exitedVehicle = vehicleService.registerExit(id);
        return exitedVehicle != null ? ResponseEntity.ok(exitedVehicle) : ResponseEntity.notFound().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Vehicle>> getActiveVehicles() {
        return ResponseEntity.ok(vehicleService.getActiveVehicles());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/available-spaces")
    public ResponseEntity<Long> getAvailableSpaces() {
        return ResponseEntity.ok(vehicleService.getAvailableSpaces());
    }

    @GetMapping("/report/history")
    public ResponseEntity<List<Vehicle>> getVehicleHistory(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Vehicle> vehicleHistory = vehicleService.getVehicleHistory(startDate, endDate);
        return ResponseEntity.ok(vehicleHistory);
    }

    @GetMapping("/report/revenue")
    public ResponseEntity<List<VehicleService.RevenueReport>> getRevenueReport(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<VehicleService.RevenueReport> revenueReports = vehicleService.getRevenueReport(startDate, endDate);
        return ResponseEntity.ok(revenueReports);
    }
}