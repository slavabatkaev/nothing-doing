// src/main/java/com/example/nothingdoing/entity/User.java
package com.example.nothingdoing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private long totalMinutes = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}