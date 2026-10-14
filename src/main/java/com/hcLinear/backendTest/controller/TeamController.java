package com.hcLinear.backendTest.controller;

import com.hcLinear.backendTest.dto.TeamCreateRequest;
import com.hcLinear.backendTest.dto.TeamResponse;
import com.hcLinear.backendTest.dto.TransferRequestResponse;
import com.hcLinear.backendTest.service.TeamService;
import com.hcLinear.backendTest.service.TransferRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TransferRequestService transferRequestService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        log.info("HTTP POST request received to create a new team with name: {}", request.getName());
        TeamResponse teamResponse = teamService.create(request);
        return new ResponseEntity<>(teamResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> findAll() {
        log.info("HTTP GET request received to retrieve all teams.");
        List<TeamResponse> teamResponses = teamService.findAll();
        return new ResponseEntity<>(teamResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable Long id) {
        log.info("HTTP GET request received to retrieve team with ID: {}", id);
        TeamResponse teamResponse = teamService.findById(id);
        return new ResponseEntity<>(teamResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("HTTP DELETE request received to delete team with ID: {}", id);
        teamService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{teamId}/captain/{playerId}")
    public ResponseEntity<TeamResponse> appointCaptain(@PathVariable Long teamId, @PathVariable Long playerId) {
        log.info("HTTP PUT request received to assign captain with playerId {} and teamId {}", playerId, teamId);
        TeamResponse teamResponse = teamService.appointCaptain(teamId, playerId);
        return new ResponseEntity<>(teamResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}/transfer-requests/outgoing")
    public ResponseEntity<List<TransferRequestResponse>> listOutgoingRequestsByTeam(@PathVariable Long id) {
        log.info("HTTP GET request received to retrieve all outgoing transfer requests of team with id {}", id);
        List<TransferRequestResponse> transferRequestResponses = transferRequestService.listRequestsBySourceTeam(id);
        return new ResponseEntity<>(transferRequestResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}/transfer-requests/incoming")
    public ResponseEntity<List<TransferRequestResponse>> listIncomingRequestsByTeam(@PathVariable Long id) {
        log.info("HTTP GET request received to retrieve all incoming transfer requests of team with id {}", id);
        List<TransferRequestResponse> transferRequestResponses = transferRequestService.listRequestsByTargetTeam(id);
        return new ResponseEntity<>(transferRequestResponses, HttpStatus.OK);
    }


}
