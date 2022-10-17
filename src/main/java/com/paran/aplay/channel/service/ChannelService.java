package com.paran.aplay.channel.service;

import static com.paran.aplay.common.ErrorCode.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.repository.ChannelRepository;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.team.domain.Team;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {

  private final ChannelRepository channelRepository;

  public Channel getChannelById(Long channelId) {
    return channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException(
        CHANNEL_NOT_FOUND));
  }

  public List<Channel> getAllChannelsByTeam(Team team) {
    return channelRepository.findByTeamId(team.getId());
  }

  public Channel createChannel(String name, Team team) {
    Channel newChannel = new Channel(name, team);
    return channelRepository.save(newChannel);
  }
}
