package com.hcLinear.backendTest.controller;

import com.hcLinear.backendTest.dto.TransferDocumentResponse;
import com.hcLinear.backendTest.service.TransferDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/transfer-documents")
@Slf4j
@RequiredArgsConstructor
public class TransferDocumentController {

    private final TransferDocumentService transferDocumentService;

    @GetMapping("/{id}")
    public ResponseEntity<TransferDocumentResponse> findById(@PathVariable Long id) {
        log.info("HTTP GET request received to retrieve transfer document with ID: {}", id);
        TransferDocumentResponse transferDocumentResponse = transferDocumentService.findById(id);
        return new ResponseEntity<>(transferDocumentResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TransferDocumentResponse>> findAll() {
        log.info("HTTP GET request received to retrieve all transfer documents.");
        List<TransferDocumentResponse> transferDocumentResponses = transferDocumentService.findAll();
        return new ResponseEntity<>(transferDocumentResponses, HttpStatus.OK);
    }
}
