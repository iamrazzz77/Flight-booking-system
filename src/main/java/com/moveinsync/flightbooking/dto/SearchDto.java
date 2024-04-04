package com.moveinsync.flightbooking.dto;
import com.moveinsync.flightbooking.model.SeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    String date;
    String source;
    String destination;
    SeatType seatType;

    public SearchDto(String date, String source, String destination) {
        this.date = date;
        this.source = source;
        this.destination = destination;
    }
}
