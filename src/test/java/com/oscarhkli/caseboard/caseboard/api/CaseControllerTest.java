package com.oscarhkli.caseboard.caseboard.api;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhkli.caseboard.caseboard.CaseOperationException;
import com.oscarhkli.caseboard.caseboard.CaseService;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
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

        @SneakyThrows
        @Test
        @DisplayName("""
            Given non-long id, \
            When getCase, \
            Then can return 400 with CaseGetResponse""")
        void shouldHandleBadRequestForId() {
            var response = mockMvc.perform(
                    get("/api/v1/cases/{id}", "ABC").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().message()).contains("id", "ABC");
            BDDMockito.then(caseService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Test insertCase")
    class InsertCasesTest {

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService can insert case, \
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

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService throws CaseOperationException when inserting case, \
            When insertCase, \
            Then can return 500""")
        void shouldHandleCaseOperationExceptionForInsert() {
            var newCase = Case.builder().caseNumber("CASE_001").title("Title 001")
                .description("Description 001").status("STATUS_001").build();
            var requestJson = objectMapper.writeValueAsString(newCase);

            given(caseService.insertCase(newCase)).willThrow(
                new CaseOperationException("SOME_MSG"));

            var response = mockMvc.perform(
                    post("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().is5xxServerError()).andDo(print())
                .andReturn().getResponse().getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().message()).contains("SOME_MSG");
        }

        @SneakyThrows
        @Test
        @DisplayName("""
            Given case with some missing elements, \
            When insertCase, \
            Then can return 400""")
        void shouldHandleBadRequestForInsert() {
            var newCase = Case.builder().build();
            var requestJson = objectMapper.writeValueAsString(newCase);

            var response = mockMvc.perform(
                    post("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().isBadRequest()).andDo(print())
                .andReturn().getResponse().getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().errors()).extracting("reason", "message")
                .containsExactlyInAnyOrder(tuple("caseNumber", "must not be empty"),
                    tuple("title", "must not be empty"), tuple("status", "must not be empty"));
            BDDMockito.then(caseService).shouldHaveNoInteractions();
        }

        @SneakyThrows
        @Test
        @DisplayName("""
            Given newCase is not provided, \
            When insertCase, \
            Then can return 400""")
        void shouldHandleBadRequestForUpdateWithoutNewCase() {
            var response = mockMvc.perform(
                    post("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn().getResponse()
                .getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().message()).isEqualTo("Required request body is missing");
            BDDMockito.then(caseService).shouldHaveNoInteractions();
        }
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

        @SneakyThrows
        @Test
        @DisplayName("""
            Given caseService throws CaseOperationException when updating case, \
            When updateCase, \
            Then can return 500""")
        void shouldHandleCaseOperationExceptionForUpdate() {
            var updatedCase = Case.builder().caseNumber("CASE_001").title("Title 001")
                .description("Description 001").status("STATUS_001").build();
            var requestJson = objectMapper.writeValueAsString(updatedCase);

            willThrow(new CaseOperationException("SOME_MSG")).given(caseService)
                .updateCase(1L, updatedCase);

            var response = mockMvc.perform(
                    put("/api/v1/cases/{id}", 1L).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().is5xxServerError()).andDo(print())
                .andReturn().getResponse().getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().message()).contains("SOME_MSG");
        }

        @SneakyThrows
        @Test
        @DisplayName("""
            Given case with some missing elements, \
            When updateCase, \
            Then can return 400""")
        void shouldHandleBadRequestForUpdate() {
            var updatedCase = Case.builder().build();
            var requestJson = objectMapper.writeValueAsString(updatedCase);

            var response = mockMvc.perform(
                    put("/api/v1/cases/{id}", 1L).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().isBadRequest()).andDo(print())
                .andReturn().getResponse().getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().errors()).extracting("reason", "message")
                .containsExactlyInAnyOrder(tuple("caseNumber", "must not be empty"),
                    tuple("title", "must not be empty"), tuple("status", "must not be empty"));
            BDDMockito.then(caseService).shouldHaveNoInteractions();
        }

        @SneakyThrows
        @Test
        @DisplayName("""
            Given updatedCase is not provided, \
            When updateCase, \
            Then can return 400""")
        void shouldHandleBadRequestForUpdateWithoutUpdatedCase() {
            var response = mockMvc.perform(
                    put("/api/v1/cases/{id}", 1L).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn().getResponse()
                .getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().message()).isEqualTo("Required request body is missing");
            BDDMockito.then(caseService).shouldHaveNoInteractions();
        }

        @SneakyThrows
        @Test
        @DisplayName("""
            Given non-long id, \
            When updateCase, \
            Then can return 400""")
        void shouldHandleBadRequestForId() {
            var response = mockMvc.perform(
                    put("/api/v1/cases/{id}", "ABC").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().message()).contains("id", "ABC");
            BDDMockito.then(caseService).shouldHaveNoInteractions();
        }
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

        @SneakyThrows
        @Test
        @DisplayName("""
            Given non-long id, \
            When deletCase, \
            Then can return 400""")
        void shouldHandleBadRequestForId() {
            var response = mockMvc.perform(
                    delete("/api/v1/cases/{id}", "ABC")).andExpect(status().isBadRequest())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var apiErrorResponse = objectMapper.readValue(response, ApiErrorResponse.class);
            then(apiErrorResponse.error().message()).contains("id", "ABC");
            BDDMockito.then(caseService).shouldHaveNoInteractions();
        }
    }
}
