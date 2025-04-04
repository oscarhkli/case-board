package com.oscarhkli.caseboard.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CaseGetResponse {

    Case data;

    public static CaseGetResponse of(Case singleCase) {
        return CaseGetResponse.builder().data(singleCase).build();
    }
}
