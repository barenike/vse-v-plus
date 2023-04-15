package com.example.vse_back.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(unique = true, name = "id", nullable = false)
    @GeneratedValue(generator = "uuid")
    // Well, how can I change that?
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @OneToOne
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<OrderEntity> orderSet = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<BalanceChangeRecordsEntity> balanceChangeRecordSet = new HashSet<>();

    @Column(name = "email", nullable = false)
    private String email;

    @Min(0)
    @Column(name = "user_balance", nullable = false)
    private Integer userBalance;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "info_about", length = 1023)
    private String infoAbout;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserEntity that = (UserEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
