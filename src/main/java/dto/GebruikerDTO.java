package dto;

import util.GebruikerStatus;
import util.Rollen;

import java.time.LocalDate;

public record GebruikerDTO(
        long gebruikerId,
        int personeelsnummer,
        String naam,
        String voornaam,
        LocalDate geboortedatum,
        LocatieDTO locatie,
        String email,
        String gsm,
        Rollen rol,
        GebruikerStatus status
) {

    public String volledigeNaam() {
        return voornaam + " " + naam;

    }

}
