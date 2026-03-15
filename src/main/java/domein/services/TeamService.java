package domein.services;

import domein.beheerders.SiteBeheerder;
import domein.beheerders.TeamBeheerder;
import dto.DTOMapper;
import dto.SiteDTO;
import dto.TeamDTO;
import exception.ValidationException;
import security.Authorizer;
import security.Permission;
import security.UserPrincipal;

import java.util.List;
import java.util.Optional;

public class TeamService implements AlleTeamsService, MijnTeamService {

    private final TeamBeheerder teamBeheerder;
    private final SiteBeheerder siteBeheerder;

    public TeamService(TeamBeheerder teamBeheerder, SiteBeheerder siteBeheerder) {
        this.teamBeheerder = teamBeheerder;
        this.siteBeheerder = siteBeheerder;
    }

    public TeamService() {
        this(new TeamBeheerder(), new SiteBeheerder());
    }

    @Override
    public List<TeamDTO> getAllTeams() {
        Authorizer.require(Permission.ALLE_TEAMS_BEHEREN);

        return teamBeheerder.getAllTeams().stream()
                .map(DTOMapper::toTeamDTO)
                .toList();
    }

    @Override
    public List<SiteDTO> getBeschikbareSitesVoorNieuwTeam() {
        Authorizer.require(Permission.ALLE_TEAMS_BEHEREN);

        return siteBeheerder.getSitesZonderTeam().stream()
                .map(DTOMapper::toSiteDTO)
                .toList();
    }

    @Override
    public void addTeam(long siteId, List<Long> werknemerIds) throws exception.ValidationException {
        Authorizer.require(Permission.ALLE_TEAMS_BEHEREN);

        teamBeheerder.addTeam(siteId, werknemerIds);
    }

    @Override
    public void updateTeam(String teamCode, List<Long> werknemerIds) throws exception.ValidationException {
        Authorizer.require(Permission.ALLE_TEAMS_BEHEREN);

        teamBeheerder.updateTeam(teamCode, werknemerIds);
    }

    @Override
    public void deleteTeam(String teamCode) {
        Authorizer.require(Permission.ALLE_TEAMS_BEHEREN);

        teamBeheerder.deleteTeam(teamCode);
    }

    @Override
    public Optional<TeamDTO> getMijnTeam() {
        UserPrincipal user = Authorizer.require(Permission.MIJN_TEAM_BEHEREN);

        return teamBeheerder.findMijnTeam(user.gebruikerId())
                .map(DTOMapper::toTeamDTO);
    }

    @Override
    public void updateMijnTeam(String teamCode, List<Long> werknemerIds) throws ValidationException {
        UserPrincipal user = Authorizer.require(Permission.MIJN_TEAM_BEHEREN);
        teamBeheerder.updateEigenTeam(user.gebruikerId(), teamCode, werknemerIds);
    }
}
