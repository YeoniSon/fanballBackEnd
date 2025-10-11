package com.example.fanball.team.service;

import com.example.fanball.team.entity.Section;
import com.example.fanball.team.entity.Stadium;
import com.example.fanball.team.entity.Team;
import com.example.fanball.team.repository.SectionRepository;
import com.example.fanball.team.repository.StadiumRepository;
import com.example.fanball.team.repository.TeamRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TeamDataService {
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final SectionRepository sectionRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void importFromJson(String classpathPath) throws Exception {
        try (InputStream is = new ClassPathResource(classpathPath).getInputStream()) {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = objectMapper.readTree(is);
            JsonNode teams = root.get("teams");
            if (teams == null || !teams.isArray()) return;

            for (JsonNode teamNode : teams) {
                // Stadium upsert (name 기준)
                JsonNode stadiumNode = teamNode.path("stadium");
                if (stadiumNode.isMissingNode()) continue;
                String stadiumName = text(stadiumNode, "name");
                String stadiumLocation = text(stadiumNode, "location");

                Stadium stadium = findOrCreateStadium(stadiumName, stadiumLocation);

                // Team upsert (team_name 기준) + 추가 필드(short_name, logo, league)
                String teamName = text(teamNode, "team_name");
                Integer leagueLevel = intOrNull(teamNode.get("level"));
                String shortName = text(teamNode, "short_name");
                String logo = text(teamNode, "logo");
                String league = text(teamNode, "league");
                Team team = findOrCreateTeam(teamName, leagueLevel, stadium, shortName, logo, league);

                // Sections → category별로 저장 (stadium + team + name + category 기준)
                JsonNode sections = stadiumNode.path("sections");
                if (sections.isArray()) {
                    for (JsonNode secNode : sections) {
                        String sectionName = text(secNode, "section_name");
                        JsonNode priceNode = secNode.get("price");
                        if (priceNode == null || priceNode.isNull()) continue;

                        if (priceNode.isNumber()) {
                            // 단일가 → flat 카테고리
                            saveOrUpdateSection(stadium, team, sectionName, "flat", priceNode.intValue());
                        } else if (priceNode.isObject()) {
                            // weekday/weekend 혹은 g1..special
                            Iterator<Map.Entry<String, JsonNode>> fields = priceNode.fields();
                            while (fields.hasNext()) {
                                Map.Entry<String, JsonNode> f = fields.next();
                                String category = f.getKey();
                                JsonNode val = f.getValue();
                                if (!val.isNumber()) continue;
                                saveOrUpdateSection(stadium, team, sectionName, category, val.intValue());
                            }
                        }
                    }
                }
            }
        }
    }

    private Stadium findOrCreateStadium(String name, String location) {
        if (name == null) throw new IllegalArgumentException("stadium name is required");
        Optional<Stadium> existing = stadiumRepository.findByName(name);
        if (existing.isPresent()) return existing.get();
        return stadiumRepository.save(Stadium.builder().name(name).location(location).build());
    }

    private Team findOrCreateTeam(String name, Integer leagueLevel, Stadium stadium, String shortName, String logo, String league) {
        if (name == null) throw new IllegalArgumentException("team name is required");
        Optional<Team> existing = teamRepository.findByName(name);
        if (existing.isPresent()) {
            Team team = existing.get();
            boolean changed = false;
            if (!Objects.equals(team.getLeagueLevel(), leagueLevel)) { team.setLeagueLevel(leagueLevel); changed = true; }
            if (!Objects.equals(team.getStadium(), stadium)) { team.setStadium(stadium); changed = true; }
            if (!Objects.equals(team.getShortName(), shortName)) { team.setShortName(shortName); changed = true; }
            if (!Objects.equals(team.getLogo(), logo)) { team.setLogo(logo); changed = true; }
            if (!Objects.equals(team.getLeague(), league)) { team.setLeague(league); changed = true; }
            return changed ? teamRepository.save(team) : team;
        }
        return teamRepository.save(
                Team.builder()
                        .name(name)
                        .leagueLevel(leagueLevel)
                        .stadium(stadium)
                        .shortName(shortName)
                        .logo(logo)
                        .league(league)
                        .build()
        );
    }

    private void saveOrUpdateSection(Stadium stadium, Team team, String sectionName, String category, Integer price) {
        if (price == null) return;
        sectionRepository.findByStadiumAndTeamAndNameAndCategory(stadium, team, sectionName, category)
                .map(sec -> {
                    sec.setPrice(price);
                    sec.setTeam(team);
                    return sectionRepository.save(sec);
                })
                .orElseGet(() -> sectionRepository.save(
                        Section.builder()
                                .stadium(stadium)
                                .team(team)
                                .name(sectionName)
                                .category(category)
                                .price(price)
                                .build()
                ));
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? null : v.asText();
    }

    private Integer intOrNull(JsonNode node) {
        return node == null || !node.isNumber() ? null : node.intValue();
    }
}
