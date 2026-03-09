package dto;

import domein.*;
import dto.*;

import java.util.List;
import java.util.stream.Collectors;

public class DTOMapper {

    public static LocatieDTO toLocatieDTO(Locatie locatie) {

        return new LocatieDTO(
                locatie.getStraat(),
                locatie.getNummer(),
                locatie.getPostcode(),
                locatie.getGemeente(),
                locatie.getLand()
        );
    }


    public static SiteDTO toSiteDTO(Site site) {

        return new SiteDTO(
                site.getSiteId(),
                site.getNaam(),
                toLocatieDTO(site.getLocatie()),
                site.getCapaciteit(),
                site.getOperationeleStatus(),
                site.getProductieStatus()
        );
    }

    public static List<SiteDTO> toSiteDTOList(List<Site> sites) {
        return sites.stream()
                .map(DTOMapper::toSiteDTO)
                .collect(Collectors.toList());
    }

    public static TaakDTO toTaakDTO(Taak taak) {

        return new TaakDTO(
                taak.getTaakId(),
                taak.getTaakType(),
                taak.getOmschrijving(),
                taak.getDuurtijd()
        );
    }

    public static List<TaakDTO> toTaakDTOList(List<Taak> taken) {
        return taken.stream()
                .map(DTOMapper::toTaakDTO)
                .collect(Collectors.toList());
    }

    public static GebruikerDTO toGebruikerDTO(Gebruiker gebruiker) {

        return new GebruikerDTO(
                gebruiker.getGebruikerId(),
                gebruiker.getEmail(),
                gebruiker.getGebruikersnaam(),
                gebruiker.getWachtwoord(), // TODO -> veilig? zinvol?
                gebruiker.getStatus(),
                gebruiker.getRol()
        );
    }

    public static GebruikerDTO teamLidToGebruikerDTO(TeamLid teamLid) {

        Gebruiker gebruiker = teamLid.getWerknemer();

        return new GebruikerDTO(
                teamLid.getTeamLidId(),
                gebruiker.getEmail(),
                gebruiker.getGebruikersnaam(),
                gebruiker.getWachtwoord(), // TODO -> veilig? zinvol?
                gebruiker.getStatus(),
                gebruiker.getRol()
        );
    }

    public static List<GebruikerDTO> toGebruikerDTOList(List<Gebruiker> gebruikers) {
        return gebruikers.stream()
                .map(DTOMapper::toGebruikerDTO)
                .collect(Collectors.toList());
    }

    public static List<GebruikerDTO> teamLedenToGebruikerDTOList(List<TeamLid> teamleden) {
        return teamleden.stream()
                .map(DTOMapper::teamLidToGebruikerDTO)
                .collect(Collectors.toList());
    }

    public static TeamDTO toTeamDTO(Team team) {

        return new TeamDTO(
                team.getCode(),
                DTOMapper.toSiteDTO(team.getSite()),
                toGebruikerDTO(team.getSite().getVerantwoordelijke()),
                teamLedenToGebruikerDTOList(team.getLeden())
        );
    }

    public static List<TeamDTO> toTeamDTOList(List<Team> teams) {
        return teams.stream()
                .map(DTOMapper::toTeamDTO)
                .collect(Collectors.toList());
    }
}