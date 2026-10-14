package com.hcLinear.backendTest.service;

import com.hcLinear.backendTest.dto.TransferCreateRequest;
import com.hcLinear.backendTest.dto.TransferRequestResponse;
import com.hcLinear.backendTest.exceptionhandling.*;
import com.hcLinear.backendTest.mapper.TransferRequestMapper;
import com.hcLinear.backendTest.model.Player;
import com.hcLinear.backendTest.model.Team;
import com.hcLinear.backendTest.model.TransferRequest;
import com.hcLinear.backendTest.model.TransferRequestStatus;
import com.hcLinear.backendTest.repository.PlayerRepository;
import com.hcLinear.backendTest.repository.TransferRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferRequestService {

    private static final int DEFAULT_EXPIRATION_DAYS = 2;
    private final TransferRequestRepository transferRequestRepository;
    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private final TransferRequestMapper mapper;

    public TransferRequestResponse create(TransferCreateRequest request) {
        Player player = playerRepository.findById(request.getPlayerId()).orElseThrow(() -> new PlayerNotFoundException(request.getPlayerId()));
        Team targetTeam = teamService.findTeamWithIdIfExists(request.getTargetTeamId());
        Team sourceTeam = teamService.findTeamWithIdIfExists(request.getSourceTeamId());
        validateSourceTeamMatchesPlayerTeam(request.getSourceTeamId(), player);
        validateSourceAndTargetTeamDiffer(request.getSourceTeamId(), request.getTargetTeamId());
        validateIfPendingTransferRequestExists(request.getTargetTeamId(), player.getId());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = request.getExpiresAt() != null ? request.getExpiresAt() : now.plusDays(DEFAULT_EXPIRATION_DAYS);
        BigDecimal amount = player.getTeam() == null ? BigDecimal.ZERO : player.getMarketValue();
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setPlayer(player);
        transferRequest.setTargetTeam(targetTeam);
        transferRequest.setSourceTeam(sourceTeam);
        transferRequest.setCreatedAt(now);
        transferRequest.setExpiresAt(expires);
        transferRequest.setStatus(TransferRequestStatus.PENDING);
        transferRequest.setAmount(amount);
        transferRequestRepository.save(transferRequest);
        return mapper.toResponse(transferRequest);
    }

    private void validateIfPendingTransferRequestExists(Long targetTeamId, Long playerId) {
        if (transferRequestRepository.existsByPlayerIdAndTargetTeamIdAndStatus(playerId, targetTeamId, TransferRequestStatus.PENDING)) {
            throw new RequestPendingFromTargetTeamForThisPlayerException(targetTeamId, playerId);
        }
    }

    private void validateSourceAndTargetTeamDiffer(Long sourceTeamId, Long targetTeamId) {
        if (Objects.equals(sourceTeamId, targetTeamId)) {
            throw new SourceTeamAndTargetTeamMatchesException(sourceTeamId, targetTeamId);
        }
    }

    private void validateSourceTeamMatchesPlayerTeam(Long sourceTeamId, Player player) {
        Long playerTeamId = player.getTeam() == null ? null : player.getTeam().getId();
        if (!Objects.equals(playerTeamId, sourceTeamId)) {
            throw new SourceTeamDiffersFromPlayerTeamException(player.getId(), sourceTeamId);
        }
    }

}
