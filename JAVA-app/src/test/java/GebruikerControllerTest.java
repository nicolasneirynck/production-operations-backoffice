import domein.Gebruiker;
import domein.GebruikerController;
import dto.GebruikerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GenericDao;
import util.GebruikerStatus;
import util.Rollen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO: add more tests for personeelsnummer, naam, voornaam, geboortedatum, adres, gsm AFTER adding in proper validation in Gebruiker.java
@ExtendWith(MockitoExtension.class)
public class GebruikerControllerTest {
    private Gebruiker.Builder gebruikerBuilder;

    private final static long GELDIG_ID = 1L;
    private final static int GELDIG_PERSONEELSNUMMER = 1;
    private final static String GELDIGE_NAAM = "naam";
    private final static String GELDIGE_VOORNAAM = "voornaam";
    private final static String GELDIGE_GEBOORTEDATUM = "02/24/2000";
    private final static String GELDIG_ADRES = "Straat 5; Stad; Land";
    private final static String GELDIGE_EMAIL = "geldigeEmail@gmail.com";
    private final static String GELDIGE_GSM = "+32 2 152 45 62";
    private final static Rollen GELDIGE_ROL = Rollen.WERKNEMER;
    private final static GebruikerStatus GELDIGE_STATUS = GebruikerStatus.ACTIEF;
    private final static String GELDIG_WACHTWOORD = "geldigWachtwoord123";

    @Mock
    private GenericDao<Gebruiker> gebruikerRepo;

    @InjectMocks
    private GebruikerController gebruikerController;

    @BeforeEach
    void setUp() {
        gebruikerBuilder = Gebruiker.builder()
                .personeelsnummer(GELDIG_PERSONEELSNUMMER)
                .naam(GELDIGE_NAAM)
                .voornaam(GELDIGE_VOORNAAM)
                .geboortedatum(GELDIGE_GEBOORTEDATUM)
                .adres(GELDIG_ADRES)
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

        List<GebruikerDTO> gebruikers = gebruikerController.getAllGebruikers();
        GebruikerDTO dto1 = gebruikers.get(0);

        assertEquals(2, gebruikers.size());
        assertEquals(gebruiker1.getGebruikerId(), dto1.gebruikerId());
        assertEquals(gebruiker1.getEmail(), dto1.email());
        assertEquals(gebruiker1.getWachtwoord(), dto1.wachtwoord());
        assertEquals(gebruiker1.getStatus(), dto1.status());
        assertEquals(gebruiker1.getRol(), dto1.rol());
        verify(gebruikerRepo).findAll();
    }

    @Test
    public void addGebruiker_GeldigeCombinatie_maaktGebruikerAan() {
        gebruikerController.addGebruiker(
                GELDIG_PERSONEELSNUMMER,
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIG_ADRES,
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

    private static Stream<String> ongeldigeEmails() {
        return Stream.of(null, "", " ", "   ", "nietGeldig", "nietGeldig@test", "nietGeldig@test.a");
    }

    @ParameterizedTest
    @MethodSource("ongeldigeEmails")
    void addGebruiker_OngeldigeEmail_GooitException(String email) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.email(email).build()
        );

        verifyNoInteractions(gebruikerRepo);
    }

    private static Stream<String> ongeldigWachtwoord() {
        return Stream.of(null, "", " ", "   ");
    }

