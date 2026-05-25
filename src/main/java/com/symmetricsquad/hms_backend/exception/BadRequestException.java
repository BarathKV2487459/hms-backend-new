package com.symmetricsquad.hms_backend.exception;

// ── BadRequestException.java ──────────────────────────────────────────────────
// Throw this for invalid input, business rule violations, duplicate data etc.
// Maps to 400 in the global handler.
//
// Usage in service:
//   throw new BadRequestException("Email already registered: " + email);
//   throw new BadRequestException("Passwords do not match");
//   throw new BadRequestException("Doctor already has an appointment in this slot");


public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
