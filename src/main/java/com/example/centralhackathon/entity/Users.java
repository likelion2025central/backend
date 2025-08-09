package com.example.centralhackathon.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name="uk_users_username", columnNames = "username")
})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role", length = 20) // 'STUDENT','COUNCIL','OWNER'
public abstract class Users extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;
}