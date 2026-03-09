package repository;

import domein.Taak;

import java.util.List;

public class TaakDaoJpa extends GenericDaoJpa<Taak> implements TaakDao {
    public TaakDaoJpa(Class<Taak> type) {
        super(Taak.class);
    }

    @Override
    public List<String> findAllTaakTypes() {
        return em.createNamedQuery("Taak.findAllTaakTypes",String.class).getResultList();
    }
}
