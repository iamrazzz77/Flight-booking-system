package com.moveinsync.flightbooking.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightSeatClass {
    private int seatNumber;
    private double ticketPrice;
    private SeatType seatType;

    public FlightSeatClass(int seatNumber, double ticketPrice, SeatType seatType) {
        this.seatNumber = seatNumber;
        this.ticketPrice = ticketPrice;
        this.seatType = seatType;
    }

    public FlightSeatClass() {
    }
}
