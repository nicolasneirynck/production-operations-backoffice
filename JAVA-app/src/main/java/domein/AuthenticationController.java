package domein;

import domein.entiteiten.Gebruiker;
import dto.GebruikerDTO;
import exception.LoginException;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthenticationController {
    private final GebruikerDao gebruikerRepo;

    private static String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }

    public AuthenticationController() {
        this(new GebruikerDaoJpa());
    }

    //TODO tijdelijk voor devFase -> Mockito
    public AuthenticationController(GebruikerDao gebruikerRepo) {
        this.gebruikerRepo = gebruikerRepo;
    }

    // TODO: use encryption for password
    public GebruikerDTO login(String email, String wachtwoord) throws LoginException {
        String normalizedEmail = normalizeEmail(email);

        Map<String, IllegalArgumentException> errors = new HashMap<>();

        if (normalizedEmail.isBlank()) {
//            errors.put("email", new IllegalArgumentException("Email mag niet leeg zijn en moet een geldig formaat hebben. Formaat: x@xxx.xx, waarbij x 1 of meer karakters voorstelt."));
            errors.put("email", new IllegalArgumentException("Email is vereist."));
        }

        if (wachtwoord == null || wachtwoord.isBlank()) {
            errors.put("wachtwoord", new IllegalArgumentException("Wachtwoord is vereist."));
//            errors.put("wachtwoord", new IllegalArgumentException("Wachtwoord mag niet leeg zijn of uit enkel spaties bestaan."));
        }

        Optional<Gebruiker> gebruikerOptional = gebruikerRepo.findByEmail(normalizedEmail);

        if (gebruikerOptional.isEmpty()) {
            errors.put("onbestaand", new IllegalArgumentException("Ongeldige login."));
//            errors.put("onbestaand", new IllegalArgumentException("Een gebruiker met deze email en wachtwoord bestaat niet."));
            throw new LoginException(errors);
        }

        Gebruiker gebruiker = gebruikerOptional.get();
        if (!wachtwoord.equals(gebruiker.getWachtwoord())) {
//            errors.put("onbestaand", new IllegalArgumentException("Een gebruiker met deze email en wachtwoord bestaat niet."));
            errors.put("onbestaand", new IllegalArgumentException("Ongeldige login."));
        }

        if (!errors.isEmpty())
            throw new LoginException(errors);

        return new GebruikerDTO(
                gebruiker.getGebruikerId(),
                gebruiker.getPersoneelsnummer(),
                gebruiker.getNaam(),
                gebruiker.getVoornaam(),
                gebruiker.getGeboortedatum(),
                gebruiker.getAdres(),
                gebruiker.getEmail(),
                gebruiker.getGsm(),
                gebruiker.getRol(),
                gebruiker.getStatus(),
                null
        );
    }
}
