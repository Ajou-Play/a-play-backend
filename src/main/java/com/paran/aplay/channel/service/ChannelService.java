package com.paran.aplay.channel.service;

import static com.paran.aplay.common.ErrorCode.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.dto.response.ChannelDetailResponse;
import com.paran.aplay.channel.repository.ChannelRepository;
import com.paran.aplay.common.error.exception.AlreadyExistsException;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.common.error.exception.PermissionDeniedException;
import com.paran.aplay.team.domain.Team;

import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.domain.UserChannel;
import com.paran.aplay.user.repository.UserChannelRepository;
import com.paran.aplay.user.service.UserUtilService;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {

  private final ChannelRepository channelRepository;

  private final UserChannelRepository userChannelRepository;

  private final UserUtilService userUtilService;

  private final ChannelUtilService channelUtilService;

  @Transactional(readOnly = true)
  public List<Channel> getAllChannelsByTeam(Team team) {
    return channelRepository.findByTeamId(team.getId());
  }

  @Transactional(readOnly = true)
  public List<Team> getAllTeamsByUser(User user) {
    return userUtilService.getUserTeamsByUser(user)
            .stream().map(userTeam -> userTeam.getTeam())
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public ChannelDetailResponse getChannelDetailById(Long channelId) {
    Channel channel = channelUtilService.getChannelById(channelId);
    List<User> members = userChannelRepository.findAllByChannelId(channelId).stream()
            .map(UserChannel::getUser).toList();
    return ChannelDetailResponse.from(channel, members);
  }


  @Transactional
  public Channel createChannel(String name, Team team) {
    Channel newChannel = new Channel(name, team);
    return channelRepository.save(newChannel);
  }
  @Transactional
  public void inviteUserToChannel(User user, Channel channel) {
    if (!userUtilService.checkUserExistsInTeam(user, channel.getTeam())) throw new PermissionDeniedException(USER_NOT_ALLOWED);
    if (userUtilService.checkUserExistsInChannel(user, channel)) throw new AlreadyExistsException(USER_ALREADY_IN_CHANNEL);
    UserChannel userChannel = new UserChannel(user, channel);
    userChannelRepository.save(userChannel);
  }
}
