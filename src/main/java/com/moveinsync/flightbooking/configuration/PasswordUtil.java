package com.moveinsync.flightbooking.configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public static String encode(String password) {
        return encoder.encode(password);
    }
    public static boolean match(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }

    private PasswordUtil() {
    }
}