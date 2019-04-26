package com.sixfold.flight.entity;

import lombok.Data;

@Data
public class Route {
    private String sourceAirportIata;
    private int sourceAirportId;
    private String destinationAirportIata;
    private int destinationAirportId;
    private int stops;
    private double distance;
}
