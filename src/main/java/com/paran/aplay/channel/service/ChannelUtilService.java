package com.paran.aplay.channel.service;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.repository.ChannelRepository;
import com.paran.aplay.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.paran.aplay.common.ErrorCode.CHANNEL_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelUtilService {

    private final ChannelRepository channelRepository;

    @Transactional(readOnly = true)
    public Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException(
                CHANNEL_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public void validateChannelId(Long channelId) {
        channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));
    }

}
