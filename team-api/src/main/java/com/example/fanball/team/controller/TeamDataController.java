package com.example.fanball.team.controller;

import com.example.fanball.team.exception.ErrorCode;
import com.example.fanball.team.exception.TeamException;
import com.example.fanball.team.service.TeamDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.fanball.team.exception.ErrorCode.FAIL_TEAM_DATA_INPUT;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamDataController {
    private final TeamDataService teamDataService;

    @PostMapping("/teams")
    public ResponseEntity<String> teamData() {
        try {
            teamDataService.importFromJson("team/teams.json");
            return ResponseEntity.ok("teams.json 데이터 삽입 완료");
        } catch (Exception e) {
           throw new TeamException(FAIL_TEAM_DATA_INPUT);
        }
    }
}
