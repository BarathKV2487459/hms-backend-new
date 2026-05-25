package com.symmetricsquad.hms_backend.model.enums;

public enum AppointmentStatus {PENDING, CONFIRMED, CANCELLED_BY_USER, CANCELLED_BY_DOCTOR, COMPLETED, NO_SHOW}


/*
 * Status flow:
 *   User books       → PENDING
 *   Doctor approves  → CONFIRMED
 *   User cancels     → CANCELLED_BY_USER
 *   Doctor cancels   → CANCELLED_BY_DOCTOR
 *   Doctor marks     → COMPLETED
 *   Admin marks      → NO_SHOW  (hidden from patient & doctor views)
 *
 * Who can set what:
 *   PATIENT → PENDING, CANCELLED_BY_USER
 *   DOCTOR  → CONFIRMED, CANCELLED_BY_DOCTOR, COMPLETED
 *   ADMIN   → NO_SHOW (and override anything)
 *
 * Query rule:
 *   Patient/Doctor queries : WHERE status != 'NO_SHOW'
 *   Admin queries          : no filter — sees everything
 */