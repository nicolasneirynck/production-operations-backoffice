package repository;

import domein.entiteiten.Site;

public interface SiteDao extends GenericDao<Site> {

    public boolean existsByName(String naam, Long id);
}
