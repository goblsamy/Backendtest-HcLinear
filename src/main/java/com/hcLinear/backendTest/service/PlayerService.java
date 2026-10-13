package com.hcLinear.backendTest.service;

import com.hcLinear.backendTest.dto.PlayerCreateRequest;
import com.hcLinear.backendTest.dto.PlayerResponse;
import com.hcLinear.backendTest.exceptionhandling.FieldValidationException;
import com.hcLinear.backendTest.exceptionhandling.PlayerNotFoundException;
import com.hcLinear.backendTest.exceptionhandling.ShirtNumberAlreadyExistsException;
import com.hcLinear.backendTest.exceptionhandling.TeamNotFoundException;
import com.hcLinear.backendTest.mapper.PlayerMapper;
import com.hcLinear.backendTest.mapper.TeamMapper;
import com.hcLinear.backendTest.model.Player;
import com.hcLinear.backendTest.model.Team;
import com.hcLinear.backendTest.repository.PlayerRepository;
import com.hcLinear.backendTest.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PlayerMapper playerMapper;
    private static final int MIN_PLAYER_AGE = 15;


    public PlayerResponse create(PlayerCreateRequest request) {
        validatePlayerBirthDate(request.getBirthDate());
        Team team = findTeamWithIdIfExists(request.getTeamId());
        validatePlayerShirtNumberIsUnique(request.getShirtNumber(), request.getTeamId());
        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();
        Player player = playerMapper.toEntity(request);
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setTeam(team);
        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponse(savedPlayer);

    }

    private void validatePlayerShirtNumberIsUnique(Integer shirtNumber, Long teamId) {
        if (shirtNumber != null && teamId != null && playerRepository.existsByShirtNumberAndTeamId(shirtNumber, teamId)) {
            throw new ShirtNumberAlreadyExistsException(shirtNumber);
        }
    }

    private void validatePlayerBirthDate(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < MIN_PLAYER_AGE) {
            throw new FieldValidationException("birthDate", "Age must be older than or equal to " + MIN_PLAYER_AGE);
        }
    }

    private Team findTeamWithIdIfExists(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
    }

    public PlayerResponse findById(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        return playerMapper.toResponse(player);
    }

    public List<PlayerResponse> findAll() {
        List<Player> players = playerRepository.findAll();
        return players.stream().map(playerMapper::toResponse).toList();
    }

    public void delete(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        Team team = player.getTeam();
        if (team != null && team.getCaptain() != null && Objects.equals(player.getId(), team.getCaptain().getId())) {
            team.setCaptain(null);
        }
        playerRepository.delete(player);
    }
}
