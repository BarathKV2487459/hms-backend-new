package com.symmetricsquad.hms_backend.exception;

// ── ConflictException.java ────────────────────────────────────────────────────
// Throw this for state conflicts — e.g. booking a taken slot, duplicate unique fields.
// Maps to 409 in the global handler.
//
// Usage in service:
//   throw new ConflictException("Doctor already has an appointment in this time slot");
//   throw new ConflictException("Patient profile already exists for user: " + userId);

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