    @ParameterizedTest
    @MethodSource("ongeldigWachtwoord")
    void addGebruiker_OngeldigWachtwoord_GooitException(String wachtwoord) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.wachtwoord(wachtwoord).build()
        );

        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    void addGebruiker_OngeldigeStatus_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.status(GebruikerStatus.INACTIEF).build()
        );

        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    public void updateSite_geldigeCombinatie_pastGebruikerAan() {
        long id = 1L;

        Gebruiker gebruiker = gebruikerBuilder.build();

        when(gebruikerRepo.get(id)).thenReturn(gebruiker);

        int nieuwePersoneelsnummer = 20;
        String nieuweNaam = "test";
        String nieuweVoornaam = "voornaamTest";
        String nieuweGeboortedatum = "02/15/1995";
        String nieuwAdres = "Straat 1; nieuweStad; nieuwLand";
        String nieuweEmail = "nieuweEmail@gmail.com";
        String nieuweGsm = "+32 5 293 45 62";
        Rollen nieuweRol = Rollen.ADMINISTRATOR;
        String nieuwWachtwoord = "@Wachtwoord123";

        gebruikerController.updateGebruiker(
                id,
                nieuwePersoneelsnummer,
                nieuweNaam,
                nieuweVoornaam,
                nieuweGeboortedatum,
                nieuwAdres,
                nieuweEmail,
                nieuweGsm,
                nieuweRol,
                GELDIGE_STATUS,
                nieuwWachtwoord
        );

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(id);
        verify(gebruikerRepo).commitTransaction();
        verify(gebruikerRepo, never()).rollbackTransaction();

        assertEquals(nieuwePersoneelsnummer, gebruiker.getPersoneelsnummer());
        assertEquals(nieuweNaam, gebruiker.getNaam());
        assertEquals(nieuweVoornaam, gebruiker.getVoornaam());
        assertEquals(nieuweGeboortedatum, gebruiker.getGeboortedatum());
        assertEquals(nieuwAdres, gebruiker.getAdres());
        assertEquals(nieuweEmail, gebruiker.getEmail());
        assertEquals(nieuweGsm, gebruiker.getGsm());
        assertEquals(nieuweRol, gebruiker.getRol());
        assertEquals(GELDIGE_STATUS, gebruiker.getStatus());
        assertEquals(nieuwWachtwoord, gebruiker.getWachtwoord());
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

        assertThrows(IllegalArgumentException.class, () -> gebruikerController.updateGebruiker(
                        GELDIG_ID,
                        GELDIG_PERSONEELSNUMMER,
                        GELDIGE_NAAM,
                        GELDIGE_VOORNAAM,
                        GELDIGE_GEBOORTEDATUM,
                        GELDIG_ADRES,
                        email,
                        GELDIGE_GSM,
                        GELDIGE_ROL,
                        GELDIGE_STATUS,
                        GELDIG_WACHTWOORD
                )
        );



        verifyGebruikerRepoAfterUpdate();
    }

    @ParameterizedTest
    @MethodSource("ongeldigWachtwoord")
    void updateGebruiker_OngeldigWachtwoord_GooitException(String wachtwoord) {
        Gebruiker gebruiker = gebruikerBuilder.build();

        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        assertThrows(IllegalArgumentException.class, () -> gebruikerController.updateGebruiker(
                        GELDIG_ID,
                        GELDIG_PERSONEELSNUMMER,
                        GELDIGE_NAAM,
                        GELDIGE_VOORNAAM,
                        GELDIGE_GEBOORTEDATUM,
                        GELDIG_ADRES,
                        GELDIGE_EMAIL,
                        GELDIGE_GSM,
                        GELDIGE_ROL,
                        GELDIGE_STATUS,
                        wachtwoord
                )
        );



        verifyGebruikerRepoAfterUpdate();
    }

    @Test
    void updateGebruiker_OnbestaandeGebruiker_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerController.updateGebruiker(
                        GELDIG_ID,
                        GELDIG_PERSONEELSNUMMER,
                        GELDIGE_NAAM,
                        GELDIGE_VOORNAAM,
                        GELDIGE_GEBOORTEDATUM,
                        GELDIG_ADRES,
                        GELDIGE_EMAIL,
                        GELDIGE_GSM,
                        GELDIGE_ROL,
                        GELDIGE_STATUS,
                        GELDIG_WACHTWOORD
                )
        );



        verifyGebruikerRepoAfterUpdate();
    }

    @Test
    void deleteGebruiker_GebruikerIsAlVerwijderd_GooitException() {
        Gebruiker gebruiker = gebruikerBuilder.build();

        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        // Status mag niet beginnen als verwijderd dus we moeten updaten
        gebruikerController.updateGebruiker(
                GELDIG_ID,
                GELDIG_PERSONEELSNUMMER,
                GELDIGE_NAAM,
                GELDIGE_VOORNAAM,
                GELDIGE_GEBOORTEDATUM,
                GELDIG_ADRES,
                GELDIGE_EMAIL,
                GELDIGE_GSM,
                GELDIGE_ROL,
                GebruikerStatus.INACTIEF,
                GELDIG_WACHTWOORD
        );

        verify(gebruikerRepo).commitTransaction();

        assertThrows(IllegalArgumentException.class, () -> gebruikerController.deleteGebruiker(GELDIG_ID)
        );

        verify(gebruikerRepo, times(2)).startTransaction();
        verify(gebruikerRepo, times(2)).get(GELDIG_ID);
        verify(gebruikerRepo).rollbackTransaction();
    }

    @Test
    void deleteGebruiker_OnbestaandeGebruiker_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerController.deleteGebruiker(GELDIG_ID)
        );

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(GELDIG_ID);
        verify(gebruikerRepo).rollbackTransaction();
        verify(gebruikerRepo, never()).commitTransaction();
    }

    @Test
    void deleteGebruiker_BestaandeGebruiker_CommitSoftDelete() {
        Gebruiker gebruiker = gebruikerBuilder.build();

        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        gebruikerController.deleteGebruiker(GELDIG_ID);

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(GELDIG_ID);
        verify(gebruikerRepo).commitTransaction();
        verify(gebruikerRepo, never()).rollbackTransaction();
    }
}
