package com.hcLinear.backendTest.repository;

import com.hcLinear.backendTest.model.TransferRequest;
import com.hcLinear.backendTest.model.TransferRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {

    boolean existsByPlayerIdAndTargetTeamIdAndStatus(Long playerId, Long targetTeamId, TransferRequestStatus status);
}
