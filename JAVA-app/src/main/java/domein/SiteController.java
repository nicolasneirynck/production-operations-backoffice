package domein;

import dto.SiteDTO;
import exception.SiteException;
import repository.SiteDao;
import repository.SiteDaoJpa;
import security.Authorizer;
import security.Permission;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.List;

public class SiteController {

    private final SiteDao siteRepo;

    public SiteController() {
        siteRepo = new SiteDaoJpa();
    }

    //TODO tijdelijk voor devFase -> Mockito
    public SiteController(SiteDao siteRepo) {
        this.siteRepo = siteRepo;
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

    public void addSite(String naam, String locatie, Integer capaciteit, OperationeleStatus op, ProductieStatus prod) throws SiteException
    {
        authorize();

        Site nieuweSite = Site.builder()
                    .naam(naam).locatie(locatie).capaciteit(capaciteit).operationeleStatus(op).productieStatus(prod)
                    .build();

        if (siteRepo.existsByName(naam,null)) {
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

    public void updateSite(long id, String naam, String locatie, Integer capaciteit,
                           OperationeleStatus op, ProductieStatus prod) throws SiteException {
        authorize();

        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);
            if (site == null)
                throw new IllegalArgumentException("Site niet gevonden.");

            if (siteRepo.existsByName(naam,id)) {
                throw new IllegalArgumentException("Er bestaat al een site met deze naam.");
            }

            site.update(naam, locatie, capaciteit, op, prod);

            siteRepo.commitTransaction();

            //return createDto(site);
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
        return new SiteDTO(
                site.getSiteId(),
                site.getNaam(),
                site.getLocatie(),
                site.getCapaciteit(),
                site.getOperationeleStatus(),
                site.getProductieStatus()
        );
    }
}
