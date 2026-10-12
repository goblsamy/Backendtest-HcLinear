package com.hcLinear.backendTest.mapper;

import com.hcLinear.backendTest.dto.TeamCreateRequest;
import com.hcLinear.backendTest.dto.TeamResponse;
import com.hcLinear.backendTest.model.Player;
import com.hcLinear.backendTest.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "captain", ignore = true)
    @Mapping(target = "players", ignore = true)
    Team toEntity(TeamCreateRequest request);

    @Mapping(target = "captainFullName", source = "captain", qualifiedByName = "captainFullName")
    @Mapping(target = "numberOfPlayers", source = "players", qualifiedByName = "numberOfPlayers")
    TeamResponse toResponse(Team team);

    @Named("captainFullName")
    default String captainFullName(Player player) {
        return player == null ? null : player.getFirstName() + " " + player.getLastName();
    }

    @Named("numberOfPlayers")
    default int numberOfPlayers(List<Player> players) {
        return players == null ? 0 : players.size();
    }
}
