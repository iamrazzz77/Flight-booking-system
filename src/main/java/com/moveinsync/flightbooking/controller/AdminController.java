package com.moveinsync.flightbooking.controller;

import com.moveinsync.flightbooking.configuration.JwtUtil;
import com.moveinsync.flightbooking.dto.FlightDto;
import com.moveinsync.flightbooking.exceptions.InvalidFlightException;
import com.moveinsync.flightbooking.model.Flight;
import com.moveinsync.flightbooking.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/flights/")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    JwtUtil jwtUtil;
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> add(@RequestBody FlightDto flightDto, @RequestHeader Map<String,String> request) throws InvalidFlightException {
        String token = request.get("authorization").substring(7);
        Map<String, String> response = new HashMap<>();
        if (!jwtUtil.isAdmin(token)) {
            response.put("message", "You are not authorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        try {
            Flight flight = new Flight(flightDto.getFlightNumber(), flightDto.getDepartureAirport(), flightDto.getDepartureTime(), flightDto.getDate(), flightDto.getArrivalAirport(), flightDto.getArrivalTime(), flightDto.getFlightDuration(), flightDto.getTicketPrice(), flightDto.getTotalSeats(), flightDto.getAirlineName(), flightDto.getAircraftType(), flightDto.getArrivalCity(), flightDto.getDepartureCity(),flightDto.getGateNo(),flightDto.getTerminal(), flightDto.getFlightSeatClasses(), flightDto.getSeats());
            boolean success = adminService.addFlight(flight);
            if (success) {
                response.put("Status", "Success");
            } else {
                response.put("Status", "Failed");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            throw new InvalidFlightException("Invalid flight data");
        }
    }


    @DeleteMapping("/delete/{flightNumber}")
    public Map<String, String> delete(@PathVariable String flightNumber, @RequestHeader Map<String,String> request) {
        Map<String, String> response = new HashMap<>();
        String token = request.get("authorization").substring(7);
        if (!jwtUtil.isAdmin(token)) {
            response.put("message", "You are not authorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED).getBody();
        }
        boolean success = adminService.deleteFlight(flightNumber);
        if (success) {
            response.put("Status", "Success");
        } else {
            response.put("Status", "Failed");
        }
        return response;
    }


    @PutMapping("/update")
    public Map<String, String> update(@RequestBody FlightDto flightDto, @RequestHeader Map<String,String> request) {
        Map<String, String> response = new HashMap<>();
        String token = request.get("authorization").substring(7);
        if (!jwtUtil.isAdmin(token)) {
            response.put("message", "You are not authorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED).getBody();
        }
        boolean success = adminService.updateFlight(flightDto);
        if (success) {
            response.put("Status", "Success");
        } else {
            response.put("Status", "Failed");
        }
        return response;

    }
}
