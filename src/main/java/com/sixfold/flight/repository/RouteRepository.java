package com.sixfold.flight.repository;

import com.sixfold.flight.entity.Airport;
import com.sixfold.flight.service.Edge;
import com.sixfold.flight.service.Vertex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
@Slf4j
public class RouteRepository {
    private static long routeSeq = 0;
    private Map<Long, Edge> database = new HashMap<>();
    private final AirportRepository airportRepository;

    public RouteRepository(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
        String fileName = "src/main/resources/routes.txt";

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(x -> {
                routeSeq++;
                Edge route = new Edge();
                String[] splitted = x.replaceAll("\\\\N", "0").split(",");
                route.setSource(new Vertex(splitted[2]));
                route.setDestination(new Vertex(splitted[4]));
                route.setWeight(distanceInKilometers(route.getSource().getName(), route.getDestination().getName()));
                if (route.getSource() != null && route.getDestination() != null && route.getWeight() > 0) {
                    database.put(routeSeq, route);
                }
            });
        } catch (IOException e) {
            log.error("Error reading file: ", e);
        }
    }


    public List<Edge> getAllRoutes(String source) {
        return new ArrayList<>(database.values());
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
