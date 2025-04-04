package com.oscarhkli.caseboard.caseboard.api;

import com.oscarhkli.caseboard.caseboard.entity.CaseEntity;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Case {

    Long id;
    @NotEmpty
    String caseNumber;
    @NotEmpty
    String title;
    String description;
    @NotEmpty
    String status;
    LocalDateTime createdDateTime;
    LocalDateTime lastModifiedDateTime;

    public CaseEntity toEntity() {
        return CaseEntity.builder().caseNumber(this.caseNumber).title(this.title)
            .description(this.description).status(this.status).build();
    }

    public static Case of(CaseEntity caseEntity) {
        return Case.builder().id(caseEntity.getId()).caseNumber(caseEntity.getCaseNumber())
            .title(caseEntity.getTitle()).description(caseEntity.getDescription())
            .status(caseEntity.getStatus()).createdDateTime(caseEntity.getCreatedDateTime())
            .lastModifiedDateTime(caseEntity.getLastModifiedDateTime()).build();
    }
}
