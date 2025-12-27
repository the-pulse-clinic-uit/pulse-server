package com.pulseclinic.pulse_server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDetail {
    private String path;
    private String exception;
    private String message;
    private LocalDateTime timestamp;
}
