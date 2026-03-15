package domein.services;

import domein.beheerders.GebruikerBeheerder;
import dto.DTOMapper;
import dto.GebruikerDTO;
import security.Authorizer;
import security.Permission;
import util.GebruikerStatus;
import util.Rollen;

import java.time.LocalDate;
import java.util.List;

public class GebruikerService {

    private final GebruikerBeheerder gebruikerBeheerder;

    public GebruikerService(GebruikerBeheerder gebruikerBeheerder) {
        this.gebruikerBeheerder = gebruikerBeheerder;
    }

    public GebruikerService() {
        this(new GebruikerBeheerder());
    }

    private void authorize() {
        Authorizer.require(Permission.GEBRUIKERS_BEHEREN);
    }

    public List<GebruikerDTO> getAllGebruikers() {
        authorize();
        return gebruikerBeheerder.getAllGebruikers().stream()
                .map(DTOMapper::toGebruikerDTO)
                .toList();
    }

    public List<GebruikerDTO> getWerknemersVoorTeamBeheer() {
        Authorizer.requireAny(Permission.ALLE_TEAMS_BEHEREN, Permission.MIJN_TEAM_BEHEREN);
        return gebruikerBeheerder.getAllGebruikers().stream()
                .map(DTOMapper::toGebruikerDTO)
                .filter(gebruiker -> gebruiker.rol() == Rollen.WERKNEMER)
                .toList();
    }

    public void addGebruiker(String naam, String voornaam, LocalDate geboortedatum,
                             String straat, String nummer, String postcode, String gemeente, String land,
                             String email, String gsm, Rollen rol, GebruikerStatus status,
                             String wachtwoord) {
        authorize();
        gebruikerBeheerder.addGebruiker(
                naam, voornaam, geboortedatum, straat, nummer, postcode, gemeente, land,
                email, gsm, rol, status, wachtwoord
        );
    }

    public void updateGebruiker(long id, String naam, String voornaam, LocalDate geboortedatum,
                                String straat, String nummer, String postcode, String gemeente, String land,
                                String email, String gsm, Rollen rol, GebruikerStatus status,
                                String wachtwoord) {
        authorize();
        gebruikerBeheerder.updateGebruiker(
                id, naam, voornaam, geboortedatum, straat, nummer, postcode, gemeente, land,
                email, gsm, rol, status, wachtwoord
        );
    }

    public void deleteGebruiker(long id) {
        authorize();
        gebruikerBeheerder.deleteGebruiker(id);
    }

    public List<GebruikerDTO> getVerantwoordelijkenZonderSite() {
        Authorizer.requireAny(Permission.GEBRUIKERS_BEHEREN, Permission.SITES_BEHEREN);
        return gebruikerBeheerder.getVerantwoordelijkenZonderSite().stream()
                .map(DTOMapper::toGebruikerDTO)
                .toList();
    }
}
