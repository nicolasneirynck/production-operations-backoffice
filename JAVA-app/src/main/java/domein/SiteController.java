package domein;

import repository.GenericDao;
import repository.GenericDaoJpa;

import java.util.ArrayList;
import java.util.List;

public class SiteController {

    private GenericDao<Site> siteRepo;

    public SiteController() {
        this(new GenericDaoJpa<>(Site.class));
    }

    //mockito testing
    public SiteController(GenericDao<Site> siteRepo) {
        this.siteRepo = siteRepo;
    }

    public void addSite(String naam, String locatie, int capaciteit, Site.OperationeleStatus op, Site.ProductieStatus prod)
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

    public List<Site> getAllSites(){
        return siteRepo.findAll();
    }

    //addSite

    //updateSite

    //deleteSite
}
