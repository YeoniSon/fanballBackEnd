package com.example.fanball.team.entity;

import com.example.common.base.BaseEntity;
import com.example.fanball.team.domain.TeamForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity(name = "TEAM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Team extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    private Integer leagueLevel;

    public static Team from(TeamForm form) {
        return Team.builder()
                .name(form.getName())
                .leagueLevel(form.getLeagueLevel())
                .build();
    }
}
