package com.paran.aplay.user.repository;

import com.paran.aplay.user.domain.UserTeam;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
  boolean existsByUserIdAndTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);

  List<UserTeam> findAllByTeamId(@Param("teamId") Long longs);

  List<UserTeam> findAllByUserId(@Param("userId") Long longs);

  UserTeam findByUserIdAndTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);

  void deleteAllByTeamId(@Param("teamId") Long teamId);
}
