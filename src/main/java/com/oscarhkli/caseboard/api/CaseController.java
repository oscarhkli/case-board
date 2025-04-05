package com.oscarhkli.caseboard.api;

import com.oscarhkli.caseboard.CaseService;
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
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:8080",
    "https://localhost:8080",
})
public class CaseController {

    private final CaseService caseService;

    @GetMapping(value = "/v1/cases", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CasesFindResponse> getCases(@RequestHeader HttpHeaders headers) {
        log.info("getCases request: [referer: {}, user-agent: {}]",
            headers.getOrEmpty(HttpHeaders.REFERER), headers.getOrEmpty(HttpHeaders.USER_AGENT));
        var casesFindResponse = CasesFindResponse.of(caseService.findAllCases());
        log.info("getCases response size: {}", casesFindResponse.getData().size());
        return ResponseEntity.ok(casesFindResponse);
    }

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

    @PutMapping(value = "/v1/cases/{id}")
    public ResponseEntity<Boolean> updateCase(@RequestHeader HttpHeaders headers,
        @PathVariable Long id, @RequestBody @NotNull @Valid Case updatedCase) {
        log.info("updateCase request id: {} [referer: {}, user-agent: {}]", id,
            headers.getOrEmpty(HttpHeaders.REFERER), headers.getOrEmpty(HttpHeaders.USER_AGENT));
        caseService.updateCase(id, updatedCase);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping(value = "/v1/cases/{id}")
    public ResponseEntity<Void> deleteCase(@RequestHeader HttpHeaders headers,
        @PathVariable Long id) {
        log.info("deleteCase request id: {} [referer: {}, user-agent: {}]", id,
            headers.getOrEmpty(HttpHeaders.REFERER), headers.getOrEmpty(HttpHeaders.USER_AGENT));
        this.caseService.deleteCaseById(id);
        return ResponseEntity.noContent().build();
    }
}
