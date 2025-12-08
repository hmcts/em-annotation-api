package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Metadata;

import java.util.UUID;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {

    Metadata findByDocumentId(UUID documentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Metadata m WHERE m.documentId = :documentId")
    void deleteAllByDocumentId(UUID documentId);
}