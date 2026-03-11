package domein.controllers;

import domein.TeamBeheerder;
import dto.DTOMapper;
import dto.TeamDTO;
import exception.ValidationException;
import security.SecurityContext;
import security.UserPrincipal;

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

    public TeamDTO getMijnTeam() {
        UserPrincipal user = requireAuthenticatedUser();

        return DTOMapper.toTeamDTO(
                teamBeheerder.getTeamVanVerantwoordelijke(user.gebruikerId())
        );
    }

    public void updateMijnTeam(long teamCode, List<Long> werknemerIds) throws ValidationException {
        UserPrincipal user = requireAuthenticatedUser();

        teamBeheerder.updateEigenTeam(user.gebruikerId(), teamCode, werknemerIds);
    }

    private UserPrincipal requireAuthenticatedUser() {
        UserPrincipal user = SecurityContext.getUser();
        if (user == null) {
            throw new IllegalStateException("Geen ingelogde gebruiker.");
        }
        return user;
    }
}