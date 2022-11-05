package com.paran.aplay.socket;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.chat.domain.MessageType;
import com.paran.aplay.chat.dto.ChatRequest;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.chat.service.ChatService;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.service.TeamService;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.request.UserSignUpRequest;
import com.paran.aplay.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class StompSupportTest {
  protected StompSession stompSession;

  @LocalServerPort
  private int port;

  private final String url;

  private final ChannelService channelService;

  private final TeamService teamService;

  private final WebSocketStompClient websocketClient;

  private final UserService userService;

  private final ChatService chatService;
  private User testUser;

  private Team testTeam;

  private Channel testChannel;

  @Autowired
  public StompSupportTest(ChannelService channelService, TeamService teamService,
      UserService userService, ChatService chatService) {
    this.channelService = channelService;
    this.teamService = teamService;
    this.userService = userService;
    this.chatService = chatService;
    this.websocketClient = new WebSocketStompClient(new SockJsClient(createTransport()));
    this.websocketClient.setMessageConverter(new MappingJackson2MessageConverter());
    this.url = "ws://localhost:";
  }

  @BeforeAll
  public void initializeObjects() {
    testUser = userService.signUp(UserSignUpRequest.builder()
        .email("test@gmail.com")
        .name("김승은")
        .password("test1234!")
        .build());
    testTeam = teamService.createTeam("A-Play");
    teamService.inviteUserToTeam(testUser, testTeam);
    testChannel = channelService.createChannel("general", testTeam);
    channelService.inviteUserToChannel(testUser, testChannel);
  }

  @BeforeEach
  public void connect() throws ExecutionException, InterruptedException, TimeoutException {
    this.stompSession = this.websocketClient
        .connect(url + port + "/socket", new StompSessionHandlerAdapter() {})
        .get(3, TimeUnit.SECONDS);
  }

  @AfterEach
  public void disconnect() {
    if (this.stompSession.isConnected()) {
      this.stompSession.disconnect();
    }
  }

  private List<Transport> createTransport() {
    List<Transport> transports = new ArrayList<>(1);
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    return transports;
  }

  @Test
  @DisplayName("소켓 연결 후 stompCli publish, subscribe 통신 테스트")
  public void testChat() throws ExecutionException, InterruptedException, TimeoutException {
    /* GIVEN */
    MessageFrameHandler<ChatResponse> handler = new MessageFrameHandler<>(ChatResponse.class);
    String content = "Hello motherfuckers!";
    String destination = "/sub/chat/message/channel/"+testChannel.getId();
    this.stompSession.subscribe(destination, handler);
    ChatRequest request = ChatRequest.builder()
        .channelId(testChannel.getId())
        .senderId(testUser.getId())
        .type(MessageType.TALK)
        .content(content)
        .build();
    /* WHEN */
    this.stompSession.send("/pub/chat/message", request);

    /* THEN */
    ChatResponse response = handler.getCompletableFuture().get(10, TimeUnit.SECONDS);

    assertThat(response).isNotNull();
    assertThat(response.getContent()).isEqualTo(content);
    assertThat(response.getSender().getUserId()).isEqualTo(testUser.getId());
    assertThat(response.getSender().getName()).isEqualTo(testUser.getName());
  }
  @Test
  @DisplayName("채팅 송신 후 저장 확인 테스트")
  public void testChatSaved() throws ExecutionException, InterruptedException, TimeoutException {
    /* GIVEN */
    MessageFrameHandler<ChatResponse> handler = new MessageFrameHandler<>(ChatResponse.class);
    String content = "Hello motherfuckers!";
    String destination = "/sub/chat/message/channel/"+testChannel.getId();
    this.stompSession.subscribe(destination, handler);
    ChatRequest request = ChatRequest.builder()
        .channelId(testChannel.getId())
        .senderId(testUser.getId())
        .type(MessageType.TALK)
        .content(content)
        .build();
    /* WHEN */
    this.stompSession.send("/pub/chat/message", request);

    handler.getCompletableFuture().get(10, TimeUnit.SECONDS);
    /* THEN */
    List<ChatMessage> messages = chatService.getAllChatMessages();
    assertThat(messages.size()).isEqualTo(1);
    assertThat(messages.get(0).getContent()).isEqualTo(content);
  }

}
