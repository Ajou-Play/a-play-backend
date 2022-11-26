package com.paran.aplay.common.listener;

import com.paran.aplay.jwt.JwtPrincipal;
import com.paran.aplay.meeting.WebRtcService;
import com.paran.aplay.user.domain.User;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@RequiredArgsConstructor
public final class StompEventListener {

    private final WebRtcService webRtcService;

    @EventListener
    public void unsubscribe(final SessionUnsubscribeEvent event) {
        final User user = this.getUser(event);

        if (Objects.nonNull(user)) {
            this.webRtcService.leave(user);
        }
    }

    @EventListener
    public void disconnect(final SessionDisconnectEvent event) {
        final User user = this.getUser(event);

        if (Objects.nonNull(user)) {
            this.webRtcService.leave(user);
        }
    }

    private User getUser(final AbstractSubProtocolEvent event) {
        if (Objects.nonNull(event.getUser())) {
            final JwtPrincipal principal = (JwtPrincipal) event.getUser();

            return principal.getUser();
        }

        return null;
    }

}
