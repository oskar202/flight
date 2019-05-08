package com.sixfold.flight.entity;

import lombok.Data;

@Data
public class Route {
    private String sourceAirportIata;
    private String destinationAirportIata;
    private int stops;
    private double distance;
}
