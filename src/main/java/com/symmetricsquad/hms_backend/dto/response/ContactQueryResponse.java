package com.symmetricsquad.hms_backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContactQueryResponse {
    private Long id;
    private String fullName;
    private String email;
    private Long contactNo;
    private String message;
    private String adminRemark;
    private Boolean isRead;
    private String handledByAdminName;  // null until an admin responds
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}