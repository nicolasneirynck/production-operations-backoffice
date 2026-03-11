package repository;

import domein.entiteiten.Site;
import jakarta.persistence.TypedQuery;

import java.util.List;

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

    @Override
    public List<Site> findSitesZonderVerantwoordelijke() {
        return em.createQuery(
                "select s from Site s where s.verantwoordelijke is null",
                Site.class
        ).getResultList();
    }

    @Override
    public List<Site> findSitesZonderTeam() {
        return em.createQuery("""
            SELECT s
            FROM Site s
            WHERE s.team IS NULL
            """, Site.class)
                .getResultList();
    }

    @Override
    public boolean verantwoordelijkeHeeftAndereSite(long gebruikerId, Long huidigeSiteId) {
        String jpql = """
        SELECT COUNT(s)
        FROM Site s
        WHERE s.verantwoordelijke.gebruikerId = :gebruikerId
    """;

        if (huidigeSiteId != null) {
            jpql += " AND s.id <> :huidigeSiteId";
        }

        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                .setParameter("gebruikerId", gebruikerId);

        if (huidigeSiteId != null) {
            query.setParameter("huidigeSiteId", huidigeSiteId);
        }

        return query.getSingleResult() > 0;
    }
}
