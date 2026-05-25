package com.symmetricsquad.hms_backend.model;

import com.symmetricsquad.hms_backend.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "request_logs",
        indexes = {
                @Index(name = "idx_log_user",   columnList = "user_id"),
                @Index(name = "idx_log_status", columnList = "http_status"),
                @Index(name = "idx_log_time",   columnList = "requested_at")
        }
)
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;                  // nullable — unauthenticated requests still get logged

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;            // nullable for same reason

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;            // GET, POST, PUT, DELETE, PATCH

    @Column(name = "endpoint", nullable = false)
    private String endpoint;              // e.g. /api/appointments/3

    @Column(name = "http_status", nullable = false)
    private Integer httpStatus;           // 200, 201, 400, 401, 403, 404, 500 ...

    @Column(name = "response_time_ms")
    private Long responseTimeMs;          // how long the request took — useful for spotting slow endpoints

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @PrePersist
    void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }

    // No BaseEntity, no soft delete — logs are append-only, purged by scheduled job
}