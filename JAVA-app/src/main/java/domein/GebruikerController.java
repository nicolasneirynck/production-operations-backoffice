package domein;

import dto.GebruikerDTO;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;
import repository.GenericDao;
import repository.GenericDaoJpa;
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
                        g.getEmail(),
                        g.getGebruikersnaam(),
                        g.getWachtwoord(),
                        g.getStatus(),
                        g.getRol()
                ))
                .toList();
    }

    public void addGebruiker(String email, String gebruikersnaam, String wachtwoord, GebruikerStatus status, Rollen rol) {
        Gebruiker nieuweGebruiker = Gebruiker.builder()
                .email(email).gebruikersnaam(gebruikersnaam).wachtwoord(wachtwoord).status(status).rol(rol)
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

    public void updateGebruiker(long id, String email, String gebruikersnaam, String wachtwoord, GebruikerStatus status, Rollen rol) {
        gebruikerRepo.startTransaction();
        try {
            Gebruiker gebruiker = gebruikerRepo.get(id);

            if (gebruiker == null)
                throw new IllegalArgumentException("Gebruiker niet gevonden.");

            gebruiker.update(email, gebruikersnaam, wachtwoord, status, rol);
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
            if (gebruiker.getStatus() == GebruikerStatus.VERWIJDERD)
                throw new IllegalArgumentException("Gebruiker is al verwijderd.");

            // Soft-delete de gebruiker (= GebruikerStatus naar verwijderd zetten).
            gebruiker.update(gebruiker.getEmail(), gebruiker.getGebruikersnaam(), gebruiker.getWachtwoord(), GebruikerStatus.VERWIJDERD, gebruiker.getRol());
            gebruikerRepo.commitTransaction();
        } catch (RuntimeException ex) {
            gebruikerRepo.rollbackTransaction();
            throw ex;
        }
    }
}
