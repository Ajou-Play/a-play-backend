package com.paran.aplay.channel.service;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.repository.ChannelRepository;
import com.paran.aplay.common.ErrorCode;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.service.TeamService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {

  private final ChannelRepository channelRepository;

  private final TeamService teamService;

  public Channel getChannelById(Long channelId) {
    return channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException(
        ErrorCode.CHANNEL_NOT_FOUND));
  }

  public List<Channel> getAllChannel() {
    return channelRepository.findAll();
  }

  public Channel createChannel(String name, Team team) {
    Channel newChannel = new Channel(name, team);
    return channelRepository.save(newChannel);
  }
}
