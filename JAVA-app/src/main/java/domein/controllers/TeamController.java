package domein.controllers;

import domein.TeamBeheerder;
import dto.DTOMapper;
import dto.TeamDTO;
import exception.ValidationException;
import security.SecurityContext;
import security.UserPrincipal;

import java.util.List;
import java.util.Optional;

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

    public void updateTeam(String teamCode, List<Long> werknemerIds) throws exception.ValidationException {
        teamBeheerder.updateTeam(teamCode, werknemerIds);
    }

    public void deleteTeam(String teamCode) {
        teamBeheerder.deleteTeam(teamCode);
    }

    public Optional<TeamDTO> getMijnTeam() {
        UserPrincipal user = requireAuthenticatedUser();

        return teamBeheerder.findTeamVanVerantwoordelijke(user.gebruikerId())
                .map(DTOMapper::toTeamDTO);
    }

    public void updateMijnTeam(String teamCode, List<Long> werknemerIds) throws ValidationException {
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
