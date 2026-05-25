package com.symmetricsquad.hms_backend.dto.request;

import lombok.Data;

@Data
public class ContactQueryRequest {
    private String fullName;
    private String email;
    private Long contactNo;
    private String message;
}