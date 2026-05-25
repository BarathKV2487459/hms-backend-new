package com.symmetricsquad.hms_backend.repository;

import com.symmetricsquad.hms_backend.model.ContactQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactQueryRepository extends JpaRepository<ContactQuery, Long> {

    // ── Used by AdminService.getAllContactQueries (isRead filter) ─────────────

    Page<ContactQuery> findByIsRead(Boolean isRead, Pageable pageable);

    long countByIsRead(Boolean isRead);
}
