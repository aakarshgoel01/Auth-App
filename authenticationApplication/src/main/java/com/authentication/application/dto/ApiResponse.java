package com.authentication.application.dto;

import java.time.OffsetDateTime;

public record ApiResponse(
        int status,
        String messgae,
        String error,
        String path,
        OffsetDateTime timeStamp
) {

    public static ApiResponse of(int status, String messgae, String error, String path,OffsetDateTime timeStamp) {
        return new ApiResponse(status, messgae, error, path, timeStamp);
    }

}
