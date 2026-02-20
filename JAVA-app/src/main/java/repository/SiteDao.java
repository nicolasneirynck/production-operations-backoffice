package repository;

import domein.Site;

public interface SiteDao extends GenericDao<Site> {

    public boolean existsByName(String naam, Long id);
}
