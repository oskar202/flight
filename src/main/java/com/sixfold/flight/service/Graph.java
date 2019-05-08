package com.sixfold.flight.service;

import lombok.Data;

import java.util.List;

@Data
class Graph {
    private List<Vertex> vertexes;
    private List<Edge> edges;

    Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }
}
