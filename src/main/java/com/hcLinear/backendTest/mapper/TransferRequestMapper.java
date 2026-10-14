package com.hcLinear.backendTest.mapper;

import com.hcLinear.backendTest.dto.TransferRequestResponse;
import com.hcLinear.backendTest.model.Player;
import com.hcLinear.backendTest.model.TransferRequest;
import com.hcLinear.backendTest.model.TransferRequestStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface TransferRequestMapper {

    @Mapping(target = "playerId", source = "player.id")
    @Mapping(target = "playerFullName", source = "player", qualifiedByName = "fullName")
    @Mapping(target = "sourceTeamId", source = "sourceTeam.id")
    @Mapping(target = "sourceTeamName", source = "sourceTeam.name")
    @Mapping(target = "targetTeamId", source = "targetTeam.id")
    @Mapping(target = "targetTeamName", source = "targetTeam.name")
    @Mapping(target = "expired", source = "transferRequest", qualifiedByName = "isExpired")
    TransferRequestResponse toResponse(TransferRequest transferRequest);

    @Named("fullName")
    default String playerFullName(Player player) {
        return player == null ? null : player.getFirstName() + " " + player.getLastName();
    }

    @Named("isExpired")
    default boolean isExpired(TransferRequest transferRequest) {
        return transferRequest.getStatus() == TransferRequestStatus.PENDING
                && transferRequest.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
