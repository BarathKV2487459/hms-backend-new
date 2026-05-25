package com.symmetricsquad.hms_backend.service;

import com.symmetricsquad.hms_backend.model.RequestLog;
import com.symmetricsquad.hms_backend.dto.response.RequestLogResponse;
import com.symmetricsquad.hms_backend.model.enums.UserRole;
import com.symmetricsquad.hms_backend.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestLogServiceImpl implements RequestLogService {

    private final RequestLogRepository requestLogRepository;

    @Override
    @Transactional
    public void log(Long userId, UserRole userRole, String httpMethod,
                    String endpoint, int httpStatus,
                    long responseTimeMs, String ipAddress) {
        RequestLog log = new RequestLog();
        log.setUserId(userId);
        log.setUserRole(userRole);
        log.setHttpMethod(httpMethod);
        log.setEndpoint(endpoint);
        log.setHttpStatus(httpStatus);
        log.setResponseTimeMs(responseTimeMs);
        log.setIpAddress(ipAddress);
        requestLogRepository.save(log);
    }

    @Override
    public Page<RequestLogResponse> getLogs(
            Long userId,
            UserRole userRole,
            String httpMethod,
            Integer httpStatus,
            String endpoint,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        Specification<RequestLog> spec = Specification.where((Specification<RequestLog>) null);

        if (userId != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("userId"), userId));
        }
        if (userRole != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("userRole"), userRole));
        }
        if (httpMethod != null && !httpMethod.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.equal(cb.upper(root.get("httpMethod")), httpMethod.toUpperCase()));
        }
        if (httpStatus != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("httpStatus"), httpStatus));
        }
        if (endpoint != null && !endpoint.isBlank()) {
            // Partial match — e.g. "/api/appointments" matches any endpoint containing that string
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("endpoint")),
                        "%" + endpoint.toLowerCase() + "%"));
        }
        if (from != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("requestedAt"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("requestedAt"), to));
        }

        return requestLogRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public int purgeLogs(LocalDateTime cutoff) {
        return requestLogRepository.deleteByRequestedAtBefore(cutoff);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private RequestLogResponse toResponse(RequestLog log) {
        RequestLogResponse res = new RequestLogResponse();
        res.setId(log.getId());
        res.setUserId(log.getUserId());
        res.setUserRole(log.getUserRole());
        res.setHttpMethod(log.getHttpMethod());
        res.setEndpoint(log.getEndpoint());
        res.setHttpStatus(log.getHttpStatus());
        res.setResponseTimeMs(log.getResponseTimeMs());
        res.setIpAddress(log.getIpAddress());
        res.setRequestedAt(log.getRequestedAt());
        return res;
    }
}
