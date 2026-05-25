package com.symmetricsquad.hms_backend.repository;

import com.symmetricsquad.hms_backend.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface RequestLogRepository
        extends JpaRepository<RequestLog, Long>, JpaSpecificationExecutor<RequestLog> {

    // ── Used by RequestLogService.getLogs ─────────────────────────────────────
    // Dynamic filtering is handled via JpaSpecificationExecutor.findAll(Spec, Pageable)
    // Sortable fields passed via Pageable: requestedAt, httpStatus,
    //   responseTimeMs, userId, endpoint — all map directly to column names.

    // ── Used by RequestLogService.purgeLogs (@Scheduled job) ─────────────────

    /**
     * Hard-deletes log rows older than the given cutoff.
     * Returns the count of deleted rows.
     * @Modifying + @Transactional required for bulk delete JPQL.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RequestLog r WHERE r.requestedAt < :cutoff")
    int deleteByRequestedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}
