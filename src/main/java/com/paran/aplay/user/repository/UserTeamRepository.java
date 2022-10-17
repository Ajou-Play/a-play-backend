package com.paran.aplay.user.repository;

import com.paran.aplay.user.domain.UserTeam;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
  boolean existsByUserIdAndTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);
}
