package com.oscarhkli.caseboard.caseboard.api;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhkli.caseboard.caseboard.entity.CaseEntity;
import com.oscarhkli.caseboard.caseboard.entity.CaseRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class CaseComponentTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CaseRepository caseRepository;

    CustomComparator comparator = new CustomComparator(JSONCompareMode.LENIENT,
        Customization.customization("data.id", (o1, o2) -> true),
        Customization.customization("data.createdDateTime", (o1, o2) -> true),
        Customization.customization("data.lastModifiedDateTime", (o1, o2) -> true));

    @BeforeEach
    void init() {
        caseRepository.deleteAll();
        var caseEntities = Stream.of(1, 2, 3, 4, 5).map(
            i -> CaseEntity.builder().caseNumber("CASE%03d".formatted(i))
                .title("Title %d".formatted(i)).description("Desc %d".formatted(i))
                .status("Status %d".formatted(i))
                .createdDateTime(LocalDateTime.now(Clock.systemUTC()))
                .lastModifiedDateTime(LocalDateTime.now(Clock.systemUTC())).build()).toList();
        caseRepository.saveAll(caseEntities);
    }

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
            var response = mockMvc.perform(
                    get("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var expected = """
                {
                  "data": [
                    {
                      "caseNumber": "CASE001",
                      "title": "Title 1",
                      "description": "Desc 1",
                      "status": "Status 1"
                    },
                    {
                      "caseNumber": "CASE002",
                      "title": "Title 2",
                      "description": "Desc 2",
                      "status": "Status 2"
                    },
                    {
                      "caseNumber": "CASE003",
                      "title": "Title 3",
                      "description": "Desc 3",
                      "status": "Status 3"
                    },
                    {
                      "caseNumber": "CASE004",
                      "title": "Title 4",
                      "description": "Desc 4",
                      "status": "Status 4"
                    },
                    {
                      "caseNumber": "CASE005",
                      "title": "Title 5",
                      "description": "Desc 5",
                      "status": "Status 5"
                    }
                  ]
                }""";
            JSONAssert.assertEquals(expected, response, comparator);
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
            var id = caseRepository.findAll().getFirst().getId();

            var response = mockMvc.perform(
                    get("/api/v1/cases/{id}", id).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse().getContentAsString();

            var expected = """
                {
                  "data": {
                    "id": 22,
                    "caseNumber": "CASE001",
                    "title": "Title 1",
                    "description": "Desc 1",
                    "status": "Status 1",
                    "createdDateTime": "2025-04-04T18:43:57.168638",
                    "lastModifiedDateTime": "2025-04-04T18:43:57.168638"
                  }
                }""";
            JSONAssert.assertEquals(expected, response, comparator);
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
            var newCase = Case.builder().caseNumber("CASE_008").title("Title 008")
                .description("Description 008").status("STATUS_008").build();
            var requestJson = objectMapper.writeValueAsString(newCase);

            var response = mockMvc.perform(
                    post("/api/v1/cases").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().isCreated()).andDo(print())
                .andReturn().getResponse().getContentAsString();

            var actual = Long.parseLong(response);
            var allCases = caseRepository.findAll();
            allCases.sort(Comparator.comparing(CaseEntity::getCreatedDateTime));

            then(allCases.getLast().getId()).isEqualTo(actual);
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
            var caseEntity = caseRepository.findAll().getFirst();
            var id = caseEntity.getId();

            var updatedCase = Case.builder().caseNumber("CASE____001").title("Title_____001")
                .description("Description 001").status("STATUS_001").build();
            var requestJson = objectMapper.writeValueAsString(updatedCase);

            var response = mockMvc.perform(
                    put("/api/v1/cases/{id}", id).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)).andExpect(status().isOk()).andDo(print()).andReturn()
                .getResponse().getContentAsString();

            var actual = Boolean.parseBoolean(response);
            then(actual).isTrue();

            var updatedCaseEntity = caseRepository.findById(id);
            then(updatedCaseEntity).isPresent();
            then(updatedCaseEntity.get().getCaseNumber()).isEqualTo(
                caseEntity.getCaseNumber()); // Should not update
            then(updatedCaseEntity.get().getTitle()).isEqualTo("Title_____001");
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
        void deleteCase() {
            var id = caseRepository.findAll().getFirst().getId();

            var response = mockMvc.perform(delete("/api/v1/cases/{id}", id))
                .andExpect(status().isNoContent()).andDo(print()).andReturn().getResponse()
                .getContentAsString();

            then(response).isEmpty();
            var target = caseRepository.findById(id);
            then(target).isNotPresent();
        }

    }
}
