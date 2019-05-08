package com.sixfold.flight.service;

import lombok.Data;

@Data
public class Vertex {
    final private String name;

    public Vertex(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
