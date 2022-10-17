package com.paran.aplay.channel.repository;

import com.paran.aplay.channel.domain.Channel;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
  List<Channel> findByTeamId(@Param("teamId") Long teamId);
}
