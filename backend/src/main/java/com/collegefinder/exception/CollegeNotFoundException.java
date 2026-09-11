package com.collegefinder.exception;

public class CollegeNotFoundException extends RuntimeException {
    public CollegeNotFoundException(String message) {
        super(message);
    }
}
