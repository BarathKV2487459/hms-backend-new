package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.dto.response.RequestLogResponse;
import com.symmetricsquad.hms_backend.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface RequestLogService {

    /**
     * Persist one log entry — called from a HandlerInterceptor or
     * OncePerRequestFilter after the response is committed.
     */
    void log(Long userId, UserRole userRole, String httpMethod,
             String endpoint, int httpStatus, long responseTimeMs,
             String ipAddress);

    /**
     * Get logs — paginated and sortable.
     * All filter params are optional (nullable).
     *
     * Sortable fields (pass via Pageable sort param):
     *   requestedAt, httpStatus, responseTimeMs, userId, endpoint
     *
     * Example: GET /admin/logs?page=0&size=20&sort=responseTimeMs,desc
     *          — shows slowest requests first
     *
     * @param userId     filter by specific user
     * @param userRole   filter by role
     * @param httpMethod filter by method (GET, POST ...)
     * @param httpStatus filter by exact status code
     * @param endpoint   filter by endpoint (partial match, contains)
     * @param from       requests after this timestamp
     * @param to         requests before this timestamp
     */
    Page<RequestLogResponse> getLogs(
            Long userId,
            UserRole userRole,
            String httpMethod,
            Integer httpStatus,
            String endpoint,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    /** Hard-delete logs older than cutoff — @Scheduled job only */
    int purgeLogs(LocalDateTime cutoff);
}