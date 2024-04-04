package com.moveinsync.flightbooking.controller;
import com.moveinsync.flightbooking.configuration.JwtUtil;
import com.moveinsync.flightbooking.dto.SearchDto;
import com.moveinsync.flightbooking.dto.UserDto;
import com.moveinsync.flightbooking.exceptions.FlightAuthException;
import com.moveinsync.flightbooking.exceptions.UsernamePasswordException;
import com.moveinsync.flightbooking.model.Flight;
import com.moveinsync.flightbooking.model.User;
import com.moveinsync.flightbooking.repository.FlightRepo;
import com.moveinsync.flightbooking.repository.UserRepo;
import com.moveinsync.flightbooking.service.UserFlightService;
import com.moveinsync.flightbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFlightService userFlightService;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private FlightRepo flightRepo;
    @Autowired
    private UserRepo userRepo;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) throws FlightAuthException, MessagingException {
        User user = new User(userDto.getUsername(), userDto.getPassword(), userDto.getEmail(), userDto.isAdmin());
        userService.usernameExists(userDto.getUsername());
        userService.registerUser(user);
        return ResponseEntity.ok().body("User registered successfully");
    }

    @GetMapping("/users")
    public List<User> showallusers(@RequestHeader Map<String,String> request) throws FlightAuthException {
        String token = request.get("authorization").substring(7);
        if (!jwtUtil.isAdmin(token)) {
            throw new FlightAuthException("You are not authorized");
        }
        return userService.showall();
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDto userDto) throws UsernamePasswordException {
        User user = new User(userDto.getUsername(), userDto.getPassword(), userDto.getEmail(), userDto.isAdmin());
        return (userService.loginUser(user));
    }

    @PutMapping("/update")
    public String update(@RequestBody UserDto userDto, @RequestHeader Map<String,String> request) {
        User user = new User(userDto.getUsername(), userDto.getPassword(), userDto.getEmail(), userDto.isAdmin());
        String token = request.get("authorization").substring(7);
        return (userService.updateUser(user, token));
    }


    @PostMapping("/flight-with-class")
    List<Flight> getFlightWithClass(@RequestBody SearchDto searchDto) {
        Date date1 = java.sql.Date.valueOf(searchDto.getDate());
        return userFlightService.findFlightWithClass(date1, searchDto.getSource(), searchDto.getDestination(), searchDto.getSeatType());
    }
    @PostMapping("/flight-without-class")
    List<Flight> getFlight(@RequestBody SearchDto searchDto) {
        Date date1 = java.sql.Date.valueOf(searchDto.getDate());
        return userFlightService.findFlight(date1, searchDto.getSource(), searchDto.getDestination());
    }
}
