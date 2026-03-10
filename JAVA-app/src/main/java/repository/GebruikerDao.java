package repository;

import domein.entiteiten.Gebruiker;

import java.util.Optional;

public interface GebruikerDao extends GenericDao<Gebruiker> {

    public Optional<Gebruiker> findByEmail(String email);
}
