package com.hcLinear.backendTest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_documents")
@Getter
@Setter
public class TransferDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_request_id")
    private TransferRequest transferRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_team_id")
    private Team sourceTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_team_id")
    private Team targetTeam;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transferred_at", nullable = false)
    private LocalDateTime transferredAt;
}
