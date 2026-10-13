package com.hcLinear.backendTest.service;

import com.hcLinear.backendTest.dto.TeamCreateRequest;
import com.hcLinear.backendTest.dto.TeamResponse;
import com.hcLinear.backendTest.exceptionhandling.FieldValidationException;
import com.hcLinear.backendTest.exceptionhandling.TeamAlreadyExistsException;
import com.hcLinear.backendTest.exceptionhandling.TeamNotFoundException;
import com.hcLinear.backendTest.mapper.TeamMapper;
import com.hcLinear.backendTest.model.Team;
import com.hcLinear.backendTest.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private static final int MIN_YEAR = 1857;

    public TeamResponse findById(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
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
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
        team.getPlayers().forEach(player -> player.setTeam(null));
        teamRepository.delete(team);
    }


}
