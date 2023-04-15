package com.example.vse_back.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @Column(unique = true, name = "id", nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    // I'd need a Set of them later
    @OneToOne
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    @Column(name = "name", nullable = false)
    private String name;

    @Min(1)
    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "description", length = 1023)
    private String description;

    @Min(1)
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductEntity that = (ProductEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
