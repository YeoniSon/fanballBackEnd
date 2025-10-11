package com.example.fanball.team.entity;

import com.example.common.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity(name = "stadium")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stadium{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;
}
