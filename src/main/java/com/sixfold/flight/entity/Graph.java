package com.sixfold.flight.entity;

import lombok.Data;

import java.util.List;

@Data
public class Graph {
    private List<Vertex> vertexes;
    private List<Edge> edges;

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }
}
