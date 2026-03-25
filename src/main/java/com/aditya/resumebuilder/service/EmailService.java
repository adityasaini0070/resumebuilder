package com.aditya.resumebuilder.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Value("${resend.mail.from}")
    private String fromEmail;

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${resend.api.url}")
    private String apiUrl;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            HttpResponse<JsonNode> request = Unirest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(new org.json.JSONObject()
                            .put("from", fromEmail)
                            .put("to", to)
                            .put("subject", subject)
                            .put("html", htmlContent))
                    .asJson();

            if (request.getStatus() == 200 || request.getStatus() == 201) {
                log.info("Email sent successfully via Resend to {}", to);
            } else {
                log.error("Failed to send email via Resend. Status: {}, Body: {}", request.getStatus(), request.getBody());
                throw new RuntimeException("Failed to send email via Resend: " + request.getStatusText());
            }
        } catch (UnirestException e) {
            log.error("Error while sending email via Resend", e);
            throw new RuntimeException("Error while sending email via Resend", e);
        }
    }
}
