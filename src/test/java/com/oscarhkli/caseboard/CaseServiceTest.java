package com.oscarhkli.caseboard;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

import com.oscarhkli.caseboard.api.Case;
import com.oscarhkli.caseboard.entity.CaseEntity;
import com.oscarhkli.caseboard.entity.CaseRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @InjectMocks
    CaseService caseService;
    @Mock
    CaseRepository caseRepository;

    @Nested
    @DisplayName("Test findAllCases")
    class FindAllCasesTest {

        @Test
        @DisplayName("""
            Given caseRepository can return some caseEntities, \
            When findAllCases, \
            Then can return all cases""")
        void findAllCases() {
            var fakeCaseEntity1 = CaseEntity.builder().id(1L).build();
            var fakeCaseEntity2 = CaseEntity.builder().id(2L).build();
            var fakeCaseEntity3 = CaseEntity.builder().id(3L).build();
            given(caseRepository.findAll()).willReturn(
                List.of(fakeCaseEntity1, fakeCaseEntity2, fakeCaseEntity3));

            var cases = caseService.findAllCases();

            var expectedCase1 = Case.builder().id(1L).build();
            var expectedCase2 = Case.builder().id(2L).build();
            var expectedCase3 = Case.builder().id(3L).build();
            then(cases).containsExactlyInAnyOrder(expectedCase1, expectedCase2, expectedCase3);
        }

        @Test
        @DisplayName("""
            Given caseRepository can return nothing, \
            When findAllCases, \
            Then can return a empty list""")
        void findAllCasesForEmptyRepository() {
            given(caseRepository.findAll()).willReturn(List.of());

            var cases = caseService.findAllCases();

            then(cases).isEmpty();
        }
    }

    @Nested
    @DisplayName("Test findCaseById")
    class FindCaseByIdTest {

        @Test
        @DisplayName("""
            Given caseRepository can return 1 caseEntity by id, \
            When findCaseById, \
            Then can return the found case""")
        void findCaseById() {
            var id = 1L;

            var fakeCaseEntity1 = CaseEntity.builder().id(id).build();
            given(caseRepository.findById(id)).willReturn(Optional.of(fakeCaseEntity1));

            var caseOpt = caseService.findCaseById(id);

            var expectedCase1 = Case.builder().id(id).build();
            then(caseOpt).isPresent().contains(expectedCase1);
        }

        @Test
        @DisplayName("""
            Given caseRepository cannot find a matched caseEntity by id, \
            When findCaseById, \
            Then can return Optional empty""")
        void getOptionalEmptyForNotFoundId() {
            var id = 1L;

            given(caseRepository.findById(id)).willReturn(Optional.empty());

            var caseOpt = caseService.findCaseById(id);

            then(caseOpt).isEmpty();
        }
    }

    @Nested
    @DisplayName("Test insertCase")
    class InsertCaseTest {

        @Test
        @DisplayName("""
            Given newCase, \
            When insertCase, \
            Then can insert caseEntity and return caseId""")
        void insertCase() {
            var newCase = spy(
                Case.builder().caseNumber("caseNumber2").title("title2").description("description2")
                    .status("status2").build());

            var fakeCaseEntity = CaseEntity.builder().caseNumber("caseNumber2").build();
            var fakeSavedCaseEntity = CaseEntity.builder().id(3L).build();
            willReturn(fakeCaseEntity).given(newCase).toEntity();
            given(caseRepository.save(fakeCaseEntity)).willReturn(fakeSavedCaseEntity);

            var actual = caseService.insertCase(newCase);

            then(actual).isEqualTo(3L);
        }

        @Test
        @DisplayName("""
            Given newCase, \
            When insertCase throws Exception, \
            Then can throw CaseOperationException""")
        void canThrowsCaseOperationExceptionForInsertException() {
            var newCase = spy(
                Case.builder().caseNumber("caseNumber2").title("title2").description("description2")
                    .status("status2").build());

            var fakeCaseEntity = CaseEntity.builder().caseNumber("caseNumber2").build();
            willReturn(fakeCaseEntity).given(newCase).toEntity();
            given(caseRepository.save(fakeCaseEntity)).willThrow(
                new DataIntegrityViolationException("Some message"));

            var thrown = catchThrowableOfType(CaseOperationException.class,
                () -> caseService.insertCase(newCase));
            then(thrown).hasMessageContainingAll("Case number caseNumber2 cannot be reused");
        }
    }

    @Nested
    @DisplayName("Test updateCase")
    class UpdateCaseTest {

        @Captor
        ArgumentCaptor<CaseEntity> caseEntityCaptor;

        @Test
        @DisplayName("""
            Given caseRepository can find by id, \
            When updateCase, \
            Then can update caseEntity using id and updatedCase, ignoring updatedCase.id""")
        void updateCase() {
            var id = 2L;
            var updatedCase = Case.builder().id(123445L).caseNumber("caseNumber2").title("title2")
                .description("description2").status("status2").build();

            var now = LocalDateTime.now(Clock.systemUTC());
            var fakeCaseEntity = CaseEntity.builder().id(id).caseNumber("caseNumber").title("title")
                .description("description").status("status").createdDateTime(now)
                .lastModifiedDateTime(now).build();
            given(caseRepository.findById(id)).willReturn(Optional.of(fakeCaseEntity));

            caseService.updateCase(id, updatedCase);

            BDDMockito.then(caseRepository).should().save(caseEntityCaptor.capture());
            var actual = caseEntityCaptor.getValue();
            var expected = CaseEntity.builder().id(id).caseNumber("caseNumber").title("title2")
                .description("description2").status("status2").createdDateTime(now)
                .lastModifiedDateTime(now).build();
            then(actual).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("""
            Given caseRepository cannot find by id, \
            When updateCase, \
            Then can throw UpdateException""")
        void canThrowUpdateExceptionWhenNotFoundId() {
            var id = 1L;
            var updatedCase = Case.builder().id(id).caseNumber("caseNumber2").title("title2")
                .description("description2").status("status2").build();

            given(caseRepository.findById(id)).willReturn(Optional.empty());

            var thrown = catchThrowableOfType(CaseOperationException.class,
                () -> caseService.updateCase(id, updatedCase));
            then(thrown).hasMessage("Case <id: 1> not found");
            BDDMockito.then(caseRepository).should(never()).save(any(CaseEntity.class));
        }
    }

    @Nested
    @DisplayName("Test deleteByCaseId")
    class DeleteByCaseIdTest {

        @Test
        @DisplayName("""
            Given id, \
            When deleteByCaseId, \
            Then can call caseRepository to deleteById no matter even if id does not exist""")
        void getOptionalEmptyForNotFoundId() {
            var id = 1L;

            caseService.deleteCaseById(id);

            BDDMockito.then(caseRepository).should().deleteById(id);
        }
    }
}
