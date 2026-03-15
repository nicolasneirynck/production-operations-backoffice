package domein.controllers;

import dto.SiteDTO;
import dto.TeamDTO;
import exception.ValidationException;

import java.util.List;

public interface AlleTeamsService {
    List<TeamDTO> getAllTeams();
    List<SiteDTO> getBeschikbareSitesVoorNieuwTeam();
    void addTeam(long siteId, List<Long> werknemerIds) throws ValidationException;
    void updateTeam(String teamCode, List<Long> werknemerIds) throws ValidationException;
    void deleteTeam(String teamCode);
}
