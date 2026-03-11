package domein.controllers;

import domein.TeamBeheerder;
import dto.DTOMapper;
import dto.TeamDTO;

import java.util.List;

public class TeamController {

    private final TeamBeheerder teamBeheerder;

    // Mockito
    public TeamController(TeamBeheerder teamBeheerder) {
        this.teamBeheerder = teamBeheerder;
    }

    public TeamController() {
        this(new TeamBeheerder());
    }

    public List<TeamDTO> getAllTeams() {
        return teamBeheerder.getAllTeams().stream()
                .map(DTOMapper::toTeamDTO)
                .toList();
    }

    public void addTeam(long siteId, List<Long> werknemerIds) throws exception.ValidationException {
        teamBeheerder.addTeam(siteId, werknemerIds);
    }

    public void updateTeam(long teamCode, List<Long> werknemerIds) throws exception.ValidationException {
        teamBeheerder.updateTeam(teamCode, werknemerIds);
    }

    public void deleteTeam(long teamCode) {
        teamBeheerder.deleteTeam(teamCode);
    }
}