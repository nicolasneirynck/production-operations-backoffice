package repository;

import domein.entiteiten.Site;

import java.util.List;

public interface SiteDao extends GenericDao<Site> {

    boolean existsByName(String naam, Long id);
    List<Site> findSitesZonderVerantwoordelijke();
}
