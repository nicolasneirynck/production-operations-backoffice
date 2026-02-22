package domein;

import dto.SiteDTO;
import repository.SiteDao;
import repository.SiteDaoJpa;
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

    public List<SiteDTO> getAllSites(){
        return siteRepo.findAll().stream()
                .map(this::createDto)
                .toList();
    }

    public SiteDTO addSite(String naam, String locatie, int capaciteit, OperationeleStatus op, ProductieStatus prod)
    {
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

        return createDto(nieuweSite);
    }

    public SiteDTO updateSite(long id, String naam, String locatie, int capaciteit,
                           OperationeleStatus op, ProductieStatus prod) {

        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);
            if (site == null)
                throw new IllegalArgumentException("Site niet gevonden.");

            site.update(naam, locatie, capaciteit, op, prod);

            if (siteRepo.existsByName(naam,id)) {
                throw new IllegalArgumentException("Er bestaat al een site met deze naam.");
            }

            siteRepo.commitTransaction();

            return createDto(site);
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
