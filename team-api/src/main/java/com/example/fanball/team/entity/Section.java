package com.example.fanball.team.entity;

import com.example.fanball.team.domain.TeamForm;
import lombok.*;

import javax.persistence.*;

@Entity(name = "SECTION")
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

    private String name;

    private Integer price;

    public static Section from(TeamForm form) {
        return Section.builder()
                .name(form.getSectionName())
                .price(form.getPrice())
                .build();
    }
}
