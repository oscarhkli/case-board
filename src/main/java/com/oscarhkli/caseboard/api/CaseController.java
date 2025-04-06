package com.oscarhkli.caseboard.api;

import com.oscarhkli.caseboard.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@AllArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080",
    "https://localhost:8080",})
public class CaseController {

    private final CaseService caseService;

    @Operation(summary = "Retrieve all cases", description = "Fetches a list of all cases.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cases"),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping(value = "/v1/cases", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CasesFindResponse> getCases(@RequestHeader HttpHeaders headers) {
        log.info("getCases request: [referer: {}, user-agent: {}]",
            headers.getOrEmpty(HttpHeaders.REFERER), headers.getOrEmpty(HttpHeaders.USER_AGENT));
        var casesFindResponse = CasesFindResponse.of(caseService.findAllCases());
        log.info("getCases response size: {}", casesFindResponse.getData().size());
        return ResponseEntity.ok(casesFindResponse);
    }

    @Operation(summary = "Retrieve a specific case", description = "Fetches a case by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the case"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "Case not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping(value = "/v1/cases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaseGetResponse> getCase(@RequestHeader HttpHeaders headers,
        @PathVariable Long id) {
        log.info("getCase request: {} [referer: {}, user-agent: {}]", id,
            headers.getOrEmpty(HttpHeaders.REFERER), headers.getOrEmpty(HttpHeaders.USER_AGENT));
        return caseService.findCaseById(id).map(CaseGetResponse::of).map(caseGetResponse -> {
            log.info("Case {} found", id);
            return ResponseEntity.ok(caseGetResponse);
        }).orElseGet(() -> {
            log.info("Case {} not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @Operation(summary = "Create a new case", description = "Inserts a new case into the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Case created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PostMapping("/v1/cases")
    public ResponseEntity<Long> insertCase(@RequestHeader HttpHeaders headers,
        @RequestBody @NotNull @Valid Case newCase) {
        log.info("insertCase request caseNumber: {} [referer: {}, user-agent: {}]",
            newCase.getCaseNumber(), headers.getOrEmpty(HttpHeaders.REFERER),
            headers.getOrEmpty(HttpHeaders.USER_AGENT));
        var caseId = caseService.insertCase(newCase);
        log.info("New case inserted with caseId: {}", caseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(caseId);
    }

    @Operation(summary = "Update an existing case", description = "Updates a case by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Case updated successfully"),
        @ApiResponse(responseCode = "404", description = "Case not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PutMapping(value = "/v1/cases/{id}")
    public ResponseEntity<Boolean> updateCase(@RequestHeader HttpHeaders headers,
        @PathVariable Long id, @RequestBody @NotNull @Valid Case updatedCase) {
        log.info("updateCase request id: {} [referer: {}, user-agent: {}]", id,
            headers.getOrEmpty(HttpHeaders.REFERER), headers.getOrEmpty(HttpHeaders.USER_AGENT));
        caseService.updateCase(id, updatedCase);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Delete a case", description = "Deletes a case by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Case deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Case not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @DeleteMapping(value = "/v1/cases/{id}")
    public ResponseEntity<Void> deleteCase(@RequestHeader HttpHeaders headers,
        @PathVariable Long id) {
        log.info("deleteCase request id: {} [referer: {}, user-agent: {}]", id,
            headers.getOrEmpty(HttpHeaders.REFERER), headers.getOrEmpty(HttpHeaders.USER_AGENT));
        this.caseService.deleteCaseById(id);
        return ResponseEntity.noContent().build();
    }
}
