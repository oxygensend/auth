package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ExcludeFromJacocoGeneratedReport
@Entity(name = "users")
public class UserJpa {
    @Id
    public UUID id;

    @Column(nullable = true)
    public String businessId;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    public String username;

    @Convert(converter = StringSetConverter.class)
    @Column(nullable = false)
    public Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public boolean locked;

    @Column(nullable = false)
    public boolean verified;

    @Column(nullable = false)
    public AccountActivationType accountActivationType;


}
