package com.moveinsync.flightbooking.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
@Entity
@Getter
@Setter
@Validated
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "flight_number",unique = true)
    private String flightNumber;
    @Column(name = "departure_airport")
    private String departureAirport;
    @Column(name = "departure_time")
    private LocalDateTime departureTime;
    @Column(name = "date")
    private Date date;
    @Column(name = "arrival_airport")
    private String arrivalAirport;
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;
    @Column(name = "flight_duration")
    private Duration flightDuration;
    @Column(name = "ticket_price")
    private Double ticketPrice;
    @Column(name = "available_seats")
    private Integer totalSeats;
    @Column(name = "airline_name")
    private String airlineName;
    @Column(name = "aircraft_type")
    private String aircraftType;

    @Column(name = "departure_city")
    private  String departureCity;

    @Column(name = "arrival_city")
    private  String arrivalCity;

    @Column(name = "terminal")
    private  String terminal;

    @Column(name = "gate_no")
    private  String gateNo;
    @Transient
    private List<FlightSeatClass> flightSeatClasses;
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<FlightSeat> seats;
    public Flight(String flightNumber, String departureAirport, LocalDateTime departureTime, Date date, String arrivalAirport, LocalDateTime arrivalTime, Duration flightDuration, Double ticketPrice, Integer totalSeats, String airlineName, String aircraftType, String arrivalCity, String departureCity, String gateNo, String terminal, List<FlightSeatClass> flightSeatClasses,List<FlightSeat> seats) {
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.departureTime = departureTime;
        this.date = date;
        this.arrivalAirport = arrivalAirport;
        this.arrivalTime = arrivalTime;
        this.flightDuration = flightDuration;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;
        this.airlineName = airlineName;
        this.aircraftType = aircraftType;
        this.flightSeatClasses = flightSeatClasses;
        this.seats = seats;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.terminal = terminal;
        this.gateNo = gateNo;
        this.generateSeats();
    }
    public Flight() {
    }

    public void generateSeats() {
        seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            FlightSeat seat = new FlightSeat(this, i, ticketPrice);
            seats.add(seat);
        }
        flightSeatClasses.forEach(seat -> {
            int index = seat.getSeatNumber();
            seats.get(index).setSeatType(seat.getSeatType());
            seats.get(index).setTicketPrice(seat.getTicketPrice());
        });
    }
    // Constructors, getters and setters omitted for brevity
}