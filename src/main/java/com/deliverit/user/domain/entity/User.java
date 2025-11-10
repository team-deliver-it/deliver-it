package com.deliverit.user.domain.entity;

import com.deliverit.global.entity.BaseEntity;
import com.deliverit.user.presentation.dto.UserEditRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(String username, String password, String name, String phone, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    public void updateProfile(UserEditRequestDto dto) {
        if (dto.getName() != null && !dto.getName().isBlank() && !dto.getName().equals(this.name)) {
            this.name = dto.getName();
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank() && !dto.getPhone().equals(this.phone)) {
            this.phone = dto.getPhone();
        }
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
