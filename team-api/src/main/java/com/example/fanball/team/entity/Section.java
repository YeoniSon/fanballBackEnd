package com.example.fanball.team.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "section")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String name;
    private String category; // weekday, weekend, g1, g2 등

    private Integer price;
}
