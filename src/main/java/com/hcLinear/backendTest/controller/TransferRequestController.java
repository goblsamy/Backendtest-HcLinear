package com.hcLinear.backendTest.controller;


import com.hcLinear.backendTest.dto.TransferCreateRequest;
import com.hcLinear.backendTest.dto.TransferRequestResponse;
import com.hcLinear.backendTest.service.TransferRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer-requests")
@Slf4j
@RequiredArgsConstructor
public class TransferRequestController {

    private final TransferRequestService transferRequestService;

    @PostMapping
    public ResponseEntity<TransferRequestResponse> createTransferRequest(@Valid @RequestBody TransferCreateRequest request) {
        log.info("HTTP POST request received to create a new transfer request with playerId {} and target teamId {}", request.getPlayerId(), request.getTargetTeamId());
        TransferRequestResponse transferRequestResponse = transferRequestService.create(request);
        return new ResponseEntity<>(transferRequestResponse, HttpStatus.CREATED);
    }
}
