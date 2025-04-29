package com.parking.service;

import com.parking.model.ParkingLot;
import com.parking.repository.ParkingLotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    public Optional<ParkingLot> getParkingLotById(Long id) {
        return parkingLotRepository.findById(id);
    }

    public ParkingLot createParkingLot(ParkingLot parkingLot) {
        parkingLot.setAvailableSpaces(parkingLot.getCapacity());
        return parkingLotRepository.save(parkingLot);
    }

    public ParkingLot updateParkingLot(Long id, ParkingLot updatedLot) {
        return parkingLotRepository.findById(id)
                .map(parkingLot -> {
                    parkingLot.setName(updatedLot.getName());
                    parkingLot.setCapacity(updatedLot.getCapacity());
                    return parkingLotRepository.save(parkingLot);
                })
                .orElse(null);
    }

    public void deleteParkingLot(Long id) {
        parkingLotRepository.deleteById(id);
    }

    // Método para decrementar los espacios disponibles
    public boolean decrementAvailableSpaces(Long parkingLotId) {
        Optional<ParkingLot> parkingLotOptional = parkingLotRepository.findById(parkingLotId);
        return parkingLotOptional.map(parkingLot -> {
            if (parkingLot.getAvailableSpaces() > 0) {
                parkingLot.setAvailableSpaces(parkingLot.getAvailableSpaces() - 1);
                parkingLotRepository.save(parkingLot);
                return true;
            }
            return false; // No hay espacios disponibles
        }).orElse(false); // El parqueadero no existe
    }

    public void incrementAvailableSpaces(Long parkingLotId) {
        parkingLotRepository.findById(parkingLotId)
                .ifPresent(parkingLot -> {
                    if (parkingLot.getAvailableSpaces() < parkingLot.getCapacity()) {
                        parkingLot.setAvailableSpaces(parkingLot.getAvailableSpaces() + 1);
                        parkingLotRepository.save(parkingLot);
                    }
                });
    }
}