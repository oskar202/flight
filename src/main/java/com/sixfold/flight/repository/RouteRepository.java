package com.sixfold.flight.repository;

import com.sixfold.flight.entity.Edge;
import com.sixfold.flight.entity.Vertex;
import com.sixfold.flight.service.DistanceService;
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

    public RouteRepository(DistanceService distanceService) {
        String fileName = "src/main/resources/routes.txt";

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(x -> {
                routeSeq++;
                Edge route = new Edge();
                String[] splitted = x.replaceAll("\\\\N", "0").split(",");
                route.setSource(new Vertex(splitted[2]));
                route.setDestination(new Vertex(splitted[4]));
                route.setWeight(distanceService.distanceInKilometers(route.getSource().getName(), route.getDestination().getName()));
                if (route.getSource() != null && route.getDestination() != null && route.getWeight() > 0) {
                    database.put(routeSeq, route);
                }
            });
        } catch (IOException e) {
            log.error("Error reading file: ", e);
        }
    }

    public List<Edge> getAllRoutes() {
        return new ArrayList<>(database.values());
    }


}
