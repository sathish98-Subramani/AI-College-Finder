package com.collegefinder.exception;

public class InvalidRecommendationRequestException extends RuntimeException {
    public InvalidRecommendationRequestException(String message) {
        super(message);
    }
}
