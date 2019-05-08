package com.sixfold.flight.service;

import com.sixfold.flight.controller.FlightResponseDto;
import com.sixfold.flight.entity.Airport;
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

    public FlightService(RouteRepository routeRepository, AirportRepository airportRepository) {
        this.routeRepository = routeRepository;
        this.airportRepository = airportRepository;
    }

    public FlightResponseDto findShortestRoute(String start, String end) {
        List<Edge> edges = routeRepository.getAllRoutes(start);
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
        dijkstra.execute(vertexStart);
        LinkedList<Vertex> path = dijkstra.getPath(vertexEnd);

        validateResultPath(path);
        List<Airport> allAirports = airportRepository.getAllAirports();
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

    public double calculateTotalDistance(LinkedList<Vertex> path) {
        return IntStream.range(0, path.size() - 1)
                .mapToDouble(p -> distanceInKilometers(path.get(p).getName(), path.get(p + 1).getName()))
                .sum();
    }

    private Map<LinkedList<Vertex>, Double> findAlternativePaths(List<Airport> allAirports, String end, LinkedList<Vertex> path) {
        Map<LinkedList<Vertex>, Double> alternativePaths = new HashMap<>();

        allAirports.forEach(airport -> {
            double distanceToNeigbours = distanceInKilometers(end, airport.getIATA());
            if (distanceToNeigbours <= 100 && !end.equals(airport.getIATA())) {
                LinkedList<Vertex> path2 = (LinkedList<Vertex>) path.clone();
                path2.removeLast();
                path2.add(new Vertex(airport.getIATA()));
                alternativePaths.put(path2, calculateTotalDistance(path2));
            }
        });
        return alternativePaths;
    }

    private double distanceInKilometers(String start, String end) {
        Airport departure = airportRepository.getAirportByIata(start);
        Airport destination = airportRepository.getAirportByIata(end);
        if (departure == null || destination == null)
            return 0;
        double lat1 = departure.getLatitude();
        double lon1 = departure.getLongitude();
        double lat2 = destination.getLatitude();
        double lon2 = destination.getLongitude();

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        double distanceInNauticalMile = dist * 60;

        return distanceInNauticalMile * 1.852;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}