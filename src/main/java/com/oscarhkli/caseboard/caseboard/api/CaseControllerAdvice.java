package com.oscarhkli.caseboard.caseboard.api;

import com.oscarhkli.caseboard.caseboard.CaseOperationException;
import com.oscarhkli.caseboard.caseboard.api.ApiErrorResponse.ApiError;
import com.oscarhkli.caseboard.caseboard.api.ApiErrorResponse.ErrorDetails;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(annotations = RestController.class, assignableTypes = {CaseController.class})
public class CaseControllerAdvice {

    @ExceptionHandler(value = {CaseOperationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorResponse> handleCaseOperation(CaseOperationException ex) {
        var errors = new ArrayList<ErrorDetails>();
        if (ex.getCause() != null) {
            errors.add(new ErrorDetails(ex.getCause().getClass().getSimpleName(),
                ex.getCause().getMessage()));
        }
        var errorResponse = new ApiErrorResponse(
            new ApiError(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                ex.getMessage(), errors));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
        final MethodArgumentTypeMismatchException ex) {
        var errors = new ArrayList<ErrorDetails>();
        errors.add(new ErrorDetails(ex.getCause().getClass().getSimpleName(),
            "%s %s".formatted(ex.getParameter().getParameterName(), ex.getValue())));
        var errorResponse = new ApiErrorResponse(
            new ApiError(Integer.toString(HttpStatus.BAD_REQUEST.value()), ex.getLocalizedMessage(),
                errors));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
        final MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getAllErrors().stream().map(
                error -> new ErrorDetails(((FieldError) error).getField(), error.getDefaultMessage()))
            .toList();
        var errorResponse = new ApiErrorResponse(
            new ApiError(Integer.toString(HttpStatus.BAD_REQUEST.value()), ex.getLocalizedMessage(),
                errors));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
        final HttpMessageNotReadableException ex) {
        var errorResponse = new ApiErrorResponse(
            new ApiError(Integer.toString(HttpStatus.BAD_REQUEST.value()),
                "Required request body is missing", List.of()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}
