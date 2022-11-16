package com.Domain;

import java.time.LocalDateTime;
import java.util.UUID;


public class Token {
    private String user;
    private UUID uniqueUserIdentifier;
    private LocalDateTime expiryDate;


    public Token(UUID uniqueUserIdentifier, String user) {
        this.uniqueUserIdentifier = uniqueUserIdentifier;
        this.user = user;
        this.expiryDate = LocalDateTime.now(); // To control timeout of tokens
    }

    public String getUser() {
        return user;
    }


    public void setUser(String user) {
        this.user = user;
    }


    public UUID getUniqueUserIdentifier() {
        return uniqueUserIdentifier;
    }


    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
