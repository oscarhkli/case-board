package com.oscarhkli.caseboard.caseboard.api;

import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class CasesFindResponse {

    @Singular("singleCase")
    List<Case> data;

    public static CasesFindResponse of(List<Case> cases) {
        return CasesFindResponse.builder().data(cases).build();
    }
}
