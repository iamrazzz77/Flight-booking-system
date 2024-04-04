package com.moveinsync.flightbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Validated
public class Reportmodel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Booked_seats")
    private int bookedSeats=0;

    @Column(name = "Revenue_Generated")
    private Double revenueGenerated= (double) 0;

    @Column(name = "Flight_Number",unique = true)
    private String flightNumber;
}
