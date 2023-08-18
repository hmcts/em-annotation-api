package uk.gov.hmcts.reform.em.annotation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
public class Metadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "rotation_angle", nullable = false)
    private Integer rotationAngle;

}
