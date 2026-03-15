package dto;

import domein.*;
import domein.entiteiten.*;

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

        Gebruiker verantwoordelijke = site.getVerantwoordelijke();
        GebruikerDTO verantwoordelijkeDTO =
                verantwoordelijke != null ? toGebruikerDTO(verantwoordelijke) : null;

        return new SiteDTO(
                site.getId(),
                site.getNaam(),
                verantwoordelijkeDTO,
                toLocatieDTO(site.getLocatie()),
                site.getCapaciteit(),
                site.getOperationeleStatus(),
                site.getProductieStatus());
    }

    public static TaakDTO toTaakDTO(Taak taak) {
        if (taak == null) {
            return null;
        }

        return new TaakDTO(
                taak.getId(),
                taak.getTaakType(),
                taak.getOmschrijving(),
                taak.getDuurtijd()
        );
    }

    public static GebruikerDTO toGebruikerDTO(Gebruiker gebruiker) {
        if (gebruiker == null) {
            return null;
        }

        return new GebruikerDTO(
                gebruiker.getGebruikerId(),
                gebruiker.getPersoneelsnummer(),
                gebruiker.getNaam(),
                gebruiker.getVoornaam(),
                gebruiker.getGeboortedatum(),
                gebruiker.getAdres(),
                gebruiker.getEmail(),
                gebruiker.getGsm(),
                gebruiker.getRol(),
                gebruiker.getStatus(),
                gebruiker.getWachtwoord());
    }

    public static List<GebruikerDTO> toGebruikerDTOList(List<Gebruiker> gebruikers) {
        if (gebruikers == null) {
            return List.of();
        }

        return gebruikers.stream()
                .map(DTOMapper::toGebruikerDTO)
                .collect(Collectors.toList());
    }

    public static TeamDTO toTeamDTO(Team team) {
        if (team == null) {
            return null;
        }

        return new TeamDTO(
                team.getCode(),
                toSiteDTO(team.getSite()),
                toGebruikerDTOList(team.getWerknemers()));
    }
}
