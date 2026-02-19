package dto;

import util.GebruikerStatus;
import util.Rollen;

public record GebruikerDTO(
        long gebruikerId,
        String email,
        String gebruikersnaam,
        String wachtwoord,
        GebruikerStatus status,
        Rollen rol
) {}
