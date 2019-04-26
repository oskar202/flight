package com.sixfold.flight.repository;

import com.sixfold.flight.entity.Airport;
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

import static java.lang.Double.parseDouble;

@Repository
@Slf4j
public class AirportRepository {
    private Map<Long, Airport> database = new HashMap<>();
    private static long airportSeq = 0;

    public AirportRepository() {
        String fileName = "src/main/resources/airports.txt";

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(x -> {
                airportSeq++;
                Airport airport = new Airport();
                String[] splitted = x.replaceAll("\\\\N", "0").split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                airport.setName(splitted[1]);
                airport.setIATA(splitted[4].replaceAll("\"", ""));
                airport.setLatitude(parseDouble(splitted[6]));
                airport.setLongitude(parseDouble(splitted[7]));

                database.put(airportSeq, airport);
            });
        } catch (IOException e) {
            log.error("Error reading file: ", e);
        }
    }

    public Airport getAirportByIata(String iata) {
        return database.values().stream().filter(a -> a.getIATA().equals(iata)).findFirst().orElse(null);
    }

    public List<Airport> getAllAirports() {
        return new ArrayList<>(database.values());
    }
}
