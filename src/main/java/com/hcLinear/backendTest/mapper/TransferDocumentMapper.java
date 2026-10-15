package com.hcLinear.backendTest.mapper;


import com.hcLinear.backendTest.dto.TransferDocumentResponse;
import com.hcLinear.backendTest.model.Player;
import com.hcLinear.backendTest.model.TransferDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransferDocumentMapper {
    @Mapping(target = "transferRequestId", source = "transferRequest.id")
    @Mapping(target = "playerId", source = "player.id")
    @Mapping(target = "playerFullName", source = "player", qualifiedByName = "fullName")
    @Mapping(target = "sourceTeamId", source = "sourceTeam.id")
    @Mapping(target = "sourceTeamName", source = "sourceTeam.name")
    @Mapping(target = "targetTeamId", source = "targetTeam.id")
    @Mapping(target = "targetTeamName", source = "targetTeam.name")
    TransferDocumentResponse toResponse(TransferDocument transferDocument);

    @Named("fullName")
    default String playerFullName(Player player) {
        return player == null ? null : player.getFirstName() + " " + player.getLastName();
    }
}
