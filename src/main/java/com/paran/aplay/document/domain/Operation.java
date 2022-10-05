package com.paran.aplay.document.domain;

import com.paran.aplay.common.entity.BaseEntity;
import com.paran.aplay.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Operation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "operation_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "document_id")
  private Document document;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private EventType type;

  @Column(length = 2)
  private String modifiedString;

  @Builder
  public Operation(User user, Document document, EventType type, String modifiedString) {
    this.user = user;
    this.document = document;
    this.type = type;
    this.modifiedString = modifiedString;
  }
}
