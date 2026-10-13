package com.hcLinear.backendTest.controller;

import com.hcLinear.backendTest.dto.TeamCreateRequest;
import com.hcLinear.backendTest.dto.TeamResponse;
import com.hcLinear.backendTest.service.TeamService;
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
}
