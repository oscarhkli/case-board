package com.oscarhkli.caseboard.api;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Clock;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CaseGetResponseTest {

    @Test
    void fromEntity() {
        var now = LocalDateTime.now(Clock.systemUTC());
        var singleCase = Case.builder().id(1L).caseNumber("caseNumber").title("title")
            .description("description").status("status").createdDateTime(now)
            .lastModifiedDateTime(now).build();

        var actual = CaseGetResponse.of(singleCase);
        var expected = CaseGetResponse.builder().data(
            Case.builder().id(1L).caseNumber("caseNumber").title("title").description("description")
                .status("status").createdDateTime(now).lastModifiedDateTime(now).build()).build();
        then(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
