package domein.controllers;

import domein.beheerders.SiteBeheerder;
import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import domein.entiteiten.Site;
import dto.DTOMapper;
import dto.GebruikerDTO;
import dto.LocatieDTO;
import dto.SiteDTO;
import exception.ValidationException;
import security.Authorizer;
import security.Permission;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.List;

public class SiteController {

    private final SiteBeheerder siteBeheerder;

    public SiteController(SiteBeheerder siteBeheerder) {
        this.siteBeheerder = siteBeheerder;
    }

    // tijdelijk voor devfase
    public SiteController() {
        this(new SiteBeheerder());
    }

    private void authorize() {
        Authorizer.require(Permission.SITES_BEHEREN);
    }

    public List<SiteDTO> getAllSites() {
        authorize();

        return siteBeheerder.getAllSites().stream()
                .map(this::toDto)
                .toList();
    }

    public void addSite(String naam, Long verantwoordelijkeId, String straat, String nummer, String postcode, String gemeente, String land,
                        int capaciteit, OperationeleStatus op, ProductieStatus prod) throws ValidationException {
        authorize();
        siteBeheerder.addSite(naam, verantwoordelijkeId, straat, nummer, postcode, gemeente, land, capaciteit, op, prod);
    }

    public void updateSite(long id, Long verantwoordelijkeId, String naam, String straat, String nummer, String postcode, String gemeente, String land,
                           int capaciteit, OperationeleStatus op, ProductieStatus prod) throws ValidationException {
        authorize();
        siteBeheerder.updateSite(id, naam, verantwoordelijkeId, straat, nummer, postcode, gemeente, land, capaciteit, op, prod);
    }

    public void deleteSite(long id) {
        authorize();
        siteBeheerder.deleteSite(id);
    }

    private SiteDTO toDto(Site site) {
        Locatie loc = site.getLocatie();

        LocatieDTO locatie = new LocatieDTO(
                loc.getStraat(),
                loc.getNummer(),
                loc.getPostcode(),
                loc.getGemeente(),
                loc.getLand()
        );

        GebruikerDTO verantwoordelijke = null;
        Gebruiker g = site.getVerantwoordelijke();

        if (g != null) {
            verantwoordelijke = new GebruikerDTO(
                    g.getGebruikerId(),
                    g.getPersoneelsnummer(),
                    g.getNaam(),
                    g.getVoornaam(),
                    g.getGeboortedatum(),
                    g.getAdres(),
                    g.getEmail(),
                    g.getGsm(),
                    g.getRol(),
                    g.getStatus(),
                    g.getWachtwoord()
            );
        }

        return new SiteDTO(
                site.getId(),
                site.getNaam(),
                verantwoordelijke,
                locatie,
                site.getCapaciteit(),
                site.getOperationeleStatus(),
                site.getProductieStatus()
        );
    }

    public List<SiteDTO> getSitesZonderTeam() {
        return siteBeheerder.getSitesZonderTeam().stream()
                .map(DTOMapper::toSiteDTO)
                .toList();
    }
}