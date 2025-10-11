package com.example.fanball.team.repository;

import com.example.fanball.team.entity.Section;
import com.example.fanball.team.entity.Stadium;
import com.example.fanball.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByStadiumAndTeamAndNameAndCategory(
            Stadium stadium, Team team, String sectionName, String category
    );
}
