package com.hcLinear.backendTest.controller;


import com.hcLinear.backendTest.dto.TransferCreateRequest;
import com.hcLinear.backendTest.dto.TransferRequestResponse;
import com.hcLinear.backendTest.service.TransferRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{transferRequestId}/reject")
    public ResponseEntity<TransferRequestResponse> rejectTransferRequest(@PathVariable Long transferRequestId) {
        log.info("HTTP POST request received to reject transfer request with id {}", transferRequestId);
        TransferRequestResponse transferRequestResponse = transferRequestService.reject(transferRequestId);
        return new ResponseEntity<>(transferRequestResponse, HttpStatus.OK);
    }

    @PostMapping("/{transferRequestId}/accept")
    public ResponseEntity<TransferRequestResponse> acceptTransferRequest(@PathVariable Long transferRequestId) {
        log.info("HTTP POST request received to accept transfer request with id {}", transferRequestId);
        TransferRequestResponse transferRequestResponse = transferRequestService.accept(transferRequestId);
        return new ResponseEntity<>(transferRequestResponse, HttpStatus.ACCEPTED);


    }
}
