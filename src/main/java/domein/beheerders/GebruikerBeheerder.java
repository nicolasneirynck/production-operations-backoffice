package domein.beheerders;

import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import exception.ValidationException;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;
import security.PasswordHasher;
import util.GebruikerStatus;
import util.Rollen;

import java.time.LocalDate;
import java.util.List;

public class GebruikerBeheerder {

    private final GebruikerDao gebruikerRepo;

    public GebruikerBeheerder(GebruikerDao gebruikerRepo) {
        this.gebruikerRepo = gebruikerRepo;
    }

    public GebruikerBeheerder() {
        this(new GebruikerDaoJpa());
    }

    public List<Gebruiker> getAllGebruikers() {
        return gebruikerRepo.findAll();
    }

    public void addGebruiker(String naam, String voornaam, LocalDate geboortedatum,
                             String straat, String nummer, String postcode, String gemeente, String land,
                             String email, String gsm, Rollen rol, GebruikerStatus status,
                             String wachtwoord) {
        String hashedWachtwoord = PasswordHasher.hash(wachtwoord);
        int personeelsnummer = bepaalVolgendPersoneelsnummer();
        Locatie locatie = createLocatie(straat, nummer, postcode, gemeente, land);

        Gebruiker nieuweGebruiker = Gebruiker.builder()
                .personeelsnummer(personeelsnummer)
                .naam(naam)
                .voornaam(voornaam)
                .geboortedatum(geboortedatum)
                .locatie(locatie)
                .email(email)
                .gsm(gsm)
                .rol(rol)
                .status(status)
                .wachtwoord(hashedWachtwoord)
                .build();

        gebruikerRepo.startTransaction();
        try {
            gebruikerRepo.insert(nieuweGebruiker);
            gebruikerRepo.commitTransaction();
        } catch (RuntimeException ex) {
            gebruikerRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void updateGebruiker(long id, String naam, String voornaam, LocalDate geboortedatum,
                                String straat, String nummer, String postcode, String gemeente, String land,
                                String email, String gsm, Rollen rol, GebruikerStatus status,
                                String wachtwoord) {
        gebruikerRepo.startTransaction();
        try {
            Gebruiker gebruiker = gebruikerRepo.get(id);

            if (gebruiker == null) {
                throw new IllegalArgumentException("Gebruiker niet gevonden.");
            }

            String hashedWachtwoord = (wachtwoord == null || wachtwoord.isBlank())
                    ? gebruiker.getWachtwoord()
                    : PasswordHasher.hash(wachtwoord);
            Locatie locatie = createLocatie(straat, nummer, postcode, gemeente, land);

            gebruiker.update(
                    naam, voornaam, geboortedatum, locatie, email, gsm, rol, status, hashedWachtwoord
            );
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

            if (gebruiker == null) {
                throw new IllegalArgumentException("Gebruiker niet gevonden.");
            }
            if (gebruiker.getStatus() == GebruikerStatus.INACTIEF) {
                throw new IllegalArgumentException("Gebruiker is al verwijderd.");
            }

            gebruiker.update(
                    gebruiker.getNaam(),
                    gebruiker.getVoornaam(),
                    gebruiker.getGeboortedatum(),
                    gebruiker.getLocatie(),
                    gebruiker.getEmail(),
                    gebruiker.getGsm(),
                    gebruiker.getRol(),
                    GebruikerStatus.INACTIEF,
                    gebruiker.getWachtwoord()
            );
            gebruikerRepo.commitTransaction();
        } catch (RuntimeException ex) {
            gebruikerRepo.rollbackTransaction();
            throw ex;
        }
    }

    public List<Gebruiker> getVerantwoordelijkenZonderSite() {
        return gebruikerRepo.findVerantwoordelijkenZonderSite();
    }

    private int bepaalVolgendPersoneelsnummer() {
        return gebruikerRepo.findAll().stream()
                .mapToInt(Gebruiker::getPersoneelsnummer)
                .max()
                .orElse(0) + 1;
    }

    private Locatie createLocatie(String straat, String nummer, String postcode, String gemeente, String land) {
        try {
            return Locatie.builder(straat, nummer, postcode, gemeente, land);
        } catch (ValidationException ex) {
            String message = ex.getExceptionMap().values().stream()
                    .map(IllegalArgumentException::getMessage)
                    .findFirst()
                    .orElse("Adres is ongeldig.");
            throw new IllegalArgumentException(message);
        }
    }
}
