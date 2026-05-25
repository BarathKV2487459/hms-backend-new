package com.symmetricsquad.hms_backend.exception;

// ── ResourceNotFoundException.java ───────────────────────────────────────────
// Throw this instead of IllegalArgumentException when an entity is not found.
// Maps to 404 in the global handler.
//
// Usage in service:
//   throw new ResourceNotFoundException("Doctor not found with id: " + id);


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
