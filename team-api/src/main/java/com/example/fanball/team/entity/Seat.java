package com.example.fanball.team.entity;

import com.example.fanball.team.domain.TeamForm;
import lombok.*;

import javax.persistence.*;

@Entity(name = "SEAT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "row_id")
    private Row row;

    private String seat;

    public static Seat from(TeamForm form) {
        return Seat.builder()
                .seat(form.getSeatNumber())
                .build();
    }
}
