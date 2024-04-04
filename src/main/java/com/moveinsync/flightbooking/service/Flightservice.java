package com.moveinsync.flightbooking.service;

import com.convertapi.client.Config;
import com.convertapi.client.ConvertApi;
import com.moveinsync.flightbooking.configuration.JwtUtil;
import com.moveinsync.flightbooking.model.Flight;
import com.moveinsync.flightbooking.model.FlightSeat;
import com.moveinsync.flightbooking.model.User;
import com.moveinsync.flightbooking.repository.FlightRepo;
import com.moveinsync.flightbooking.repository.SeatRepo;
import com.moveinsync.flightbooking.repository.UserRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class Flightservice {

    @Autowired
    FlightRepo flightRepo;

    @Autowired
    SeatRepo seatRepo;

    @Autowired
    Paymentservice paymentservice;

    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    EmailService emailService;

    private static final long MAX_PRICE_INCREASE_PERCENTAGE = 50;
    private static final long DAYS_BEFORE_DEPARTURE_TO_MAX_PRICE_INCREASE = 60;
    private static final long MAX_MINUTES_BEFORE_CANCEL_TICKET = 240;

    public Double calculateTicketPrice(long flightId,double ticketPrice) {

        long numUnsoldSeats = seatRepo.getNumUnsoldSeatsForFlight(flightId);
        Flight flight = flightRepo.findById(flightId);
        LocalDate currentDate = LocalDate.now();
        long daysUntilDeparture = ChronoUnit.DAYS.between(currentDate, flight.getDate().toLocalDate()); // Implement this method to calculate days until departure


        Double priceIncreasePercentage = 0.0;
        if (numUnsoldSeats > 0) {
            priceIncreasePercentage = (100.0 * (MAX_PRICE_INCREASE_PERCENTAGE * numUnsoldSeats)) / (numUnsoldSeats + 10);
            if (priceIncreasePercentage > MAX_PRICE_INCREASE_PERCENTAGE) {
                priceIncreasePercentage = MAX_PRICE_INCREASE_PERCENTAGE * 1.0;
            }
        }
        if (daysUntilDeparture <= DAYS_BEFORE_DEPARTURE_TO_MAX_PRICE_INCREASE) {
            long daysUntilMaxPriceIncrease = DAYS_BEFORE_DEPARTURE_TO_MAX_PRICE_INCREASE - daysUntilDeparture;
            long maxPriceIncreasePercentage = (daysUntilMaxPriceIncrease / 7) * 10;
            priceIncreasePercentage += maxPriceIncreasePercentage;
        }
        return ticketPrice + (ticketPrice * priceIncreasePercentage / 100);
    }

    public List<Flight> getallflights(){
        List<Flight> flightList = flightRepo.findAll();
        flightList.forEach(flight-> flight.getSeats().forEach(seat-> seat.setTicketPrice(calculateTicketPrice(flight.getId(), seat.getTicketPrice()))));
        return flightList;
    }

    public List<FlightSeat> getallseatsbyflightid(Long id){
        List<FlightSeat> flightSeatList = seatRepo.findAllByFlightId(id);
        flightSeatList.forEach(seat-> seat.setTicketPrice(calculateTicketPrice(id, seat.getTicketPrice())));
        return flightSeatList;
    }

    public String bookaseat(Long id, String token) {
        String username = jwtUtil.extractUsername(token);
        User curUser = userRepo.findByUsername(username);
        Long userId = curUser.getId();
        Optional<FlightSeat> seat = seatRepo.findById(id);
        if (seat.isPresent()) {
            if (seat.get().isBooked()) {
                return "This seat is already booked";
            }
            String flightNumber = seat.get().getFlight().getFlightNumber();
            Double ticketprice = calculateTicketPrice(seat.get().getFlight().getId(), seat.get().getTicketPrice());
            paymentservice.dopayment(flightNumber, ticketprice);
            seat.get().setBooked(true);
            seat.get().setUserId(userId);
            seatRepo.save(seat.get());
            boardingPassGenerator(seat.get().getFlight(), curUser, seat.get(),token);
            return "Your seat booked successfully";
        }
        return "Seat id is invalid";
    }

    public String deleteaseat(Long seatid, String token) throws MessagingException {
        String username = jwtUtil.extractUsername(token);
        User curUser = userRepo.findByUsername(username);
        Long userId = curUser.getId();
        Optional<FlightSeat> seat = seatRepo.findById(seatid);
        if (seat.isPresent() && userId.equals(seat.get().getUserId())) {
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime flightDate = seat.get().getFlight().getArrivalTime();

            Duration duration = Duration.between(currentDate, flightDate);
            long minutes = Math.abs(duration.toMinutes());

            if(MAX_MINUTES_BEFORE_CANCEL_TICKET>=minutes){
                return "You can't cancel seat...";
            }

            seat.get().setUserId(null);
            seat.get().setBooked(false);
            seatRepo.save(seat.get());
            double refundedTicketPrice = (seat.get().getTicketPrice()) / 2;
            emailService.sendEmail(curUser.getEmail(), "Canceled booking", "Your booking has been cancled");
            return "Seat deleted successfully your refunded amount is " + refundedTicketPrice;
        }
        return "Its not Your seat Check your seat number first";
    }
    public List<FlightSeat> getseatsrelatedtouser(String token){
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username);
        return seatRepo.findAllByUserId(user.getId());
    }

    public void boardingPassGenerator(Flight flight, User user, FlightSeat flightSeat, String token) {
        Config.setDefaultSecret("T3APGExm27JtAOwB");
        String username = jwtUtil.extractUsername(token);
        String emailUser = userRepo.findByUsername(username).getEmail();
        try {
            File htmlFile = new File("src/main/resources/templates/flight-ticket.html");
            Document document = Jsoup.parse(htmlFile, "UTF-8");

            Element flightNumElement = document.select(".flight strong").first();
            flightNumElement.text(flight.getFlightNumber());

            Element firstCityElement = document.select(".first").first();
            Element firstCityNameElement = firstCityElement.selectFirst("small");
            Element firstCitySmallElement = firstCityElement.selectFirst("strong");
            firstCityNameElement.text(flight.getDepartureCity());
            firstCitySmallElement.text(flight.getDepartureCity().substring(0,3));

            Element secondCityElement = document.select(".second").first();
            Element secondCityNameElement = secondCityElement.selectFirst("small");
            Element secondCitySmallElement = secondCityElement.selectFirst("strong");
            secondCityNameElement.text(flight.getArrivalCity());
            secondCitySmallElement.text(flight.getArrivalCity().substring(0,3));

            Element terminalElement = document.select(".term strong em").first();
            terminalElement.text(flight.getTerminal());

            Element gateElement = document.select(".gate strong em").first();
            gateElement.text(flight.getGateNo());

            Element seatElement = document.select(".seat strong").first();
            int seatNumber = flightSeat.getSeatNumber();
            seatElement.text(String.valueOf(seatNumber+1));

            Element seatTypeElement = document.select(".type strong").first();
            seatTypeElement.text(flightSeat.getSeatType().toString());

            Element boardingElement = document.select(".board strong").first();
            boardingElement.text(flight.getDepartureTime().minusHours(1).toString().substring(11));

            Element departureElement = document.select(".depart strong").first();
            departureElement.text(flight.getDepartureTime().toString().substring(11));

            Element durationElement = document.select(".duration strong").first();
            Duration duration = Duration.between(flight.getDepartureTime(), flight.getArrivalTime());
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            String formattedDuration = String.format("%d:%02d hrs", hours, minutes);
            durationElement.text(formattedDuration);

            Element arrivalElement = document.select(".arrival strong").first();
            arrivalElement.text(flight.getArrivalTime().toString().substring(11));

            Element passengerElement = document.select(".passenger strong").first();
            passengerElement.text(user.getUsername());

            Element dateElement = document.select(".date strong").first();
            dateElement.text(flight.getDate().toString());

            FileWriter writer = new FileWriter(htmlFile);
            writer.write(document.outerHtml());
            writer.close();

            ConvertApi.convertFile(htmlFile.getPath(), "src/main/resources/templates/flight-ticket.pdf");
            emailService.sendEmailWithAttachment(emailUser, "Boarding Pass", "Your boarding pass for your upcomming flight is ready!","src/main/resources/templates/flight-ticket.pdf");
        } catch (Exception ex) {
            System.err.println("Conversion failed: " + ex.getMessage());
        }
    }

}
