package com.oscarhkli.caseboard.caseboard.api;

import java.util.List;

public record ApiErrorResponse(ApiError error) {

    public record ApiError(String code, String message, List<ErrorDetails> errors) {

    }

    public record ErrorDetails(String reason, String message) {

    }
}
