package com.moveinsync.flightbooking.controller;
import com.itextpdf.text.DocumentException;
import com.moveinsync.flightbooking.configuration.JwtUtil;
import com.moveinsync.flightbooking.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayInputStream;
import java.util.Map;

@RestController
public class ReportController {
    @Autowired
    ReportService reportService;

    @Autowired
    JwtUtil jwtUtil;
    @GetMapping("/generatereport")
    public ResponseEntity<InputStreamResource> generatereports(@RequestHeader Map<String,String> request) throws DocumentException {
        String token = request.get("authorization").substring(7);
        if (!jwtUtil.isAdmin(token)) {
            String message = "You are not authorized to access this resource";
            byte[] bytes = (message + "\n").getBytes();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(new ByteArrayInputStream(bytes)));

        }
        ByteArrayInputStream bis=reportService.generateReports();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=my-pdf.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
