package domein;

import dto.GebruikerDTO;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;
import repository.GenericDao;

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
    public Optional<GebruikerDTO> login(String email, String wachtwoord) {
        String normalizedEmail = normalizeEmail(email);

        if (normalizedEmail.isBlank() || wachtwoord == null || wachtwoord.isBlank()) {
            return Optional.empty();
        }

        Optional<Gebruiker> gebruikerOptional = gebruikerRepo.findByEmail(normalizedEmail);

        if (gebruikerOptional.isEmpty()) {
            return Optional.empty();
        }

        Gebruiker gebruiker = gebruikerOptional.get();
        if (!wachtwoord.equals(gebruiker.getWachtwoord())) {
            return Optional.empty();
        }

        // TODO: dont return password/status/something else?
        return Optional.of(new GebruikerDTO(
                gebruiker.getGebruikerId(),
                gebruiker.getEmail(),
                gebruiker.getGebruikersnaam(),
                gebruiker.getWachtwoord(),
                gebruiker.getStatus(),
                gebruiker.getRol()
        ));
    }
}
