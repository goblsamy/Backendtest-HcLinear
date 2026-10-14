package com.hcLinear.backendTest.service;

import com.hcLinear.backendTest.dto.TeamCreateRequest;
import com.hcLinear.backendTest.dto.TeamResponse;
import com.hcLinear.backendTest.exceptionhandling.*;
import com.hcLinear.backendTest.mapper.TeamMapper;
import com.hcLinear.backendTest.model.Player;
import com.hcLinear.backendTest.model.Team;
import com.hcLinear.backendTest.repository.PlayerRepository;
import com.hcLinear.backendTest.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private static final int MIN_YEAR = 1857;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final PlayerRepository playerRepository;

    public TeamResponse findById(Long teamId) {
        Team team = findTeamWithIdIfExists(teamId);
        return teamMapper.toResponse(team);

    }

    public List<TeamResponse> findAll() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream().map(teamMapper::toResponse).toList();
    }

    public TeamResponse create(TeamCreateRequest request) {
        String requestNameTrimmed = request.getName().trim();
        String requestCityTrimmed = request.getCity().trim();
        validateTeamNameIsUnique(requestNameTrimmed);
        validateFoundedYear(request.getFoundedYear());
        Team team = teamMapper.toEntity(request);
        team.setName(requestNameTrimmed);
        team.setCity(requestCityTrimmed);
        Team savedTeam = teamRepository.save(team);
        return teamMapper.toResponse(savedTeam);
    }

    private void validateFoundedYear(Integer foundedYear) {
        if (foundedYear > Year.now().getValue() || foundedYear < MIN_YEAR) {
            throw new FieldValidationException("foundedYear", "Founded year must be between " + MIN_YEAR + " and the current year.");
        }
    }

    private void validateTeamNameIsUnique(String name) {
        if (teamRepository.existsByName(name)) {
            throw new TeamAlreadyExistsException(name);
        }
    }

    public void delete(Long teamId) {
        Team team = findTeamWithIdIfExists(teamId);
        team.getPlayers().forEach(player -> player.setTeam(null));
        teamRepository.delete(team);
    }

    public TeamResponse appointCaptain(Long teamId, Long playerId) {
        Team team = findTeamWithIdIfExists(teamId);
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        if (player.getTeam() == null || !Objects.equals(teamId, player.getTeam().getId())) {
            throw new PlayerNotTeamMemberException(playerId, teamId);
        }
        team.setCaptain(player);
        return teamMapper.toResponse(team);
    }

    public Team findTeamWithIdIfExists(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
    }

}
