package com.oxygensend.auth.port.adapter.in.rest.resources.auth.request;

import com.oxygensend.auth.port.Ports;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;

import java.util.UUID;


@Profile(Ports.REST)
public record UserIdRequest(@NotNull UUID id) {
}
