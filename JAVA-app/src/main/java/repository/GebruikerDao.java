package repository;

import domein.Gebruiker;

import java.util.Optional;

public interface GebruikerDao extends GenericDao<Gebruiker> {

    public Optional<Gebruiker> findByEmail(String email);
}
