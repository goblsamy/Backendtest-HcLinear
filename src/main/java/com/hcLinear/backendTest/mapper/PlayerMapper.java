package com.hcLinear.backendTest.mapper;


import com.hcLinear.backendTest.dto.PlayerCreateRequest;
import com.hcLinear.backendTest.dto.PlayerResponse;
import com.hcLinear.backendTest.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true)
    Player toEntity(PlayerCreateRequest request);


    @Mapping(target = "teamName", source = "team.name")
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "fullName", source = "player", qualifiedByName = "fullName")
    PlayerResponse toResponse(Player player);

    @Named("fullName")
    default String playerFullName(Player player) {
        return player == null ? null : player.getFirstName() + " " + player.getLastName();
    }
}

