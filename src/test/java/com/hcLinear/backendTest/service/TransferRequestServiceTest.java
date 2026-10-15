package com.hcLinear.backendTest.service;

import com.hcLinear.backendTest.exceptionhandling.InsufficientFundsException;
import com.hcLinear.backendTest.exceptionhandling.SourceTeamDiffersFromPlayerTeamException;
import com.hcLinear.backendTest.exceptionhandling.TargetTeamFullException;
import com.hcLinear.backendTest.exceptionhandling.TransferRequestExpiredException;
import com.hcLinear.backendTest.exceptionhandling.TransferRequestNotFoundException;
import com.hcLinear.backendTest.exceptionhandling.TransferRequestNotPendingException;
import com.hcLinear.backendTest.mapper.TransferRequestMapper;
import com.hcLinear.backendTest.model.Player;
import com.hcLinear.backendTest.model.Team;
import com.hcLinear.backendTest.model.TransferRequest;
import com.hcLinear.backendTest.model.TransferRequestStatus;
import com.hcLinear.backendTest.repository.PlayerRepository;
import com.hcLinear.backendTest.repository.TransferRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferRequestServiceTest {

    private static final Long REQUEST_ID = 1L;
    private static final Long OTHER_REQUEST_ID = 2L;
    private static final Long SOURCE_TEAM_ID = 10L;
    private static final Long TARGET_TEAM_ID = 20L;
    private static final Long PLAYER_ID = 100L;

    @Mock
    private TransferRequestRepository transferRequestRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private TransferRequestMapper mapper;

    @Mock
    private TransferDocumentService transferDocumentService;

    @InjectMocks
    private TransferRequestService transferRequestService;

    private Team sourceTeam;
    private Team targetTeam;
    private Player player;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        sourceTeam = createTeam(SOURCE_TEAM_ID, "Ferencváros", "50000000");
        targetTeam = createTeam(TARGET_TEAM_ID, "Újpest", "30000000");

        player = new Player();
        player.setId(PLAYER_ID);
        player.setFirstName("Dávid");
        player.setLastName("Gera");
        player.setShirtNumber(10);
        player.setMarketValue(new BigDecimal("10000000"));
        player.setTeam(sourceTeam);

        transferRequest = createPendingRequest(REQUEST_ID);
    }

    @Test
    void accept_whenRequestDoesNotExist_throwsNotFound() {
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferRequestService.accept(REQUEST_ID))
                .isInstanceOf(TransferRequestNotFoundException.class);
    }

    @Test
    void accept_whenRequestIsNotPending_throwsNotPending() {
        transferRequest.setStatus(TransferRequestStatus.REJECTED);
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));

        assertThatThrownBy(() -> transferRequestService.accept(REQUEST_ID))
                .isInstanceOf(TransferRequestNotPendingException.class);
        verify(transferDocumentService, never()).createForAcceptedRequest(any(), any());
    }

    @Test
    void accept_whenRequestIsExpired_throwsExpired() {
        transferRequest.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));

        assertThatThrownBy(() -> transferRequestService.accept(REQUEST_ID))
                .isInstanceOf(TransferRequestExpiredException.class);
    }

    @Test
    void accept_whenPlayerIsNoLongerInSourceTeam_throwsSourceTeamMismatch() {
        player.setTeam(createTeam(30L, "Debrecen", "1000000"));
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));

        assertThatThrownBy(() -> transferRequestService.accept(REQUEST_ID))
                .isInstanceOf(SourceTeamDiffersFromPlayerTeamException.class);
    }

    @Test
    void accept_whenTargetTeamIsFull_throwsTargetTeamFull() {
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));
        when(playerRepository.countByTeamId(TARGET_TEAM_ID)).thenReturn(25L);

        assertThatThrownBy(() -> transferRequestService.accept(REQUEST_ID))
                .isInstanceOf(TargetTeamFullException.class);
    }

    @Test
    void accept_whenTargetTeamHasInsufficientFunds_throwsAndChangesNothing() {
        targetTeam.setBudget(new BigDecimal("5000000"));
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));
        when(playerRepository.countByTeamId(TARGET_TEAM_ID)).thenReturn(10L);

        assertThatThrownBy(() -> transferRequestService.accept(REQUEST_ID))
                .isInstanceOf(InsufficientFundsException.class);
        assertThat(targetTeam.getBudget()).isEqualByComparingTo("5000000");
        assertThat(sourceTeam.getBudget()).isEqualByComparingTo("50000000");
        assertThat(player.getTeam()).isSameAs(sourceTeam);
    }

    @Test
    void accept_whenValid_executesTransfer() {
        sourceTeam.setCaptain(player);
        TransferRequest otherRequest = createPendingRequest(OTHER_REQUEST_ID);
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));
        when(playerRepository.countByTeamId(TARGET_TEAM_ID)).thenReturn(10L);
        when(transferRequestRepository.findByPlayerIdAndStatus(PLAYER_ID, TransferRequestStatus.PENDING))
                .thenReturn(List.of(transferRequest, otherRequest));

        transferRequestService.accept(REQUEST_ID);

        assertThat(targetTeam.getBudget()).isEqualByComparingTo("20000000");
        assertThat(sourceTeam.getBudget()).isEqualByComparingTo("60000000");
        assertThat(player.getTeam()).isSameAs(targetTeam);
        assertThat(player.getShirtNumber()).isNull();
        assertThat(sourceTeam.getCaptain()).isNull();
        assertThat(transferRequest.getStatus()).isEqualTo(TransferRequestStatus.ACCEPTED);
        assertThat(transferRequest.getDecidedAt()).isNotNull();
        assertThat(otherRequest.getStatus()).isEqualTo(TransferRequestStatus.CLOSED);
        assertThat(otherRequest.getDecidedAt()).isEqualTo(transferRequest.getDecidedAt());
        verify(transferDocumentService).createForAcceptedRequest(eq(transferRequest), any(LocalDateTime.class));
    }

    @Test
    void accept_whenPlayerHasNoTeam_movesPlayerWithoutTouchingBudgets() {
        player.setTeam(null);
        transferRequest.setSourceTeam(null);
        transferRequest.setAmount(BigDecimal.ZERO);
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));
        when(playerRepository.countByTeamId(TARGET_TEAM_ID)).thenReturn(10L);
        when(transferRequestRepository.findByPlayerIdAndStatus(PLAYER_ID, TransferRequestStatus.PENDING))
                .thenReturn(List.of(transferRequest));

        transferRequestService.accept(REQUEST_ID);

        assertThat(targetTeam.getBudget()).isEqualByComparingTo("30000000");
        assertThat(player.getTeam()).isSameAs(targetTeam);
        assertThat(transferRequest.getStatus()).isEqualTo(TransferRequestStatus.ACCEPTED);
    }

    @Test
    void reject_whenPending_setsStatusToRejected() {
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));

        transferRequestService.reject(REQUEST_ID);

        assertThat(transferRequest.getStatus()).isEqualTo(TransferRequestStatus.REJECTED);
        assertThat(transferRequest.getDecidedAt()).isNotNull();
        assertThat(player.getTeam()).isSameAs(sourceTeam);
        verify(transferDocumentService, never()).createForAcceptedRequest(any(), any());
    }

    @Test
    void reject_whenAlreadyAccepted_throwsNotPending() {
        transferRequest.setStatus(TransferRequestStatus.ACCEPTED);
        when(transferRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(transferRequest));

        assertThatThrownBy(() -> transferRequestService.reject(REQUEST_ID))
                .isInstanceOf(TransferRequestNotPendingException.class);
    }

    private TransferRequest createPendingRequest(Long id) {
        TransferRequest request = new TransferRequest();
        request.setId(id);
        request.setPlayer(player);
        request.setSourceTeam(sourceTeam);
        request.setTargetTeam(targetTeam);
        request.setAmount(new BigDecimal("10000000"));
        request.setStatus(TransferRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now().minusHours(1));
        request.setExpiresAt(LocalDateTime.now().plusDays(1));
        return request;
    }

    private Team createTeam(Long id, String name, String budget) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        team.setCity("Budapest");
        team.setBudget(new BigDecimal(budget));
        return team;
    }
}