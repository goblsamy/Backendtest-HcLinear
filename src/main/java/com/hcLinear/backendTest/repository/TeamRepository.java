package com.hcLinear.backendTest.repository;

import com.hcLinear.backendTest.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {


    boolean existsByName(String name);
}
