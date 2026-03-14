package domein.controllers;

import domein.beheerders.TeamBeheerder;
import dto.DTOMapper;
import dto.TeamDTO;
import exception.ValidationException;
import security.SecurityContext;
import security.UserPrincipal;

import java.util.List;
import java.util.Optional;

public class TeamService implements AlleTeamsService, MijnTeamService {

    private final TeamBeheerder teamBeheerder;

    // Mockito
    public TeamService(TeamBeheerder teamBeheerder) {
        this.teamBeheerder = teamBeheerder;
    }

    public TeamService() {
        this(new TeamBeheerder());
    }

    @Override
    public List<TeamDTO> getAllTeams() {
        return teamBeheerder.getAllTeams().stream()
                .map(DTOMapper::toTeamDTO)
                .toList();
    }

    @Override
    public void addTeam(long siteId, List<Long> werknemerIds) throws exception.ValidationException {
        teamBeheerder.addTeam(siteId, werknemerIds);
    }

    @Override
    public void updateTeam(String teamCode, List<Long> werknemerIds) throws exception.ValidationException {
        teamBeheerder.updateTeam(teamCode, werknemerIds);
    }

    @Override
    public void deleteTeam(String teamCode) {
        teamBeheerder.deleteTeam(teamCode);
    }

    @Override
    public Optional<TeamDTO> getMijnTeam() {
        UserPrincipal user = requireAuthenticatedUser();

        return teamBeheerder.findMijnTeam(user.gebruikerId())
                .map(DTOMapper::toTeamDTO);
    }

    @Override
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
