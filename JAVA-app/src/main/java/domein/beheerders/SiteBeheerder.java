package domein.beheerders;

import domein.entiteiten.Locatie;
import domein.entiteiten.Site;
import exception.SiteException;
import repository.SiteDao;
import repository.SiteDaoJpa;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteBeheerder {

    private final SiteDao siteRepo;

    // tijdelijk voor devfase
    public SiteBeheerder(SiteDao siteRepo) {
        this.siteRepo = siteRepo;
    }

    public SiteBeheerder() {
        this(new SiteDaoJpa());
    }

    public List<Site> getAllSites() {
        return siteRepo.findAll();
    }

    public void addSite(String naam, String straat, String nummer, String postcode, String gemeente, String land,
                        int capaciteit, OperationeleStatus op, ProductieStatus prod) throws SiteException {

        Map<String, IllegalArgumentException> errors = new HashMap<>();

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
        } catch (RuntimeException ex) {
            siteRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void updateSite(long id, String naam, String straat, String nummer, String postcode, String gemeente, String land,
                           int capaciteit, OperationeleStatus op, ProductieStatus prod) throws SiteException {

        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);

            if (site == null) {
                throw new IllegalArgumentException("Site niet gevonden.");
            }

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
        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);

            if (site == null) {
                throw new IllegalArgumentException("Site niet gevonden.");
            }

            siteRepo.delete(site);
            siteRepo.commitTransaction();
        } catch (RuntimeException ex) {
            siteRepo.rollbackTransaction();
            throw ex;
        }
    }
}