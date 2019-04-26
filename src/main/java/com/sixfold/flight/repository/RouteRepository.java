package com.sixfold.flight.repository;

import com.sixfold.flight.entity.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

@Repository
@Slf4j
public class RouteRepository {
    private static long routeSeq = 0;
    private Map<Long, Route> database = new HashMap<>();

    public RouteRepository() {
        String fileName = "src/main/resources/routes.txt";

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(x -> {
                routeSeq++;
                Route route = new Route();
                String[] splitted = x.replaceAll("\\\\N", "0").split(",");
                route.setSourceAirportIata(splitted[2]);
                route.setSourceAirportId(parseInt(splitted[3]));
                route.setDestinationAirportIata(splitted[4]);
                route.setDestinationAirportId(parseInt(splitted[5]));
                route.setStops(parseInt(splitted[7]));
                if (route.getSourceAirportIata() != null && route.getDestinationAirportIata() != null) {
                    database.put(routeSeq, route);
                }
            });
        } catch (IOException e) {
            log.error("Error reading file: ", e);
        }
    }

    public Set<Route> getAllUniqueRoutes() {
        return new HashSet<>(database.values());
    }

    public List<Route> getAllRoutes() {
        return new ArrayList<>(database.values());
    }
}
