package com.dmu.stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String phone;

    private LocalDateTime createdAt;

    private String refreshToken;

    @OneToMany(mappedBy = "member")
    private List<UserStock> userStocks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

}
