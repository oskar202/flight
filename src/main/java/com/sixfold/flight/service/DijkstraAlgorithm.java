package com.sixfold.flight.service;

import com.sixfold.flight.entity.Edge;
import com.sixfold.flight.entity.Graph;
import com.sixfold.flight.entity.Vertex;

import java.util.*;

import static java.util.Objects.requireNonNullElse;

class DijkstraAlgorithm {
    private final List<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Double> distance;

    DijkstraAlgorithm(Graph graph) {
        this.edges = new ArrayList<>(graph.getEdges());
    }

    void execute(Vertex source, Vertex destination) {
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(source, 0d);
        unSettledNodes.add(source);

        Optional<Edge> directRoute = edges.stream().filter(s -> s.getSource().equals(source)).filter(d -> d.getDestination().equals(destination)).findFirst();
        if (directRoute.isPresent()) {
            findShortestRoute();
        } else {
            while (!unSettledNodes.isEmpty()) {
                findShortestRoute();
            }
        }
    }

    private void findShortestRoute() {
        Vertex node = getMinimum(unSettledNodes);
        settledNodes.add(node);
        unSettledNodes.remove(node);
        getNeighbors(node);
    }

    private void getNeighbors(Vertex node) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && !settledNodes.contains((edge.getDestination())) && edge.getWeight() > 0) {
                double newDistance = getShortestDistance(node) + edge.getWeight();
                if (getShortestDistance(edge.getDestination()) > newDistance) {
                    distance.put(edge.getDestination(), newDistance);
                    predecessors.put(edge.getDestination(), node);
                    unSettledNodes.add(edge.getDestination());
                }
            }
        }
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

    private double getShortestDistance(Vertex destination) {
        return requireNonNullElse(distance.get(destination), Double.MAX_VALUE);
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
