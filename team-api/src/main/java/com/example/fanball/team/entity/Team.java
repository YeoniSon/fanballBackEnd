package com.example.fanball.team.entity;

import com.example.common.base.BaseEntity;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity(name = "team")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortName;

    private String logo;

    @ManyToOne
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    private Integer leagueLevel;
    private String league; //2군만
}
