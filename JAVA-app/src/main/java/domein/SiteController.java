package domein;

import dto.SiteDTO;
import repository.GenericDao;
import repository.GenericDaoJpa;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.List;

public class SiteController {

    private final GenericDao<Site> siteRepo;

    public SiteController() {
        this(new GenericDaoJpa<>(Site.class));
    }

    //mockito testing
    public SiteController(GenericDao<Site> siteRepo) {
        this.siteRepo = siteRepo;
    }

    public List<SiteDTO> getAllSites(){
        return siteRepo.findAll().stream()
                .map(s -> new SiteDTO(
                        s.getSiteId(),
                        s.getNaam(),
                        s.getLocatie(),
                        s.getCapaciteit(),
                        s.getOperationeleStatus(),
                        s.getProductieStatus()
                ))
                .toList();
    }

    public void addSite(String naam, String locatie, int capaciteit, OperationeleStatus op, ProductieStatus prod)
    {
        Site nieuweSite = Site.builder()
                    .naam(naam).locatie(locatie).capaciteit(capaciteit).operationeleStatus(op).productieStatus(prod)
                    .build();

        siteRepo.startTransaction();
        try {
            siteRepo.insert(nieuweSite);
            siteRepo.commitTransaction();
        }
        catch (RuntimeException ex) {
            siteRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void updateSite(long id, String naam, String locatie, int capaciteit,
                           OperationeleStatus op, ProductieStatus prod) {

        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);
            site.update(naam, locatie, capaciteit, op, prod);
            siteRepo.commitTransaction();
        } catch (RuntimeException ex) {
            siteRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void deleteSite(long id) {
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
}
