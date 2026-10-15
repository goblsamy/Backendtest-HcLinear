package com.hcLinear.backendTest.service;

import com.hcLinear.backendTest.dto.TransferDocumentResponse;
import com.hcLinear.backendTest.exceptionhandling.TransferDocumentNotFoundException;
import com.hcLinear.backendTest.mapper.TransferDocumentMapper;
import com.hcLinear.backendTest.model.TransferDocument;
import com.hcLinear.backendTest.model.TransferRequest;
import com.hcLinear.backendTest.repository.TransferDocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferDocumentService {

    private static final String PREFIX = "TRFR";
    private static final String SUFFIX = "HU";

    private final TransferDocumentRepository repository;
    private final TransferDocumentMapper mapper;


    public void createForAcceptedRequest(TransferRequest transferRequest, LocalDateTime transferTime) {
        Long nextSerialNumber = repository.nextSequenceNum();
        String docNumber = String.format("%s%06d%s", PREFIX, nextSerialNumber, SUFFIX);
        TransferDocument transferDocument = new TransferDocument();
        transferDocument.setDocumentNumber(docNumber);
        transferDocument.setTransferRequest(transferRequest);
        transferDocument.setPlayer(transferRequest.getPlayer());
        transferDocument.setSourceTeam(transferRequest.getSourceTeam());
        transferDocument.setTargetTeam(transferRequest.getTargetTeam());
        transferDocument.setAmount(transferRequest.getAmount());
        transferDocument.setTransferredAt(transferTime);
        repository.save(transferDocument);
    }

    public List<TransferDocumentResponse> findAll() {
        List<TransferDocument> transferDocuments = repository.findAll();
        return transferDocuments.stream().map(mapper::toResponse).toList();
    }

    public TransferDocumentResponse findById(Long transferDocumentId) {
        TransferDocument transferDocument = repository.findById(transferDocumentId).orElseThrow(() -> new TransferDocumentNotFoundException(transferDocumentId));
        return mapper.toResponse(transferDocument);
    }
}
