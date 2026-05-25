package com.symmetricsquad.hms_backend.exception;

// ── ForbiddenException.java ───────────────────────────────────────────────────
// Throw this for ownership violations caught in service logic
// (separate from Spring Security's AccessDeniedException).
// Maps to 403 in the global handler.
//
// Usage in service:
//   throw new ForbiddenException("You can only cancel your own appointments");
//   throw new ForbiddenException("You can only update records you created");

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}


// ── IllegalStateException is already in java.lang ────────────────────────────
// Keep using it for invalid state transitions (e.g. cancelling a COMPLETED appointment).
// The global handler maps it to 422 Unprocessable Entity.
