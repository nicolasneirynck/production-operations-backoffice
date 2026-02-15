package repository;

import domein.Site;

import java.util.ArrayList;
import java.util.List;

public class SiteRepository {
    private List<Site> sites = new ArrayList<>();

    public SiteRepository(){
        // tijdelijk
    //    sites.add(new Site("Gent", "België", 100, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND));
        //  sites.add(new Site("Antwerpen", "België", 80, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.PROBLEMEN));
       // sites.add(new Site("Brugge", "België", 60, Site.OperationeleStatus.NON_ACTIEF, Site.ProductieStatus.OFFLINE));
    }

    public List<Site> findAll(){
        //databank ophalen
        return sites;
    }

    public void addSite(Site site){
        sites.add(site);
        // naar databank sturen
    }

    // edit/save
    // delete

}
