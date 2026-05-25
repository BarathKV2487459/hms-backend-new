package com.symmetricsquad.hms_backend.dto.response;

import com.symmetricsquad.hms_backend.model.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequestLogResponse {
    private Long id;
    private Long userId;        // null for unauthenticated requests
    private UserRole userRole;  // null for unauthenticated requests
    private String httpMethod;
    private String endpoint;
    private Integer httpStatus;
    private Long responseTimeMs;
    private String ipAddress;
    private LocalDateTime requestedAt;
}