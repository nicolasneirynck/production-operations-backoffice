package domein.services;

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

public class SiteService {

    private final SiteBeheerder siteBeheerder;

    public SiteService(SiteBeheerder siteBeheerder) {
        this.siteBeheerder = siteBeheerder;
    }

    // tijdelijk voor devfase
    public SiteService() {
        this(new SiteBeheerder());
    }

    private void authorize() {
        Authorizer.require(Permission.SITES_BEHEREN);
    }

    public List<SiteDTO> getAllSites() {
        authorize();

        return siteBeheerder.getAllSites().stream()
                .map(DTOMapper::toSiteDTO)
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
}
