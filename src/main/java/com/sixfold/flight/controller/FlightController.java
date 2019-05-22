package com.sixfold.flight.controller;


import com.sixfold.flight.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping(value = "get-shortest-path")
    public ResponseEntity<FlightResponseDto> findShortestRoute(@RequestBody FlightRequestDto request) {

        FlightResponseDto response = flightService.findShortestRoute(request.getStart().toUpperCase(), request.getEnd().toUpperCase());
        return ResponseEntity.ok(response);
    }
}
