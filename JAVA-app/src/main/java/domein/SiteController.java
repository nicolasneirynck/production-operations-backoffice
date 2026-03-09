package domein;

import dto.LocatieDTO;
import dto.SiteDTO;
import exception.SiteException;
import repository.SiteDao;
import repository.SiteDaoJpa;
import security.Authorizer;
import security.Permission;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteController {

    private final SiteDao siteRepo;

    public SiteController(SiteDao siteRepo) {
        this.siteRepo = siteRepo;
    }

    //TODO tijdelijk voor devFase -> Mockito
    public SiteController() {
        siteRepo = new SiteDaoJpa();
    }


    private void authorize() {
        Authorizer.require(Permission.SITES_BEHEREN);
    }

    public List<SiteDTO> getAllSites(){
        authorize();

        return siteRepo.findAll().stream()
                .map(this::createDto)
                .toList();
    }

    public void addSite(String naam, String straat, String nummer, String postcode, String gemeente, String land,
                        int capaciteit, OperationeleStatus op, ProductieStatus prod) throws SiteException
    {
        authorize();

        Map<String, IllegalArgumentException> errors = new HashMap<>(); // tijdelijk TODO anders geeft hij enkel locatie fouten

        Locatie locatie = null;
        try {
            locatie = Locatie.builder(straat, nummer, postcode, gemeente, land);
        } catch (SiteException ex) {
            errors.putAll(ex.getExceptionMap());
        }

        Site nieuweSite = null;
        try {
            nieuweSite = Site.builder()
                    .naam(naam)
                    .locatie(locatie)
                    .capaciteit(capaciteit)
                    .operationeleStatus(op)
                    .productieStatus(prod)
                    .build();
        } catch (SiteException ex) {
            errors.putAll(ex.getExceptionMap());
        }

        if (!errors.isEmpty()) {
            throw new SiteException(errors);
        }

        if (siteRepo.existsByName(naam, null)) {
            throw new IllegalArgumentException("Er bestaat al een site met deze naam.");
        }

        siteRepo.startTransaction();
        try {
            siteRepo.insert(nieuweSite);
            siteRepo.commitTransaction();
        }
        catch (RuntimeException ex) {
            siteRepo.rollbackTransaction();
            throw ex;
        }

        //return createDto(nieuweSite);
    }

    public void updateSite(long id, String naam, String straat, String nummer, String postcode, String gemeente, String land,
                           int capaciteit, OperationeleStatus op, ProductieStatus prod) throws SiteException {
        authorize();

        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);

            if (site == null)
                throw new IllegalArgumentException("Site niet gevonden.");

            if (siteRepo.existsByName(naam, id)) {
                throw new IllegalArgumentException("Er bestaat al een site met deze naam.");
            }

            Map<String, IllegalArgumentException> errors = new HashMap<>();

            Locatie locatie = null;
            try {
                locatie = Locatie.builder(straat, nummer, postcode, gemeente, land);
            } catch (SiteException ex) {
                errors.putAll(ex.getExceptionMap());
            }

            try {
                // update() valideert ook business rules (capaciteit>0, statuses, naam, ...)
                site.update(naam, locatie, capaciteit, op, prod);
            } catch (SiteException ex) {
                errors.putAll(ex.getExceptionMap());
            }

            if (!errors.isEmpty()) {
                throw new SiteException(errors);
            }

            siteRepo.commitTransaction();
        } catch (RuntimeException | SiteException ex) {
            siteRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void deleteSite(long id) {
        authorize();

        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);

            if (site == null)
                throw new IllegalArgumentException("Site niet gevonden.");

            siteRepo.delete(site);
            siteRepo.commitTransaction();
        } catch (RuntimeException ex) {
            siteRepo.rollbackTransaction();
            throw ex;
        }
    }

    private SiteDTO createDto(Site site){

        Locatie loc = site.getLocatie();

        LocatieDTO locatieDTO = new LocatieDTO(
                loc.getStraat(),
                loc.getNummer(),
                loc.getPostcode(),
                loc.getGemeente(),
                loc.getLand()
        );

        return new SiteDTO(
                site.getSiteId(),
                site.getNaam(),
                locatieDTO,
                site.getCapaciteit(),
                site.getOperationeleStatus(),
                site.getProductieStatus()
        );
    }
}
