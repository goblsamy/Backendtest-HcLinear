package com.hcLinear.backendTest.repository;

import com.hcLinear.backendTest.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    boolean existsByShirtNumberAndTeamId(Integer shirtNumber, Long teamId);
}
