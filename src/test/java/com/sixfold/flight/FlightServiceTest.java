package com.sixfold.flight;

import com.sixfold.flight.controller.FlightResponseDto;
import com.sixfold.flight.entity.Airport;
import com.sixfold.flight.entity.Vertex;
import com.sixfold.flight.exception.BusinessException;
import com.sixfold.flight.repository.AirportRepository;
import com.sixfold.flight.repository.RouteRepository;
import com.sixfold.flight.service.DistanceService;
import com.sixfold.flight.service.FlightService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FlightServiceTest {
    private final AirportRepository airportRepository = mock(AirportRepository.class);

    private final RouteRepository routeRepository = mock(RouteRepository.class);
    private final DistanceService distanceService = new DistanceService(airportRepository);
    private final FlightService flightService = new FlightService(routeRepository, airportRepository, distanceService);

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void distance() {
        Airport tallinnAirport = new Airport();
        Airport rigaAirport = new Airport();
        tallinnAirport.setLatitude(59.436962);
        tallinnAirport.setLongitude(24.753574);
        rigaAirport.setLatitude(56.949650);
        rigaAirport.setLongitude(24.105186);

        LinkedList<Vertex> path = new LinkedList<Vertex>();
        path.add(new Vertex("TLL"));
        path.add(new Vertex("RIX"));

        when(airportRepository.getAirportByIata("TLL")).thenReturn(tallinnAirport);
        when(airportRepository.getAirportByIata("RIX")).thenReturn(rigaAirport);

        double distance = flightService.calculateTotalDistance(path);

        assertThat(Math.round(distance)).isEqualTo(279);
    }

    @Test
    public void findShortestPath_success() {
        AirportRepository airportRepository = new AirportRepository();
        DistanceService distanceService = new DistanceService(airportRepository);
        RouteRepository routeRepository = new RouteRepository(distanceService);
        FlightService flightService = new FlightService(routeRepository, airportRepository, distanceService);

        FlightResponseDto flightResponseDto = flightService.findShortestRoute("TLL", "RIX");

        assertThat(flightResponseDto.getPath()).isNotNull();
    }

    @Test
    public void airportDoesNotExist() {
        expected.expect(BusinessException.class);
        expected.expectMessage("Route or airport does not exist");
        AirportRepository airportRepository = new AirportRepository();
        DistanceService distanceService = new DistanceService(airportRepository);
        RouteRepository routeRepository = new RouteRepository(distanceService);
        FlightService flightService = new FlightService(routeRepository, airportRepository, distanceService);

        flightService.findShortestRoute("TLL", "NON");
    }

    @Test
    public void routeTooLong() {
        expected.expect(BusinessException.class);
        expected.expectMessage("Route is too long: 6");
        AirportRepository airportRepository = new AirportRepository();
        DistanceService distanceService = new DistanceService(airportRepository);
        RouteRepository routeRepository = new RouteRepository(distanceService);
        FlightService flightService = new FlightService(routeRepository, airportRepository, distanceService);

        flightService.findShortestRoute("TLL", "ACV");
    }
}