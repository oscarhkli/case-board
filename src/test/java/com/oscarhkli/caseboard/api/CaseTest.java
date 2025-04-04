package com.oscarhkli.caseboard.api;

import static org.assertj.core.api.BDDAssertions.then;

import com.oscarhkli.caseboard.entity.CaseEntity;
import java.time.Clock;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CaseTest {

    @Test
    void toEntity() {
        var now = LocalDateTime.now(Clock.systemUTC());
        var singleCase = Case.builder().id(1L).caseNumber("caseNumber").title("title")
            .description("description").status("status").createdDateTime(now)
            .lastModifiedDateTime(now).build();

        var actual = singleCase.toEntity();
        var expected = CaseEntity.builder().caseNumber("caseNumber").title("title")
            .description("description").status("status").build();
        then(expected).isEqualTo(actual);
    }

    @Test
    void fromEntity() {
        var now = LocalDateTime.now(Clock.systemUTC());
        var caseEntity = CaseEntity.builder().id(1L).caseNumber("caseNumber").title("title")
            .description("description").status("status").createdDateTime(now)
            .lastModifiedDateTime(now).build();

        var actual = Case.of(caseEntity);
        var expected = Case.builder().id(1L).caseNumber("caseNumber").title("title")
            .description("description").status("status").createdDateTime(now)
            .lastModifiedDateTime(now).build();
        then(expected).usingRecursiveComparison().isEqualTo(actual);
    }
}
