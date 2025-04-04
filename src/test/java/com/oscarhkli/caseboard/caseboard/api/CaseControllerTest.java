package com.oscarhkli.caseboard.caseboard.api;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhkli.caseboard.caseboard.CaseService;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CaseController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class CaseControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    CaseService caseService;

    @Nested
    @DisplayName("Test getCases")
    class GetCasesTest {

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService can return some cases, \
            When getCases, \
            Then can return 200 with CasesFindResponse""")
        void getCasesFindResponse() {
            var fakeCase1 = Case.builder().id(1L).build();
            var fakeCase2 = Case.builder().id(2L).build();

            given(caseService.findAllCases()).willReturn(List.of(fakeCase1, fakeCase2));

            var response = mockMvc.perform(
                    get("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var casesFindResponse = objectMapper.readValue(response, CasesFindResponse.class);
            var expected = CasesFindResponse.builder().singleCase(fakeCase1).singleCase(fakeCase2)
                .build();
            then(casesFindResponse).usingRecursiveComparison().isEqualTo(expected);
        }

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService can find any case, \
            When getCases, \
            Then can return 200 with empty list""")
        void getCasesFindEmptyResponse() {
            given(caseService.findAllCases()).willReturn(List.of());

            var response = mockMvc.perform(
                    get("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var casesFindResponse = objectMapper.readValue(response, CasesFindResponse.class);
            var expected = CasesFindResponse.builder().build();
            then(casesFindResponse).usingRecursiveComparison().isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Test getCase")
    class GetCaseTest {

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService can find case by id, \
            When getCase, \
            Then can return 200 with CaseGetResponse""")
        void getCasesFindResponse() {
            var fakeCase1 = Case.builder().id(1L).build();

            given(caseService.findCaseById(1L)).willReturn(Optional.of(fakeCase1));

            var response = mockMvc.perform(
                    get("/api/v1/cases/{id}", 1L).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var caseGetResponse = objectMapper.readValue(response, CaseGetResponse.class);
            var expected = CaseGetResponse.builder().data(fakeCase1).build();
            then(caseGetResponse).usingRecursiveComparison().isEqualTo(expected);
        }

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService cannot find case by id, \
            When getCase, \
            Then can return 404""")
        void getCasesFindEmptyResponse() {
            given(caseService.findCaseById(2L)).willReturn(Optional.empty());

            var response = mockMvc.perform(
                    get("/api/v1/cases/{id}", 2L).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            then(response).isEmpty();
        }
    }

    @Nested
    @DisplayName("Test insertCase")
    class InsertCasesTest {

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService can return some cases, \
            When insertCase, \
            Then can return 201 with new case id""")
        void insertCase() {
            var newCase = Case.builder().caseNumber("CASE_001").title("Title 001")
                .description("Description 001").status("STATUS_001").build();
            var requestJson = objectMapper.writeValueAsString(newCase);

            given(caseService.insertCase(newCase)).willReturn(100L);

            var response = mockMvc.perform(
                    post("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().isCreated()).andDo(print())
                .andReturn().getResponse().getContentAsString();

            var actual = Long.parseLong(response);
            then(actual).isEqualTo(100L);
        }

        // TODO: insert error
    }

    @Nested
    @DisplayName("Test updateCase")
    class UpdateCasesTest {

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService id and updatedCase can find and update case, \
            When updateCase, \
            Then can return 200 with true""")
        void updateCase() {
            var id = 2L;
            var updatedCase = Case.builder().caseNumber("CASE_001").title("Title 001")
                .description("Description 001").status("STATUS_001").build();
            var requestJson = objectMapper.writeValueAsString(updatedCase);

            var response = mockMvc.perform(
                    put("/api/v1/cases/{id}", id).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().isOk()).andDo(print()).andReturn()
                .getResponse().getContentAsString();

            var actual = Boolean.parseBoolean(response);
            then(actual).isTrue();
        }

        // TODO: update error
    }

    @Nested
    @DisplayName("Test deleteCase")
    class DeleteCasesTest {

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService id can delete case, \
            When deleteCase, \
            Then can return 204""")
        void updateCase() {
            var id = 2L;

            var response = mockMvc.perform(delete("/api/v1/cases/{id}", id))
                .andExpect(status().isNoContent()).andDo(print()).andReturn().getResponse()
                .getContentAsString();

            then(response).isEmpty();
        }
    }
}
