package com.example.vse_back.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "events")
public class EventEntity {
    @ManyToMany
    @JoinTable(
            name = "users_events",
            joinColumns = {@JoinColumn(name = "event_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    List<UserEntity> participants = new ArrayList<>();

    @Id
    @Column(unique = true, name = "id", nullable = false)
    @UuidGenerator
    private UUID id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "type", nullable = false)
    private String type;

    @Min(3)
    @Column(name = "participants_number", nullable = false)
    private Integer participantsNumber;

    @Min(1)
    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "approving_date")
    private LocalDateTime approvingDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, length = 1023)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @OneToOne
    @JoinColumn(name = "image_id")
    private ImageEntity image;

    @ManyToOne
    @JoinColumn(name = "organizer_user_id", nullable = false)
    private UserEntity organizer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventEntity that = (EventEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
