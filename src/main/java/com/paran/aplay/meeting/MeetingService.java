package com.paran.aplay.meeting;

import com.paran.aplay.common.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final RedisService redisService;

    public void broadcastMessage(){

    }
}
