package com.hcLinear.backendTest.service;

import com.hcLinear.backendTest.dto.TeamResponse;
import com.hcLinear.backendTest.exceptionhandling.TeamNotFoundException;
import com.hcLinear.backendTest.mapper.TeamMapper;
import com.hcLinear.backendTest.model.Team;
import com.hcLinear.backendTest.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;


    public TeamResponse findById(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
        return teamMapper.toResponse(team);

    }

    public List<TeamResponse> findAll() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream().map(teamMapper::toResponse).toList();
    }

}
