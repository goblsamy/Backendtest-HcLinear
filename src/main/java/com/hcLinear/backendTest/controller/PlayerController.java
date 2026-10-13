package com.hcLinear.backendTest.controller;

import com.hcLinear.backendTest.dto.PlayerCreateRequest;
import com.hcLinear.backendTest.dto.PlayerResponse;
import com.hcLinear.backendTest.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@Slf4j
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerCreateRequest request) {
        log.info("HTTP POST request received to create a new player with name: {}", request.getFirstName() + " " + request.getLastName());
        PlayerResponse playerResponse = playerService.create(request);
        return new ResponseEntity<>(playerResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> findAll() {
        log.info("HTTP GET request received to retrieve all players.");
        List<PlayerResponse> playerResponses = playerService.findAll();
        return new ResponseEntity<>(playerResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    private ResponseEntity<PlayerResponse> findById(@PathVariable Long id) {
        log.info("HTTP GET request received to retrieve player with ID: {}", id);
        PlayerResponse playerResponse = playerService.findById(id);
        return new ResponseEntity<>(playerResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("HTTP DELETE request received to delete player with ID: {}", id);
        playerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
