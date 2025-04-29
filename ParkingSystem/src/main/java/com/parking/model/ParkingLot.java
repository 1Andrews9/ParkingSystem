package com.parking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer capacity;

    private Integer availableSpaces = 0; 

    public ParkingLot(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
        this.availableSpaces = capacity;
    }
}