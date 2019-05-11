package com.sixfold.flight.service;

import com.sixfold.flight.controller.FlightResponseDto;
import com.sixfold.flight.entity.Airport;
import com.sixfold.flight.entity.Edge;
import com.sixfold.flight.entity.Graph;
import com.sixfold.flight.entity.Vertex;
import com.sixfold.flight.exception.BusinessException;
import com.sixfold.flight.repository.AirportRepository;
import com.sixfold.flight.repository.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class FlightService {
    private RouteRepository routeRepository;
    private AirportRepository airportRepository;
    private DistanceService distanceService;

    public FlightService(RouteRepository routeRepository, AirportRepository airportRepository, DistanceService distanceService) {
        this.routeRepository = routeRepository;
        this.airportRepository = airportRepository;
        this.distanceService = distanceService;
    }

    public FlightResponseDto findShortestRoute(String start, String end) {
        List<Edge> edges = routeRepository.getAllRoutes();
        List<Vertex> nodes = new ArrayList<>();

        Set<Vertex> destination = edges.stream()
                .map(u -> u.getDestination().getName())
                .map(Vertex::new)
                .collect(Collectors.toSet());

        destination.stream()
                .map(uniqueRoute -> new Vertex(String.valueOf(uniqueRoute.getName())))
                .forEach(nodes::add);

        Vertex vertexStart = getVertexByIata(nodes, start);
        Vertex vertexEnd = getVertexByIata(nodes, end);

        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        log.info("Executing dijkstra algorithm");
        dijkstra.execute(vertexStart, vertexEnd);
        LinkedList<Vertex> path = dijkstra.getPath(vertexEnd);

        validateResultPath(path);
        List<Airport> allAirports = airportRepository.getAllAirports();
        log.info("Finished calculating shortest route");
        return new FlightResponseDto(path, calculateTotalDistance(path), findAlternativePaths(allAirports, end, path));
    }

    private Vertex getVertexByIata(List<Vertex> nodes, String iata) {
        return nodes.stream().filter(v -> v.getName().equals(iata)).findFirst().orElse(null);
    }

    private void validateResultPath(LinkedList<Vertex> path) {
        if (path.isEmpty()) {
            throw new BusinessException("Route or airport does not exist");
        } else if (path.size() > 4) {
            throw new BusinessException("Route is too long: " + path.size());
        }
    }

    public double calculateTotalDistance(List<Vertex> path) {
        return IntStream.range(0, path.size() - 1)
                .mapToDouble(p -> distanceService.distanceInKilometers(path.get(p).getName(), path.get(p + 1).getName()))
                .sum();
    }

    private Map<LinkedList<Vertex>, Double> findAlternativePaths(List<Airport> allAirports, String end, LinkedList<Vertex> path) {
        Map<LinkedList<Vertex>, Double> alternativePaths = new HashMap<>();

        allAirports.forEach(airport -> {
            double distanceToNeigbours = distanceService.distanceInKilometers(end, airport.getIata());
            if (distanceToNeigbours <= 100 && !end.equals(airport.getIata())) {
                LinkedList<Vertex> path2 = (LinkedList<Vertex>) path.clone();
                path2.removeLast();
                path2.add(new Vertex(airport.getIata()));
                alternativePaths.put(path2, calculateTotalDistance(path2));
            }
        });
        return alternativePaths;
    }
}