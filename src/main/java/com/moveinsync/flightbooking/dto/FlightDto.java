package com.moveinsync.flightbooking.dto;


import com.moveinsync.flightbooking.model.FlightSeat;
import com.moveinsync.flightbooking.model.FlightSeatClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto{
    private Long id;
    private String flightNumber;
    private String departureAirport;
    private LocalDateTime departureTime;
    private Date date;
    private String arrivalAirport;
    private LocalDateTime arrivalTime;
    private Duration flightDuration;
    private Double ticketPrice;
    private Integer totalSeats;
    private String airlineName;
    private String aircraftType;
    private List<FlightSeatClass> flightSeatClasses;
    private List<FlightSeat> seats;
    private  String departureCity;

    private  String arrivalCity;

    private  String terminal;

    private  String gateNo;
}

