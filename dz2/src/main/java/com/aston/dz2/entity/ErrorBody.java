package com.aston.dz2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@ToString
public class ErrorBody {
    private int errorCode;
    private String message;
    private String details;
    private LocalDateTime timestamp;
}
