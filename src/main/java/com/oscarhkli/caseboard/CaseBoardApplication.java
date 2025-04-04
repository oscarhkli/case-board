package com.oscarhkli.caseboard;

import com.oscarhkli.caseboard.entity.CaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class CaseBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseBoardApplication.class, args);
    }
}

@Component
@AllArgsConstructor
class Initializer {

    private final CaseRepository caseRepository;

    @EventListener(ApplicationReadyEvent.class)
    void reset() {
//        this.caseRepository.deleteAll();
//        var cases = Stream.of(1, 2, 3, 4, 5).map(i -> CaseEntity.builder()
//            //                                    .id(i)
//            .caseNumber("CASE%03d".formatted(i)).title("Title %d".formatted(i))
//            .description("Desc %d".formatted(i)).status("Status %d".formatted(i))
//            .createdDateTime(LocalDateTime.now(Clock.systemUTC()))
//            .lastModifiedDateTime(LocalDateTime.now(Clock.systemUTC())).build()).toList();
//        caseRepository.saveAll(cases);
    }
}
