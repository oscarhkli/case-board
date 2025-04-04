package com.oscarhkli.caseboard.api;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class CasesFindResponseTest {

    @Test
    void fromEntity() {
        var now = LocalDateTime.now(Clock.systemUTC());
        var singleCase1 = Case.builder().id(1L).caseNumber("caseNumber").title("title")
            .description("description").status("status").createdDateTime(now)
            .lastModifiedDateTime(now).build();
        var singleCase2 = Case.builder().id(2L).caseNumber("caseNumber2").title("title2")
            .description("description2").status("status2").createdDateTime(now.plusDays(1))
            .lastModifiedDateTime(now.plusDays(2)).build();

        var actual = CasesFindResponse.of(List.of(singleCase1, singleCase2));
        var expected = CasesFindResponse.builder().singleCase(
                Case.builder().id(1L).caseNumber("caseNumber").title("title").description("description")
                    .status("status").createdDateTime(now).lastModifiedDateTime(now).build())
            .singleCase(Case.builder().id(2L).caseNumber("caseNumber2").title("title2")
                .description("description2").status("status2").createdDateTime(now.plusDays(1))
                .lastModifiedDateTime(now.plusDays(2)).build()).build();
        then(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
