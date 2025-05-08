//package com.oxygensend.auth.infrastructure.jpa;
//
//import com.oxygensend.auth.domain.UserRole;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Transient;
//import java.time.LocalDateTime;
//import java.util.Set;
//import java.util.UUID;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.experimental.Accessors;
//
//@Builder
//@Entity(name = "user")
//@Getter
//@Setter
//@Accessors(fluent = true)
//@AllArgsConstructor
//@NoArgsConstructor
//class UserJpa {
//    @Id
//    private UUID id;
//    private String name;
//    private String surname;
//    private String email;
//    @Transient
//    private String username;
//    private String password;
//    private Boolean enabled;
//    private Boolean locked;
//    private Set<UserRole> roles;
//    private LocalDateTime verified;
//    private LocalDateTime createdAt;
//
//
//}
