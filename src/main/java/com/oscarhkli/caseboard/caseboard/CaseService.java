package com.oscarhkli.caseboard.caseboard;

import com.oscarhkli.caseboard.caseboard.api.Case;
import com.oscarhkli.caseboard.caseboard.entity.CaseRepository;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;

    public List<Case> findAllCases() {
        return caseRepository.findAll().stream().map(Case::of).toList();
    }

    public Optional<Case> findCaseById(Long id) {
        return caseRepository.findById(id).map(Case::of);
    }

    public Long insertCase(Case newCase) {
        try {
            return caseRepository.save(newCase.toEntity()).getId();
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new CaseOperationException(
                "Case number %s cannot be reused".formatted(newCase.getCaseNumber()));
        }
    }

    public void updateCase(long id, Case updatedCase) {
        caseRepository.findById(id).ifPresentOrElse(caseEntity -> {
            caseEntity.setTitle(updatedCase.getTitle());
            caseEntity.setDescription(updatedCase.getDescription());
            caseEntity.setStatus(updatedCase.getStatus());
            caseRepository.save(caseEntity);
        }, () -> {
            throw new CaseOperationException("Case <id: %d> not found".formatted(id));
        });
    }

    public void deleteCaseById(Long id) {
        caseRepository.deleteById(id);
    }
}
