package com.moveinsync.flightbooking.service;

import com.moveinsync.flightbooking.configuration.JwtUtil;
import com.moveinsync.flightbooking.configuration.PasswordUtil;
import com.moveinsync.flightbooking.exceptions.FlightAuthException;
import com.moveinsync.flightbooking.exceptions.UsernamePasswordException;
import com.moveinsync.flightbooking.model.User;
import com.moveinsync.flightbooking.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    EmailService emailService;

    public void usernameExists(String username) throws FlightAuthException {
        if (userRepo.findByUsername(username) != null)
            throw new FlightAuthException("Username is already taken");
    }

    public List<User> showall() {
        return userRepo.findAll();
    }


    public User registerUser(User userDto) throws MessagingException {
        userDto.setPassword(PasswordUtil.encode(userDto.getPassword()));
        User savedUser = userRepo.save(userDto);
        String token = jwtUtil.generateToken(savedUser);
        savedUser.setToken(token);
        emailService.sendEmail(savedUser.getEmail(), "Welcome to Alitalia Airlines", "Thank you for registering with Alitalia Airlines");
        return savedUser;
    }

    public String loginUser(User userDto) throws  UsernamePasswordException {
        String curUsername = userDto.getUsername();
        User curuser = userRepo.findByUsername(curUsername);
        if (PasswordUtil.match(userDto.getPassword(),curuser.getPassword())) {
            String token = jwtUtil.generateToken(curuser);
            userDto.setToken(token);
            return token;
        }
        throw new UsernamePasswordException("Wrong Username or Password");
    }

    public String updateUser(User userDto, String token) {
        User curUser = userRepo.findByUsername(jwtUtil.extractUsername(token));
        curUser.setUsername(userDto.getUsername());
        curUser.setPassword(PasswordUtil.encode(userDto.getPassword()));
        curUser.setEmail(userDto.getEmail());
        userRepo.save(curUser);
        return ("Details updated successfully");
    }
}
