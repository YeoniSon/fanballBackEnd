package com.example.fanball.team.entity;

import com.example.common.base.BaseEntity;
import com.example.fanball.team.domain.TeamForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity(name = "STADIUM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Stadium extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    public static Stadium from(TeamForm form) {
        return Stadium.builder()
                .name(form.getStadium())
                .location(form.getLocation())
                .build();
    }
}
