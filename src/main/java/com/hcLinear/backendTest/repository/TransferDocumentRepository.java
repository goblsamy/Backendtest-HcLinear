package com.hcLinear.backendTest.repository;

import com.hcLinear.backendTest.model.TransferDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferDocumentRepository extends JpaRepository<TransferDocument, Long> {

    @Query(value = "SELECT nextval('transfer_document_number_seq')", nativeQuery = true)
    Long nextSequenceNum();

}
