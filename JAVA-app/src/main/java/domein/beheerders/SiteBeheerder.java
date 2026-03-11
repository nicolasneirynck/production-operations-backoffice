package domein.beheerders;

import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import domein.entiteiten.Site;
import exception.ValidationException;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;
import repository.SiteDao;
import repository.SiteDaoJpa;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteBeheerder {

    private final SiteDao siteRepo;
    private final GebruikerDao gebruikerRepo;

    // tijdelijk voor devfase
    public SiteBeheerder(SiteDao siteRepo, GebruikerDao gebruikerRepo) {
        this.siteRepo = siteRepo;
        this.gebruikerRepo = gebruikerRepo;
    }

    public SiteBeheerder() {
        this(new SiteDaoJpa(), new GebruikerDaoJpa());
    }

    public List<Site> getAllSites() {
        return siteRepo.findAll();
    }

    public void addSite(String naam, Long verantwoordelijkeId, String straat, String nummer, String postcode, String gemeente, String land,
                        int capaciteit, OperationeleStatus op, ProductieStatus prod) throws ValidationException {

        Map<String, IllegalArgumentException> errors = new HashMap<>();

        Locatie locatie = null;
        try {
            locatie = Locatie.builder(straat, nummer, postcode, gemeente, land);
        } catch (ValidationException ex) {
            errors.putAll(ex.getExceptionMap());
        }

        Gebruiker verantwoordelijke = null;
        if (verantwoordelijkeId == null) {
            errors.put("verantwoordelijke", new IllegalArgumentException("Verantwoordelijke is verplicht."));
        } else {
            verantwoordelijke = gebruikerRepo.get(verantwoordelijkeId);

            if (verantwoordelijke == null) {
                errors.put("verantwoordelijke", new IllegalArgumentException("Verantwoordelijke niet gevonden."));
            } else if (verantwoordelijke.getRol() != Rollen.VERANTWOORDELIJKE) {
                errors.put("verantwoordelijke", new IllegalArgumentException("Gebruiker moet de rol VERANTWOORDELIJKE hebben."));
            } else if (siteRepo.verantwoordelijkeHeeftAndereSite(verantwoordelijkeId, null)) {
                errors.put("verantwoordelijke",
                        new IllegalArgumentException("Deze verantwoordelijke is al aan een andere site gekoppeld."));
            }
        }

        Site nieuweSite = null;
        try {
            nieuweSite = Site.builder()
                    .naam(naam)
                    .verantwoordelijke(verantwoordelijke)
                    .locatie(locatie)
                    .capaciteit(capaciteit)
                    .operationeleStatus(op)
                    .productieStatus(prod)
                    .build();
        } catch (ValidationException ex) {
            errors.putAll(ex.getExceptionMap());
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
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

    public void updateSite(long id, String naam, Long verantwoordelijkeId, String straat, String nummer,
                           String postcode, String gemeente, String land,
                           int capaciteit, OperationeleStatus op, ProductieStatus prod) throws ValidationException {

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
            } catch (ValidationException ex) {
                errors.putAll(ex.getExceptionMap());
            }

            Gebruiker verantwoordelijke = null;
            if (verantwoordelijkeId == null) {
                errors.put("verantwoordelijke", new IllegalArgumentException("Verantwoordelijke is verplicht."));
            } else {
                verantwoordelijke = gebruikerRepo.get(verantwoordelijkeId);

                if (verantwoordelijke == null) {
                    errors.put("verantwoordelijke", new IllegalArgumentException("Verantwoordelijke niet gevonden."));
                } else if (verantwoordelijke.getRol() != Rollen.VERANTWOORDELIJKE) {
                    errors.put("verantwoordelijke", new IllegalArgumentException("Gebruiker moet de rol VERANTWOORDELIJKE hebben."));
                } else if (siteRepo.verantwoordelijkeHeeftAndereSite(verantwoordelijkeId, null)) {
                    errors.put("verantwoordelijke",
                            new IllegalArgumentException("Deze verantwoordelijke is al aan een andere site gekoppeld."));
                }
            }

            try {
                site.update(naam, verantwoordelijke, locatie, capaciteit, op, prod);
            } catch (ValidationException ex) {
                errors.putAll(ex.getExceptionMap());
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            siteRepo.commitTransaction();
        } catch (RuntimeException | ValidationException ex) {
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

    public List<Site> getSitesZonderVerantwoordelijke() {
        return siteRepo.findSitesZonderVerantwoordelijke();
    }

    public List<Site> getSitesZonderTeam() {
        return siteRepo.findSitesZonderTeam();
    }
}