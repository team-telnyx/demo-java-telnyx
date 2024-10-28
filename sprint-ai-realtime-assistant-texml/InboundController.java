package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class InboundController {
    @PostMapping("/inbound")
    public ResponseEntity<String> incomingCall(HttpServletRequest request) {
        System.out.println("Incoming call received");
        String host = request.getHeader("host");

        try {
            // Load texml.xml from resources
            ClassPathResource resource = new ClassPathResource("texml.xml");
            String texmlResponse = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            texmlResponse = texmlResponse.replace("{host}", host);
            System.out.println("TeXML Response: " + texmlResponse);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            return new ResponseEntity<>(texmlResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("File not found: texml.xml");
            return new ResponseEntity<>("TeXML file not found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}