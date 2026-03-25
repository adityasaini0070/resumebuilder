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
    @Value("${spring.mail.from}")
    private String fromEmail;

    @Value("${spring.mail.apikey}")
    private String apiKey;

    @Value("${spring.mail.sandboxdomain}")
    private String sandboxDomain;

    @Value("${spring.mail.baseurl}")
    private String baseUrl;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            HttpResponse<JsonNode> request = Unirest.post(baseUrl + "/v3/" + sandboxDomain + "/messages")
                    .basicAuth("api", apiKey)
                    .field("from", fromEmail)
                    .field("to", to)
                    .field("subject", subject)
                    .field("html", htmlContent)
                    .asJson();

            if (request.getStatus() == 200) {
                log.info("Email sent successfully to {}", to);
            } else {
                log.error("Failed to send email. Status: {}, Body: {}", request.getStatus(), request.getBody());
                throw new RuntimeException("Failed to send email via Mailgun: " + request.getStatusText());
            }
        } catch (UnirestException e) {
            log.error("Error while sending email via Mailgun", e);
            throw new RuntimeException("Error while sending email via Mailgun", e);
        }
    }
}
