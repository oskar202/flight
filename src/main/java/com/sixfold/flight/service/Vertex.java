package com.sixfold.flight.service;

import lombok.Data;

@Data
public class Vertex {
    private String id;
    private String name;

    public Vertex(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
