package dto;

import util.GebruikerStatus;
import util.Rollen;

public record GebruikerDTO(
        long gebruikerId,
        int personeelsnummer,
        String naam,
        String voornaam,
        String geboortedatum,
        String adres,
        String email,
        String gsm,
        Rollen rol,
        GebruikerStatus status,
        String wachtwoord
) {

    public String volledigeNaam() {
        return voornaam + " " + naam;

    }

}
