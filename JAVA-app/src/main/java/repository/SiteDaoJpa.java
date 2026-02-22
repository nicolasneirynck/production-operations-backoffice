package repository;

import domein.Site;

public class SiteDaoJpa extends GenericDaoJpa<Site> implements SiteDao {

    public SiteDaoJpa(){super(Site.class);}

    @Override
    public boolean existsByName(String naam, Long id) {
        Long count = em.createNamedQuery("Site.existsByName", Long.class)
                .setParameter("naam", naam.toLowerCase())
                .setParameter("id", id)   // null bij add, id bij update
                .getSingleResult();
        return count > 0;
    }
}
