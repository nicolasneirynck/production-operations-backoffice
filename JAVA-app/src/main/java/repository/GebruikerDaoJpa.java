package repository;

import domein.Gebruiker;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class GebruikerDaoJpa extends GenericDaoJpa<Gebruiker> implements GebruikerDao {

    public GebruikerDaoJpa() { super(Gebruiker.class); }

    @Override
    public Optional<Gebruiker> findByEmail(String email) {
        TypedQuery<Gebruiker> q = em.createNamedQuery("Gebruiker.findByEmail", Gebruiker.class);
        q.setParameter("email", email);

        return q.getResultStream().findFirst();
    }
}
