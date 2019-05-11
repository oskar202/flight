package com.sixfold.flight.controller;

import com.sixfold.flight.entity.Vertex;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class FlightResponseDto {
    private final List<Vertex> path;
    private final double distance;
    private final Map<LinkedList<Vertex>, Double> alternativePaths;

    public FlightResponseDto(List<Vertex> path, double distance, Map<LinkedList<Vertex>, Double> alternativePaths) {
        this.path = path;
        this.distance = distance;
        this.alternativePaths = alternativePaths;
    }
}
