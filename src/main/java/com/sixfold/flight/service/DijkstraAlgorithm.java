package com.sixfold.flight.service;

import com.sixfold.flight.repository.AirportRepository;

import java.util.*;

class DijkstraAlgorithm {
    private final List<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Double> distance;

    DijkstraAlgorithm(Graph graph) {
        this.edges = new ArrayList<>(graph.getEdges());
    }

    void execute(Vertex source) {
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(source, 0d);
        unSettledNodes.add(source);
        while (!unSettledNodes.isEmpty()) {
            Vertex node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Vertex node) {
        Map<Vertex, Double> adjacentNodes = getNeighbors(node);
        adjacentNodes.forEach((target, weight) -> {
            if (weight > 0) {
                double newDistance = getShortestDistance(node) + weight;
                if (getShortestDistance(target) > newDistance) {
                    distance.put(target, newDistance);
                    predecessors.put(target, node);
                    unSettledNodes.add(target);
                }
            }
        });
    }

    private Map<Vertex, Double> getNeighbors(Vertex node) {
        Map<Vertex, Double> list = new HashMap<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) {
                Vertex destination = edge.getDestination();
                list.put(destination, edge.getWeight());
            }
        }
        return list;
    }

    private Vertex getMinimum(Set<Vertex> vertexes) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
            if (minimum == null || getShortestDistance(vertex) < getShortestDistance(minimum)) {
                minimum = vertex;
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    }

    private double getShortestDistance(Vertex destination) {
        return Objects.requireNonNullElse(distance.get(destination), Double.MAX_VALUE);
    }

    LinkedList<Vertex> getPath(Vertex step) {
        LinkedList<Vertex> path = new LinkedList<>();
        if (predecessors.get(step) == null) {
            return path;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }
}
