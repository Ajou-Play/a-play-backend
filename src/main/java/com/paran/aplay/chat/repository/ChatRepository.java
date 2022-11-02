package com.paran.aplay.chat.repository;

import com.paran.aplay.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "select cm from ChatMessage cm join fetch cm.sender s where cm.channel.id = :channelId",
    countQuery = "select count(cm) from ChatMessage cm where cm.channel.id = :channelId")
    Page<ChatMessage> findChatMessageByChannelId(@Param("channelId") Long channelId, Pageable pageable);
}
