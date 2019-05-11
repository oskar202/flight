package com.sixfold.flight.service;

import com.sixfold.flight.entity.Airport;
import com.sixfold.flight.repository.AirportRepository;
import org.springframework.stereotype.Service;

import static java.lang.Math.*;

@Service
public class DistanceService {

    private final AirportRepository airportRepository;

    public DistanceService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public double distanceInKilometers(String start, String end) {
        Airport departure = airportRepository.getAirportByIata(start);
        Airport destination = airportRepository.getAirportByIata(end);
        if (departure == null || destination == null)
            return 0;
        double lat1 = departure.getLatitude();
        double lon1 = departure.getLongitude();
        double lat2 = destination.getLatitude();
        double lon2 = destination.getLongitude();

        double theta = lon1 - lon2;
        double dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta));
        dist = acos(dist);
        dist = rad2deg(dist);
        double distanceInNauticalMile = dist * 60;

        return distanceInNauticalMile * 1.852;
    }

    private double deg2rad(double deg) {
        return (deg * PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / PI);
    }

}
