package com.App;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.Domain.Token;

public class SessionAuth {
    private static List<Token> tokens = new ArrayList<>();
    private static int timeout  = 30; // Timeout in minutes

    public static UUID createSession(String user) { // Create a session
        UUID uniqueUserIdentifier = UUID.randomUUID(); // Create a unique user identifier
        tokens.add(new Token(uniqueUserIdentifier, user)); // Add token to list of tokens
        System.out.println(user + " logged in" + " with token " + uniqueUserIdentifier); // For testing purposes
        return uniqueUserIdentifier;
    }

    public static boolean validateUser(String username){ // Validates whether the user is already logged in
        for (Token token : tokens) {
            if (token.getUser().equals(username)) { // Check if user is logged in
                return true;
            }
        }
        return false;
    }

    public static boolean removeSession(UUID uniqueUserIdentifier){ // Removes the token from the list of tokens, indicating that the user has logged out and the session is terminated
        for (Token token : tokens) {
            if (token.getUniqueUserIdentifier().equals(uniqueUserIdentifier)) { // Check if user is logged in
                tokens.remove(token);
                return true;
            }
        }
        return false;
    }
    

    public static boolean validateSession(UUID uniqueUserIdentifier) { // Validate session
        System.out.println("Validating session, with token: " + uniqueUserIdentifier + " Amount of tokens: " + tokens.size()); // For testing purposes
        for (Token token : tokens) { // Loop through tokens
            if (token.getUniqueUserIdentifier().equals(uniqueUserIdentifier)) {
                return timeoutHandler(token); // Check if token has timed out
            } 
        }
        return false; // Token not found
    }


    public static boolean timeoutHandler(Token token){ // Check if token has timed out
        if(token.getExpiryDate().isAfter(LocalDateTime.now().minusMinutes(timeout))){ 
            return true; // Token is still valid
        } else {
            tokens.remove(token); // Remove token
            return false; // Token has timed out
        }
    }

    public static List<Token> getTokens() {
        return tokens;
    }

    public static String getUsernameFromToken(UUID uniqueUserIdentifier) {
        for (Token token : tokens) {
            if (token.getUniqueUserIdentifier().equals(uniqueUserIdentifier)) { // Check if user is logged in
                return token.getUser(); // Return username
            }
        }
        return null;
    }
}
