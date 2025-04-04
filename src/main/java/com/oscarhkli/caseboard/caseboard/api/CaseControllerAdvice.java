package com.oscarhkli.caseboard.caseboard.api;

import com.oscarhkli.caseboard.caseboard.CaseOperationException;
import com.oscarhkli.caseboard.caseboard.api.ApiErrorResponse.ApiError;
import com.oscarhkli.caseboard.caseboard.api.ApiErrorResponse.ErrorDetails;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@RestControllerAdvice(annotations = RestController.class, assignableTypes = {CaseController.class})
public class CaseControllerAdvice {

    @ExceptionHandler(value = {CaseOperationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorResponse> handleUpdateException(CaseOperationException ex) {
        var errorResponse = new ApiErrorResponse(
            new ApiError(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                ex.getMessage(), ex.getCause() != null ? List.of(
                new ErrorDetails(ex.getCause().getClass().getSimpleName(),
                    ex.getCause().getMessage())) : List.of()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
