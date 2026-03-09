package dto;

import domein.*;

import java.util.List;
import java.util.stream.Collectors;

public class DTOMapper {

    public static LocatieDTO toLocatieDTO(Locatie locatie) {
        if (locatie == null) {
            return null;
        }

        return new LocatieDTO(
                locatie.getStraat(),
                locatie.getNummer(),
                locatie.getPostcode(),
                locatie.getGemeente(),
                locatie.getLand()
        );
    }

    public static SiteDTO toSiteDTO(Site site) {
        if (site == null) {
            return null;
        }

        return new SiteDTO(
                site.getSiteId(),
                site.getNaam(),
                toLocatieDTO(site.getLocatie()),
                site.getCapaciteit(),
                site.getOperationeleStatus(),
                site.getProductieStatus(),
                toGebruikerDTO(site.getVerantwoordelijke())
        );
    }

    public static List<SiteDTO> toSiteDTOList(List<Site> sites) {
        if (sites == null) {
            return List.of();
        }

        return sites.stream()
                .map(DTOMapper::toSiteDTO)
                .collect(Collectors.toList());
    }

    public static TaakDTO toTaakDTO(Taak taak) {
        if (taak == null) {
            return null;
        }

        return new TaakDTO(
                taak.getTaakId(),
                taak.getTaakType(),
                taak.getOmschrijving(),
                taak.getDuurtijd()
        );
    }

    public static List<TaakDTO> toTaakDTOList(List<Taak> taken) {
        if (taken == null) {
            return List.of();
        }

        return taken.stream()
                .map(DTOMapper::toTaakDTO)
                .collect(Collectors.toList());
    }

    public static GebruikerDTO toGebruikerDTO(Gebruiker gebruiker) {
        if (gebruiker == null) {
            return null;
        }

        return new GebruikerDTO(
                gebruiker.getGebruikerId(),
                gebruiker.getEmail(),
                gebruiker.getGebruikersnaam(),
                gebruiker.getWachtwoord(), // TODO -> liever niet meegeven in DTO
                gebruiker.getStatus(),
                gebruiker.getRol()
        );
    }

    public static GebruikerDTO teamLidToGebruikerDTO(TeamLid teamLid) {
        if (teamLid == null) {
            return null;
        }

        Gebruiker gebruiker = teamLid.getWerknemer();
        return toGebruikerDTO(gebruiker);
    }

    public static List<GebruikerDTO> toGebruikerDTOList(List<Gebruiker> gebruikers) {
        if (gebruikers == null) {
            return List.of();
        }

        return gebruikers.stream()
                .map(DTOMapper::toGebruikerDTO)
                .collect(Collectors.toList());
    }

    public static List<GebruikerDTO> teamLedenToGebruikerDTOList(List<TeamLid> teamleden) {
        if (teamleden == null) {
            return List.of();
        }

        return teamleden.stream()
                .map(DTOMapper::teamLidToGebruikerDTO)
                .collect(Collectors.toList());
    }

    public static TeamDTO toTeamDTO(Team team) {
        if (team == null) {
            return null;
        }

        return new TeamDTO(
                team.getCode() == null ? 0L : team.getCode(),
                toSiteDTO(team.getSite()),
                team.getSite() == null ? null : toGebruikerDTO(team.getSite().getVerantwoordelijke()),
                teamLedenToGebruikerDTOList(team.getLeden())
        );
    }

    public static List<TeamDTO> toTeamDTOList(List<Team> teams) {
        if (teams == null) {
            return List.of();
        }

        return teams.stream()
                .map(DTOMapper::toTeamDTO)
                .collect(Collectors.toList());
    }
}