import domein.beheerders.GebruikerBeheerder;
import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GebruikerDao;
import security.PasswordHasher;
import util.GebruikerStatus;
import util.Rollen;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GebruikerBeheerderTest {
    private Gebruiker.Builder gebruikerBuilder;

    private static final long GELDIG_ID = 1L;
    private static final String GELDIGE_NAAM = "naam";
    private static final String GELDIGE_VOORNAAM = "voornaam";
    private static final LocalDate GELDIGE_GEBOORTEDATUM = LocalDate.of(2000, 2, 24);
    private static final String GELDIGE_STRAAT = "Straat";
    private static final String GELDIG_NUMMER = "5";
    private static final String GELDIGE_POSTCODE = "9000";
    private static final String GELDIGE_GEMEENTE = "Stad";
    private static final String GELDIG_LAND = "Land";
    private static final String GELDIGE_EMAIL = "geldigeEmail@gmail.com";
    private static final String GELDIGE_GSM = "+32 2 152 45 62";
    private static final Rollen GELDIGE_ROL = Rollen.WERKNEMER;
    private static final GebruikerStatus GELDIGE_STATUS = GebruikerStatus.ACTIEF;
    private static final String GELDIG_WACHTWOORD = "geldigWachtwoord123";

    @Mock
    private GebruikerDao gebruikerRepo;

    @InjectMocks
    private GebruikerBeheerder gebruikerBeheerder;

    @BeforeEach
    void setUp() throws Exception {
        gebruikerBuilder = Gebruiker.builder()
                .personeelsnummer(1)
                .naam(GELDIGE_NAAM)
                .voornaam(GELDIGE_VOORNAAM)
                .geboortedatum(GELDIGE_GEBOORTEDATUM)
                .locatie(Locatie.builder(GELDIGE_STRAAT, GELDIG_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIG_LAND))
                .email(GELDIGE_EMAIL)
                .gsm(GELDIGE_GSM)
                .rol(GELDIGE_ROL)
                .status(GELDIGE_STATUS)
                .wachtwoord(GELDIG_WACHTWOORD);
    }

    @Test
    public void getAllGebruikers_geeftAlleGebruikersTerug() {
        Gebruiker gebruiker1 = gebruikerBuilder.email("email1@gmail.com").personeelsnummer(2).build();
        Gebruiker gebruiker2 = gebruikerBuilder.email("email2@gmail.com").personeelsnummer(3).build();

        when(gebruikerRepo.findAll()).thenReturn(Arrays.asList(gebruiker1, gebruiker2));

        List<Gebruiker> gebruikers = gebruikerBeheerder.getAllGebruikers();

        assertEquals(2, gebruikers.size());
        assertEquals("email1@gmail.com", gebruikers.get(0).getEmail());
        verify(gebruikerRepo).findAll();
    }

    @Test
    public void addGebruiker_GeldigeCombinatie_maaktGebruikerAan() {
        when(gebruikerRepo.findAll()).thenReturn(List.of());

        gebruikerBeheerder.addGebruiker(
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIGE_STRAAT,
                GELDIG_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIG_LAND,
                GELDIGE_EMAIL,
                GELDIGE_GSM,
                GELDIGE_ROL,
                GELDIGE_STATUS,
                GELDIG_WACHTWOORD
        );

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).insert(any(Gebruiker.class));
        verify(gebruikerRepo).commitTransaction();
        verify(gebruikerRepo, never()).rollbackTransaction();
    }

    @Test
    void addGebruiker_hashtWachtwoordVoorOpslag() {
        when(gebruikerRepo.findAll()).thenReturn(List.of());

        gebruikerBeheerder.addGebruiker(
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIGE_STRAAT,
                GELDIG_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIG_LAND,
                GELDIGE_EMAIL,
                GELDIGE_GSM,
                GELDIGE_ROL,
                GELDIGE_STATUS,
                GELDIG_WACHTWOORD
        );

        verify(gebruikerRepo).insert(argThat(gebruiker ->
                gebruiker.getPersoneelsnummer() == 1
                        && !GELDIG_WACHTWOORD.equals(gebruiker.getWachtwoord())
                        && PasswordHasher.matches(GELDIG_WACHTWOORD, gebruiker.getWachtwoord())
        ));
    }

    private static Stream<String> ongeldigeEmails() {
        return Stream.of(null, "", " ", "   ", "nietGeldig", "nietGeldig@test", "nietGeldig@test.a");
    }

    @ParameterizedTest
    @MethodSource("ongeldigeEmails")
    void addGebruiker_OngeldigeEmail_GooitException(String email) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.email(email).build());
        verifyNoInteractions(gebruikerRepo);
    }

    private static Stream<String> ongeldigWachtwoord() {
        return Stream.of(null, "", " ", "   ");
    }

    @ParameterizedTest
    @MethodSource("ongeldigWachtwoord")
    void addGebruiker_OngeldigWachtwoord_GooitException(String wachtwoord) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.wachtwoord(wachtwoord).build());
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    void addGebruiker_OngeldigeStatus_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.status(GebruikerStatus.INACTIEF).build());
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    public void updateGebruiker_geldigeCombinatie_pastGebruikerAan() {
        Gebruiker gebruiker = gebruikerBuilder.build();
        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        LocalDate nieuweGeboortedatum = LocalDate.of(1995, 2, 15);
        String nieuweEmail = "nieuweEmail@gmail.com";
        String nieuweGsm = "+32 5 293 45 62";
        Rollen nieuweRol = Rollen.ADMINISTRATOR;
        String nieuwWachtwoord = "@Wachtwoord123";

        gebruikerBeheerder.updateGebruiker(
                GELDIG_ID,
                "test",
                "voornaamTest",
                nieuweGeboortedatum,
                "Straat",
                "1",
                "1000",
                "nieuweStad",
                "nieuwLand",
                nieuweEmail,
                nieuweGsm,
                nieuweRol,
                GELDIGE_STATUS,
                nieuwWachtwoord
        );

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(GELDIG_ID);
        verify(gebruikerRepo).commitTransaction();
        verify(gebruikerRepo, never()).rollbackTransaction();

        assertEquals("test", gebruiker.getNaam());
        assertEquals("voornaamTest", gebruiker.getVoornaam());
        assertEquals(nieuweGeboortedatum, gebruiker.getGeboortedatum());
        assertEquals("Straat 1, 1000 nieuweStad, nieuwLand", gebruiker.getLocatie().toString());
        assertEquals(nieuweEmail, gebruiker.getEmail());
        assertEquals(nieuweGsm, gebruiker.getGsm());
        assertEquals(nieuweRol, gebruiker.getRol());
        assertEquals(GELDIGE_STATUS, gebruiker.getStatus());
        assertTrue(PasswordHasher.matches(nieuwWachtwoord, gebruiker.getWachtwoord()));
    }

    @Test
    void updateGebruiker_leegWachtwoord_behouwtBestaandeHash() {
        String bestaandeHash = PasswordHasher.hash(GELDIG_WACHTWOORD);
        Gebruiker gebruiker = gebruikerBuilder.wachtwoord(bestaandeHash).build();
        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        gebruikerBeheerder.updateGebruiker(
                GELDIG_ID,
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIGE_STRAAT,
                GELDIG_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIG_LAND,
                GELDIGE_EMAIL,
                GELDIGE_GSM,
                GELDIGE_ROL,
                GELDIGE_STATUS,
                ""
        );

        assertEquals(bestaandeHash, gebruiker.getWachtwoord());
    }

    private void verifyGebruikerRepoAfterUpdate() {
        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(GELDIG_ID);
        verify(gebruikerRepo).rollbackTransaction();
        verify(gebruikerRepo, never()).commitTransaction();
    }

    @ParameterizedTest
    @MethodSource("ongeldigeEmails")
    void updateGebruiker_OngeldigeEmail_GooitException(String email) {
        Gebruiker gebruiker = gebruikerBuilder.build();
        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        assertThrows(IllegalArgumentException.class, () -> gebruikerBeheerder.updateGebruiker(
                GELDIG_ID,
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIGE_STRAAT,
                GELDIG_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIG_LAND,
                email,
                GELDIGE_GSM,
                GELDIGE_ROL,
                GELDIGE_STATUS,
                GELDIG_WACHTWOORD
        ));

        verifyGebruikerRepoAfterUpdate();
    }

    @Test
    void updateGebruiker_OnbestaandeGebruiker_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBeheerder.updateGebruiker(
                GELDIG_ID,
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIGE_STRAAT,
                GELDIG_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIG_LAND,
                GELDIGE_EMAIL,
                GELDIGE_GSM,
                GELDIGE_ROL,
                GELDIGE_STATUS,
                GELDIG_WACHTWOORD
        ));

        verifyGebruikerRepoAfterUpdate();
    }

    @Test
    void deleteGebruiker_GebruikerIsAlVerwijderd_GooitException() {
        Gebruiker gebruiker = gebruikerBuilder.build();
        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        gebruikerBeheerder.updateGebruiker(
                GELDIG_ID,
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIGE_STRAAT,
                GELDIG_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIG_LAND,
                GELDIGE_EMAIL,
                GELDIGE_GSM,
                GELDIGE_ROL,
                GebruikerStatus.INACTIEF,
                GELDIG_WACHTWOORD
        );

        verify(gebruikerRepo).commitTransaction();

        assertThrows(IllegalArgumentException.class, () -> gebruikerBeheerder.deleteGebruiker(GELDIG_ID));

        verify(gebruikerRepo, times(2)).startTransaction();
        verify(gebruikerRepo, times(2)).get(GELDIG_ID);
        verify(gebruikerRepo).rollbackTransaction();
    }

    @Test
    void deleteGebruiker_OnbestaandeGebruiker_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBeheerder.deleteGebruiker(GELDIG_ID));

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(GELDIG_ID);
        verify(gebruikerRepo).rollbackTransaction();
        verify(gebruikerRepo, never()).commitTransaction();
    }

    @Test
    void deleteGebruiker_BestaandeGebruiker_CommitSoftDelete() {
        Gebruiker gebruiker = gebruikerBuilder.build();
        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        gebruikerBeheerder.deleteGebruiker(GELDIG_ID);

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(GELDIG_ID);
        verify(gebruikerRepo).commitTransaction();
        verify(gebruikerRepo, never()).rollbackTransaction();
    }

    @Test
    void getVerantwoordelijkenZonderSite_geeftResultaatVanRepositoryTerug() {
        Gebruiker verantwoordelijke = gebruikerBuilder.rol(Rollen.VERANTWOORDELIJKE).build();
        when(gebruikerRepo.findVerantwoordelijkenZonderSite()).thenReturn(List.of(verantwoordelijke));

        List<Gebruiker> resultaat = gebruikerBeheerder.getVerantwoordelijkenZonderSite();

        assertEquals(1, resultaat.size());
        assertEquals(Rollen.VERANTWOORDELIJKE, resultaat.getFirst().getRol());
        verify(gebruikerRepo).findVerantwoordelijkenZonderSite();
    }
}
