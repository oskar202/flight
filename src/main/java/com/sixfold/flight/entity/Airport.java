package com.sixfold.flight.entity;

import lombok.Data;

@Data
public class Airport {
    private int airportId;
    private String name;
    private String IATA;
    private double latitude;
    private double longitude;
}
