package repository;

import domein.entiteiten.Gebruiker;
import jakarta.persistence.TypedQuery;
import util.Rollen;

import java.util.List;
import java.util.Optional;

public class GebruikerDaoJpa extends GenericDaoJpa<Gebruiker> implements GebruikerDao {

    public GebruikerDaoJpa() { super(Gebruiker.class); }

    @Override
    public Optional<Gebruiker> findByEmail(String email) {
        TypedQuery<Gebruiker> q = em.createNamedQuery("Gebruiker.findByEmail", Gebruiker.class);
        q.setParameter("email", email);

        return q.getResultStream().findFirst();
    }

    @Override
    public List<Gebruiker> findVerantwoordelijkenZonderSite() {
        return em.createQuery("""
        SELECT g
        FROM Gebruiker g
        WHERE g.rol = :rol
          AND g NOT IN (
              SELECT s.verantwoordelijke
              FROM Site s
              WHERE s.verantwoordelijke IS NOT NULL
          )
        ORDER BY g.naam, g.voornaam
        """, Gebruiker.class)
                .setParameter("rol", Rollen.VERANTWOORDELIJKE)
                .getResultList();
    }}
