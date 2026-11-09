package com.authentication.application.dto;

public record ErrorRespose(    String status,
                               String message,
                               String error
                               ) {
}
