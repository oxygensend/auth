package com.oxygensend.auth.context.user;


import com.oxygensend.auth.infrastructure.security.Admin;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;


    @Admin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @Admin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/block")
    void block(@PathVariable UUID id) {
        service.block(id);
    }


    @Admin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/unblock")
    void unblock(@PathVariable UUID id) {
        service.unblock(id);
    }

}
