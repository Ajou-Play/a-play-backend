package com.paran.aplay.chat.controller;

import com.paran.aplay.chat.service.ChatService;
import com.paran.aplay.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

  private final ChatService chatService;
}
