package repository;

import domein.entiteiten.Gebruiker;

import java.util.List;
import java.util.Optional;

public interface GebruikerDao extends GenericDao<Gebruiker> {

    public Optional<Gebruiker> findByEmail(String email);
    List<Gebruiker> findVerantwoordelijkenZonderSite();
}
