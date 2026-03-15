package domein.services;

import domein.entiteiten.Gebruiker;
import dto.AuthenticatedUserDTO;
import dto.DTOMapper;
import exception.ValidationException;
import repository.GebruikerDao;
import repository.GebruikerDaoJpa;
import security.PasswordHasher;
import util.GebruikerStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginService {
    private final GebruikerDao gebruikerRepo;

    private static String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }

    public LoginService() {
        this(new GebruikerDaoJpa());
    }

    //TODO tijdelijk voor devFase -> Mockito
    public LoginService(GebruikerDao gebruikerRepo) {
        this.gebruikerRepo = gebruikerRepo;
    }

    public AuthenticatedUserDTO login(String email, String wachtwoord) throws ValidationException {
        String normalizedEmail = normalizeEmail(email);

        Map<String, IllegalArgumentException> errors = new HashMap<>();
        String invalidLoginMessage = "Ongeldige login.";

        if (normalizedEmail.isBlank()) {
            errors.put("onbestaand", new IllegalArgumentException(invalidLoginMessage));
        }

        if (wachtwoord == null || wachtwoord.isBlank()) {
            errors.put("onbestaand", new IllegalArgumentException(invalidLoginMessage));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Optional<Gebruiker> gebruikerOptional = gebruikerRepo.findByEmail(normalizedEmail);

        if (gebruikerOptional.isEmpty()) {
            errors.put("onbestaand", new IllegalArgumentException(invalidLoginMessage));
            throw new ValidationException(errors);
        }

        Gebruiker gebruiker = gebruikerOptional.get();
        if (!PasswordHasher.matches(wachtwoord, gebruiker.getWachtwoord())) {
            errors.put("onbestaand", new IllegalArgumentException(invalidLoginMessage));
        }

        if (gebruiker.getStatus() == GebruikerStatus.INACTIEF) {
            errors.put("onbestaand", new IllegalArgumentException(invalidLoginMessage));
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);

        return DTOMapper.toAuthenticatedUserDTO(gebruiker);
    }
}
