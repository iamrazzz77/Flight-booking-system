package com.moveinsync.flightbooking.controller;


import com.moveinsync.flightbooking.dto.SearchDto;
import com.moveinsync.flightbooking.model.Flight;
import com.moveinsync.flightbooking.model.FlightSeat;
import com.moveinsync.flightbooking.service.Flightservice;
import com.moveinsync.flightbooking.service.UserFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.sql.Date;
import java.util.*;

@RestController
@RequestMapping("/flights/")
public class Flightcontroller {

    @Autowired
    Flightservice flightservice;
    @Autowired
    UserFlightService userFlightService;

    @GetMapping("/getallflights")
    public List<Flight> getallflights() {
        return flightservice.getallflights();
    }

    @GetMapping("/{flightId}/")
    public List<FlightSeat> getallseatsbyflightid(@PathVariable("flightId") Long flightid) {
        return flightservice.getallseatsbyflightid(flightid);
    }

    @GetMapping("/getseatsrelatedtouser")
    public List<FlightSeat> getseatsrelatedtouser(@RequestHeader Map<String,String> request) {
        String token = request.get("authorization").substring(7);
        return flightservice.getseatsrelatedtouser(token);
    }

    @PostMapping("/{seatid}")
    public String booktheseat(@PathVariable("seatid") Long seatid, @RequestHeader Map<String,String> request) {
        String token = request.get("authorization").substring(7);
        return flightservice.bookaseat(seatid, token);
    }
    @PostMapping("/search")
    List<Flight> getFlight(@RequestBody SearchDto searchDto) {
        Date date1 = java.sql.Date.valueOf(searchDto.getDate());
        return userFlightService.findFlight(date1, searchDto.getSource(), searchDto.getDestination());
    }
    @DeleteMapping("/{seatid}")
    public String cancelbooking(@PathVariable("seatid") Long seatid, @RequestHeader Map<String,String> request) throws MessagingException {
        String token = request.get("authorization").substring(7);
        return flightservice.deleteaseat(seatid, token);
    }
}
