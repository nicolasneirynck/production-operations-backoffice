package domein;

import domein.entiteiten.Gebruiker;
import dto.GebruikerDTO;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;
import util.GebruikerStatus;
import util.Rollen;

import java.util.List;

public class GebruikerController {
    private final GebruikerDao gebruikerRepo;

    public GebruikerController() {
        this(new GebruikerDaoJpa());
    }

    //TODO tijdelijk voor devFase -> Mockito
    public GebruikerController(GebruikerDao gebruikerRepo) {
        this.gebruikerRepo = gebruikerRepo;
    }

    public List<GebruikerDTO> getAllGebruikers(){
        return gebruikerRepo.findAll().stream()
                .map(g -> new GebruikerDTO(
                        g.getGebruikerId(),
                        g.getPersoneelsnummer(),
                        g.getNaam(),
                        g.getVoornaam(),
                        g.getGeboortedatum(),
                        g.getAdres(),
                        g.getEmail(),
                        g.getGsm(),
                        g.getRol(),
                        g.getStatus(),
                        g.getWachtwoord()
                ))
                .toList();
    }

    public void addGebruiker(int personeelsnummer, String naam, String voornaam, String geboortedatum, String adres, String email, String gsm, Rollen rol, GebruikerStatus status, String wachtwoord) {
        Gebruiker nieuweGebruiker = Gebruiker.builder().personeelsnummer(personeelsnummer).naam(naam).voornaam(voornaam).geboortedatum(geboortedatum).adres(adres)
                .email(email).gsm(gsm).rol(rol).status(status).wachtwoord(wachtwoord)
                .build();

        gebruikerRepo.startTransaction();
        try {
            gebruikerRepo.insert(nieuweGebruiker);
            gebruikerRepo.commitTransaction();
        }
        catch (RuntimeException ex) {
            gebruikerRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void updateGebruiker(long id, int personeelsnummer, String naam, String voornaam, String geboortedatum, String adres, String email, String gsm, Rollen rol, GebruikerStatus status, String wachtwoord) {
        gebruikerRepo.startTransaction();
        try {
            Gebruiker gebruiker = gebruikerRepo.get(id);

            if (gebruiker == null)
                throw new IllegalArgumentException("Gebruiker niet gevonden.");

            gebruiker.update(personeelsnummer, naam, voornaam, geboortedatum, adres, email, gsm, rol, status, wachtwoord);
            gebruikerRepo.commitTransaction();
        } catch (RuntimeException ex) {
            gebruikerRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void deleteGebruiker(long id) {
        gebruikerRepo.startTransaction();
        try {
            Gebruiker gebruiker = gebruikerRepo.get(id);

            if (gebruiker == null)
                throw new IllegalArgumentException("Gebruiker niet gevonden.");
            if (gebruiker.getStatus() == GebruikerStatus.INACTIEF)
                throw new IllegalArgumentException("Gebruiker is al verwijderd.");

            // Soft-delete de gebruiker (= GebruikerStatus naar verwijderd zetten).
            gebruiker.update(gebruiker.getPersoneelsnummer(), gebruiker.getNaam(), gebruiker.getVoornaam(), gebruiker.getGeboortedatum(), gebruiker.getAdres(), gebruiker.getEmail(), gebruiker.getGsm(), gebruiker.getRol(), GebruikerStatus.INACTIEF, gebruiker.getWachtwoord());
            gebruikerRepo.commitTransaction();
        } catch (RuntimeException ex) {
            gebruikerRepo.rollbackTransaction();
            throw ex;
        }
    }
}
