package com.sixfold.flight.service;

import lombok.Data;

@Data
class Edge {
    private String id;
    private Vertex source;
    private Vertex destination;
    private double weight;

    Edge(String id, Vertex source, Vertex destination, double weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
}
