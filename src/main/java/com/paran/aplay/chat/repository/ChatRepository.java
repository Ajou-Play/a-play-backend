package com.paran.aplay.chat.repository;

import com.paran.aplay.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

}
