package com.example.vse_back.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "auth_codes")
public class AuthCodeEntity {
    @Id
    @Column(name = "user_id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuthCodeEntity that = (AuthCodeEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
