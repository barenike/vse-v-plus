package com.example.vse_back.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "balance_change_records")
public class BalanceChangeRecordEntity {
    @Id
    @Column(unique = true, name = "id", nullable = false)
    @UuidGenerator
    private UUID id;

    @Column(name = "change_amount", nullable = false)
    private Integer changeAmount;

    @Column(name = "cause", nullable = false)
    private String cause;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "object_user_id", nullable = false)
    private UserEntity objectUser;

    @ManyToOne
    @JoinColumn(name = "subject_user_id", nullable = false)
    private UserEntity subjectUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BalanceChangeRecordEntity that = (BalanceChangeRecordEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
