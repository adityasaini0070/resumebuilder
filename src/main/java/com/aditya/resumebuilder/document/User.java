package com.aditya.resumebuilder.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    private String subscriptionPlan = "basic";
    private Boolean emailVerified = false;
    private String verificationToken;
    private LocalDateTime verificationExpires;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}