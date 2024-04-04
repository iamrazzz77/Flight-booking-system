package com.moveinsync.flightbooking.service;

import com.moveinsync.flightbooking.model.Reportmodel;
import com.moveinsync.flightbooking.repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Paymentservice {

    @Autowired
    ReportRepo reportRepo;
    public Boolean dopayment(String flightNumber,Double ticketPrice){

        Reportmodel report=reportRepo.findByFlightNumber(flightNumber);

        report.setBookedSeats(report.getBookedSeats()+1);
        report.setRevenueGenerated(report.getRevenueGenerated()+ticketPrice);

        reportRepo.save(report);
        return true;

    }
}
