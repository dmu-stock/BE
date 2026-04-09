package com.dmu.stock.entiry;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    private String userId;

    private String userName;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<UserStock> userStocks = new ArrayList<>();

}
