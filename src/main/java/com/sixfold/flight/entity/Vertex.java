package com.sixfold.flight.entity;

import lombok.Data;

@Data
public class Vertex {
    private final String name;

    public Vertex(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
