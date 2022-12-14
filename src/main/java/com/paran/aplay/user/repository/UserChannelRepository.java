package com.paran.aplay.user.repository;

import com.paran.aplay.user.domain.UserChannel;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChannelRepository extends JpaRepository<UserChannel, Long> {
  boolean existsByUserIdAndChannelId(@Param("userId") Long userId, @Param("channelId") Long channelId);

  List<UserChannel> findAllByChannelId(@Param("channelId") Long channelId);
}
