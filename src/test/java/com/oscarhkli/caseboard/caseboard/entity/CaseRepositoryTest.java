package com.oscarhkli.caseboard.caseboard.entity;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

import com.oscarhkli.caseboard.caseboard.config.JpaConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
@Import(JpaConfiguration.class)
class CaseRepositoryTest {

    @Autowired
    CaseRepository caseRepository;

    @BeforeEach
    void init() {
        caseRepository.deleteAll();
    }

    @Nested
    @DisplayName("Test constraint")
    class ConstraintTest {

        @Test
        void insertOnce() {
            var caseEntity = CaseEntity.builder().caseNumber("CASE_001").title("Title 001")
                .description("Description 001").status("Status 001").build();

            var saved = caseRepository.save(caseEntity);

            var actual = caseRepository.findById(saved.getId());
            then(actual).contains(saved);
        }

        @Test
        void insertWithSameCaseNumberTwice() {
            var caseEntity1 = CaseEntity.builder().caseNumber("CASE_001").title("Title 001")
                .description("Description 001").status("Status 001").build();

            var caseEntity2 = CaseEntity.builder().caseNumber("CASE_001").title("Title 001")
                .description("Description 001").status("Status 001").build();

            caseRepository.save(caseEntity1);
            var thrown = catchThrowableOfType(DataIntegrityViolationException.class,
                () -> caseRepository.save(caseEntity2));
            then(thrown).hasMessageContainingAll("Unique index or primary key violation",
                "CASE_NUMBER");
        }
    }
}
