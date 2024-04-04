package com.moveinsync.flightbooking.repository;

import com.moveinsync.flightbooking.model.FlightSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepo extends JpaRepository<FlightSeat,Long> {
    @Query("SELECT s FROM FlightSeat s WHERE s.flight.id = :flightId")
    List<FlightSeat> findAllByFlightId(@Param("flightId") Long flightId);

    List<FlightSeat> findAllByUserId(Long userid);

    @Query("select count(*) from FlightSeat f where f.flight.id=:flightId and f.booked=false")
    long getNumUnsoldSeatsForFlight(Long flightId);
}
