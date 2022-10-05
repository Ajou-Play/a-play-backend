package com.paran.aplay.document.domain;

import static com.paran.aplay.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.common.ErrorCode;
import com.paran.aplay.common.entity.BaseEntity;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Document extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "document_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @Column(nullable = false, length = 100)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private DocumentType type;

  @Lob
  private String content;

  @Builder
  public Document(Channel channel, String title, DocumentType type, String content) {
    if (!hasText(content) || !hasText(title)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }

    this.channel = channel;
    this.title = title;
    this.type = type;
    this.content = content;
  }
}
