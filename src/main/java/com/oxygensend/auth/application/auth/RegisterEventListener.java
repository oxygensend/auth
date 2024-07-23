package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.domain.event.RegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
final class RegisterEventListener {

    @EventListener
    public void listen(RegisterEvent event) {
        log.info("Ignoring register event: {}", event);
    }

}
