package com.oscarhkli.caseboard.caseboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cases", indexes = {
    @Index(name = "idx_cases_id", columnList = "id")}, uniqueConstraints = {
    @UniqueConstraint(columnNames = "case_number")})
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "case_number", unique = true)
    String caseNumber;

    @Column(nullable = false)
    String title;

    @Column
    String description;

    @Column(nullable = false)
    String status;

    @CreatedDate
    @Column(name = "created_datetime")
    LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name = "last_modified_datetime")
    LocalDateTime lastModifiedDateTime;
}
