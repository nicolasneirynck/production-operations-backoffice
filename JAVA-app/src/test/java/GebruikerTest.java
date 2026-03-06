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

public class GebruikerTest {
    private Gebruiker.Builder gebruikerBuilder;

    @BeforeEach
    void setUp() {
        gebruikerBuilder = Gebruiker.builder()
                .email("geldigeEmail@gmail.com")
                .gebruikersnaam("geldigeNaam")
                .wachtwoord("geldigWachtwoord123")
                .status(GebruikerStatus.ACTIEF)
                .rol(Rollen.WERKNEMER);
    }

    @ParameterizedTest
    @CsvSource({"lars@gmail.com, Lars, mijnWachtwoord", "eenEmail@proton.me, Pérsöon, w@132_"})
    void builder_GeldigeCombinaties_MaaktNieuweGebruikerMetCorrecteInfo(String email, String gebruikersnaam, String wachtwoord) {
        Gebruiker gebruiker = gebruikerBuilder.email(email).gebruikersnaam(gebruikersnaam).wachtwoord(wachtwoord).build();

        assertEquals(email, gebruiker.getEmail());
        assertEquals(gebruikersnaam, gebruiker.getGebruikersnaam());
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
    void builder_OngeldigeGebruikersnaam_GooitException(String gebruikersnaam) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.gebruikersnaam(gebruikersnaam).build()
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
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.status(GebruikerStatus.VERWIJDERD).build()
        );
    }
}
