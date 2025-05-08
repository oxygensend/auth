package com.oxygensend.auth.port.adapter.in.rest.resources.user;

import com.oxygensend.auth.port.Ports;
import org.springframework.context.annotation.Profile;

@Profile(Ports.REST)
record TokenResponse(String token) {
}
