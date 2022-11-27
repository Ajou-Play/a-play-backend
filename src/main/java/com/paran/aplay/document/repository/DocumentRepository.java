package com.paran.aplay.document.repository;

import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "select d from Document d join fetch d.channel c where c.id = :channelId",
            countQuery = "select count(d) from Document d where d.channel.id = :channelId")
    Page<ChatMessage> findDoucmentsByChannelId(@Param("channelId") Long channelId, Pageable pageable);
}
