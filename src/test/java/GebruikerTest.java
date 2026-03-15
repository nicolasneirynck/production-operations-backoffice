import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import util.GebruikerStatus;
import util.Rollen;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO: more tests
public class GebruikerTest {
    private Gebruiker.Builder gebruikerBuilder;

    @BeforeEach
    void setUp() throws Exception {
        gebruikerBuilder = Gebruiker.builder()
                .personeelsnummer(1)
                .naam("eenNaam")
                .voornaam("eenVoornaam")
                .geboortedatum(LocalDate.of(2000, 2, 24))
                .locatie(Locatie.builder("Straat", "5", "9000", "Stad", "Land"))
                .email("geldigeEmail@gmail.com")
                .gsm("+32 2 152 45 62")
                .rol(Rollen.WERKNEMER)
                .status(GebruikerStatus.ACTIEF)
                .wachtwoord("geldigWachtwoord123");
    }

    @ParameterizedTest
    @CsvSource({"5, Achternaam, Lars, 1980, 8, 2, EenStraat, 16, 9000, Gent, België, lars@gmail.com, +32 5 113 45 62, mijnWachtwoord", "512390581, Langelangelangelangelangeachternaam, Pérsöon, 1950, 12, 12, LangeLangeLangeStraat, 16, 1000, Brussel, België, eenEmail@proton.me, +50 5 144 45 62, w@132_"})
    void builder_GeldigeCombinaties_MaaktNieuweGebruikerMetCorrecteInfo(int personeelsnummer, String naam, String voornaam,
                                                                        int year, int month, int day, String straat,
                                                                        String nummer, String postcode, String gemeente,
                                                                        String land, String email, String gsm,
                                                                        String wachtwoord) throws Exception {
        LocalDate geboortedatum = LocalDate.of(year, month, day);
        Locatie locatie = Locatie.builder(straat, nummer, postcode, gemeente, land);
        Gebruiker gebruiker = gebruikerBuilder.personeelsnummer(personeelsnummer).naam(naam).voornaam(voornaam)
                .geboortedatum(geboortedatum).locatie(locatie).email(email).gsm(gsm).wachtwoord(wachtwoord).build();

        assertEquals(personeelsnummer, gebruiker.getPersoneelsnummer());
        assertEquals(naam, gebruiker.getNaam());
        assertEquals(voornaam, gebruiker.getVoornaam());
        assertEquals(geboortedatum, gebruiker.getGeboortedatum());
        assertEquals(locatie, gebruiker.getLocatie());
        assertEquals(email, gebruiker.getEmail());
        assertEquals(gsm, gebruiker.getGsm());
        assertEquals(wachtwoord, gebruiker.getWachtwoord());
    }

    @ParameterizedTest
    @EnumSource(Rollen.class)
    void builder_GeldigeRollen_MaaktNieuweGebruikerMetCorrecteRol(Rollen rol) {
        Gebruiker gebruiker = gebruikerBuilder.rol(rol).build();

        assertEquals(rol, gebruiker.getRol());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "nietGeldig", "nietGeldig@test", "nietGeldig@test.a"})
    void builder_OngeldigeEmail_GooitException(String email) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.email(email).build()
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigWachtwoord_GooitException(String wachtwoord) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.wachtwoord(wachtwoord).build()
        );
    }

    @Test
    void builder_OngeldigeStatus_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.status(GebruikerStatus.INACTIEF).build()
        );
    }
}
