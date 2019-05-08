package com.sixfold.flight.service;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public
class Edge {
    private Vertex source;
    private Vertex destination;
    private double weight;
}
