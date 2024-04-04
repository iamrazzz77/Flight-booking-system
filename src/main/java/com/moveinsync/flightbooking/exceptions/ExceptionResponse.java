package com.moveinsync.flightbooking.exceptions;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExceptionResponse {

    private String message;
    private LocalDateTime timeStamp;
    private String status;
    private String path;
    private String error;

    public ExceptionResponse(WebRequest webRequest) {
        this.timeStamp = LocalDateTime.now();
        this.path = webRequest.getDescription(false).split("=")[1];
    }

}