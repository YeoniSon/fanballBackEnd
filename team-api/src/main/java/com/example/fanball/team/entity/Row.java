package com.example.fanball.team.entity;

import com.example.fanball.team.domain.TeamForm;
import lombok.*;

import javax.persistence.*;

@Entity(name = "ROW")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Row {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    private String row;

    public static Row from(TeamForm form) {
        return Row.builder()
                .row(form.getRow())
                .build();
    }
}
