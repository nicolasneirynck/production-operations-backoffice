import domein.Gebruiker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import util.GebruikerStatus;
import util.Rollen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO: more tests
public class GebruikerTest {
    private Gebruiker.Builder gebruikerBuilder;

    @BeforeEach
    void setUp() {
        gebruikerBuilder = Gebruiker.builder()
                .personeelsnummer(1)
                .naam("eenNaam")
                .voornaam("eenVoornaam")
                .geboortedatum("02/24/2000")
                .adres("Straat 5; Stad; Land")
                .email("geldigeEmail@gmail.com")
                .gsm("+32 2 152 45 62")
                .rol(Rollen.WERKNEMER)
                .status(GebruikerStatus.ACTIEF)
                .wachtwoord("geldigWachtwoord123");
    }

    @ParameterizedTest
    @CsvSource({"5, Achternaam, Lars, 08/02/1980, EenStraat 16; Gent; België, lars@gmail.com, +32 5 113 45 62, mijnWachtwoord", "512390581, Langelangelangelangelangeachternaam, Pérsöon, 12/12/1950, LangeLangeLangeStraat 16; Brussel; België, eenEmail@proton.me, +50 5 144 45 62, w@132_"})
    void builder_GeldigeCombinaties_MaaktNieuweGebruikerMetCorrecteInfo(int personeelsnummer, String naam, String voornaam, String geboortedatum, String adres, String email, String gsm, String wachtwoord) {
        Gebruiker gebruiker = gebruikerBuilder.personeelsnummer(personeelsnummer).naam(naam).voornaam(voornaam).geboortedatum(geboortedatum).adres(adres).email(email).gsm(gsm).wachtwoord(wachtwoord).build();

        assertEquals(personeelsnummer, gebruiker.getPersoneelsnummer());
        assertEquals(naam, gebruiker.getNaam());
        assertEquals(voornaam, gebruiker.getVoornaam());
        assertEquals(geboortedatum, gebruiker.getGeboortedatum());
        assertEquals(adres, gebruiker.getAdres());
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
