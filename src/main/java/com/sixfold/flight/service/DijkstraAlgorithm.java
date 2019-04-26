package com.sixfold.flight.service;

import com.sixfold.flight.BusinessException;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Vertex> adjacentNodes = getNeighbors(node);
        adjacentNodes.stream().filter(target -> getShortestDistance(target) > getShortestDistance(node)
                + getDistance(node, target)).forEach(target -> {
            distance.put(target, getShortestDistance(node) + getDistance(node, target));
            predecessors.put(target, node);
            unSettledNodes.add(target);
        });

    }

    private double getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new BusinessException("Destination airport might be null");
    }

    private List<Vertex> getNeighbors(Vertex node) {
        return edges.stream().filter(edge -> edge.getSource().equals(node)
                && !isSettled(edge.getDestination())).map(Edge::getDestination).collect(Collectors.toList());
    }

    private Vertex getMinimum(Set<Vertex> vertexes) {
        return vertexes.stream().min(Comparator.comparingDouble(this::getShortestDistance)).orElse(null);
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
