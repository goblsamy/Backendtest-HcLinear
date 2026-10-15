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
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferRequestService {

    private static final int TEAM_MAX_SIZE = 25;
    private static final int DEFAULT_EXPIRATION_DAYS = 2;
    private final TransferRequestRepository transferRequestRepository;
    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private final TransferRequestMapper mapper;
    private final TransferDocumentService transferDocumentService;

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

    public TransferRequestResponse reject(Long transferRequestId) {
        TransferRequest transferRequest = findById(transferRequestId);
        validateIfRequestIsPending(transferRequest);
        TransferRequestStatus status = TransferRequestStatus.REJECTED;
        LocalDateTime rejectTime = LocalDateTime.now();
        transferRequest.setStatus(status);
        transferRequest.setDecidedAt(rejectTime);
        return mapper.toResponse(transferRequest);
    }

    public TransferRequestResponse accept(Long transferRequestId) {
        TransferRequest transferRequest = findById(transferRequestId);
        Long sourceTeamId = transferRequest.getSourceTeam() == null ? null : transferRequest.getSourceTeam().getId();
        LocalDateTime decidedAt = LocalDateTime.now();
        validateIfRequestIsPending(transferRequest);
        validateRequestNotExpired(transferRequest);
        validateSourceTeamMatchesPlayerTeam(sourceTeamId, transferRequest.getPlayer());
        validateTargetTeamHasFreePlaces(transferRequest);
        validateBudget(transferRequest);
        executeTransfer(transferRequest);
        transferRequest.setDecidedAt(decidedAt);
        transferRequest.setStatus(TransferRequestStatus.ACCEPTED);
        transferDocumentService.createForAcceptedRequest(transferRequest, decidedAt);
        closeOtherPendingRequests(transferRequest, decidedAt);
        return mapper.toResponse(transferRequest);
    }

    private void executeTransfer(TransferRequest transferRequest) {
        Team targetTeam = transferRequest.getTargetTeam();
        Team sourceTeam = transferRequest.getSourceTeam();
        Player player = transferRequest.getPlayer();
        BigDecimal newTargetTeamBudget = targetTeam.getBudget().subtract(transferRequest.getAmount());
        targetTeam.setBudget(newTargetTeamBudget);
        if (sourceTeam != null) {
            BigDecimal newSourceTeamBudget = sourceTeam.getBudget().add(transferRequest.getAmount());
            sourceTeam.setBudget(newSourceTeamBudget);
            if (sourceTeam.getCaptain() != null && Objects.equals(player.getId(), sourceTeam.getCaptain().getId())) {
                sourceTeam.setCaptain(null);
            }
        }
        player.setShirtNumber(null);
        player.setTeam(targetTeam);
    }

    private void closeOtherPendingRequests(TransferRequest transferRequest, LocalDateTime decidedAt) {
        transferRequestRepository.findByPlayerIdAndStatus(transferRequest.getPlayer().getId(), TransferRequestStatus.PENDING)
                .stream()
                .filter(otherRequest -> !Objects.equals(otherRequest.getId(), transferRequest.getId()))
                .forEach(otherRequest -> {
                    otherRequest.setStatus(TransferRequestStatus.CLOSED);
                    otherRequest.setDecidedAt(decidedAt);
                });
    }

    private void validateBudget(TransferRequest transferRequest) {
        if (transferRequest.getTargetTeam().getBudget().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientFundsException(transferRequest.getTargetTeam().getId());
        }
    }

    private void validateTargetTeamHasFreePlaces(TransferRequest transferRequest) {
        if (playerRepository.countByTeamId(transferRequest.getTargetTeam().getId()) >= TEAM_MAX_SIZE) {
            throw new TargetTeamFullException(transferRequest.getTargetTeam().getId());
        }
    }

    private void validateRequestNotExpired(TransferRequest transferRequest) {
        if (transferRequest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TransferRequestExpiredException(transferRequest.getId());
        }
    }

    private void validateIfRequestIsPending(TransferRequest request) {
        TransferRequest transferRequest = findById(request.getId());
        if (transferRequest.getStatus() != TransferRequestStatus.PENDING) {
            throw new TransferRequestNotPendingException(transferRequest.getId());
        }
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

    private TransferRequest findById(Long transferRequestId) {
        return transferRequestRepository.findById(transferRequestId).orElseThrow(() -> new TransferRequestNotFoundException(transferRequestId));
    }

    public List<TransferRequestResponse> listRequestsByPlayer(Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException(playerId);
        }
        List<TransferRequest> transferRequests = transferRequestRepository.findByPlayerId(playerId);
        return transferRequests.stream().map(mapper::toResponse).toList();
    }

    public List<TransferRequestResponse> listRequestsBySourceTeam(Long sourceTeamId) {
        teamService.findTeamWithIdIfExists(sourceTeamId);
        return transferRequestRepository.findBySourceTeamId(sourceTeamId)
                .stream()
                .map(mapper::toResponse).toList();
    }

    public List<TransferRequestResponse> listRequestsByTargetTeam(Long targetTeamId) {
        teamService.findTeamWithIdIfExists(targetTeamId);
        return transferRequestRepository.findByTargetTeamId(targetTeamId)
                .stream()
                .map(mapper::toResponse).toList();
    }

}

