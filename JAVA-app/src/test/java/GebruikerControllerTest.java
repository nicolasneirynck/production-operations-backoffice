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

@ExtendWith(MockitoExtension.class)
public class GebruikerControllerTest {
    private Gebruiker.Builder gebruikerBuilder;

    private final static long GELDIG_ID = 1L;
    private final static String GELDIGE_EMAIL = "geldigeEmail@gmail.com";
    private final static String GELDIGE_GEBRUIKERSNAAM = "geldigeNaam";
    private final static String GELDIG_WACHTWOORD = "geldigWachtwoord123";
    private final static GebruikerStatus GELDIGE_STATUS = GebruikerStatus.ACTIEF;
    private final static Rollen GELDIGE_ROL = Rollen.WERKNEMER;

    @Mock
    private GenericDao<Gebruiker> gebruikerRepo;

    @InjectMocks
    private GebruikerController gebruikerController;

    @BeforeEach
    void setUp() {
        gebruikerBuilder = Gebruiker.builder()
                .email(GELDIGE_EMAIL)
                .gebruikersnaam(GELDIGE_GEBRUIKERSNAAM)
                .wachtwoord(GELDIG_WACHTWOORD)
                .status(GELDIGE_STATUS)
                .rol(GELDIGE_ROL);
    }

    @Test
    public void getAllGebruikers_geeftAlleGebruikersTerug() {
        Gebruiker gebruiker1 = gebruikerBuilder.email("email1@gmail.com").gebruikersnaam("naam1").build();
        Gebruiker gebruiker2 = gebruikerBuilder.email("email2@gmail.com").gebruikersnaam("naam2").build();

        when(gebruikerRepo.findAll()).thenReturn(Arrays.asList(gebruiker1, gebruiker2));

        List<GebruikerDTO> gebruikers = gebruikerController.getAllGebruikers();
        GebruikerDTO dto1 = gebruikers.get(0);

        assertEquals(2, gebruikers.size());
        assertEquals(gebruiker1.getGebruikerId(), dto1.gebruikerId());
        assertEquals(gebruiker1.getEmail(), dto1.email());
        assertEquals(gebruiker1.getGebruikersnaam(), dto1.gebruikersnaam());
        assertEquals(gebruiker1.getWachtwoord(), dto1.wachtwoord());
        assertEquals(gebruiker1.getStatus(), dto1.status());
        assertEquals(gebruiker1.getRol(), dto1.rol());
        verify(gebruikerRepo).findAll();
    }

    @Test
    public void addGebruiker_GeldigeCombinatie_maaktGebruikerAan() {
        gebruikerController.addGebruiker(
                GELDIGE_EMAIL,
                GELDIGE_GEBRUIKERSNAAM,
                GELDIG_WACHTWOORD,
                GELDIGE_STATUS,
                GELDIGE_ROL
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

    private static Stream<String> ongeldigeGebruikersnaam() {
        return Stream.of(null, "", " ", "   ");
    }

    @ParameterizedTest
    @MethodSource("ongeldigeGebruikersnaam")
    void addGebruiker_OngeldigeGebruikersnaam_GooitException(String gebruikersnaam) {
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.gebruikersnaam(gebruikersnaam).build()
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
        assertThrows(IllegalArgumentException.class, () -> gebruikerBuilder.status(GebruikerStatus.VERWIJDERD).build()
        );

        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    public void updateSite_geldigeCombinatie_pastGebruikerAan() {
        long id = 1L;

        Gebruiker gebruiker = gebruikerBuilder.build();

        when(gebruikerRepo.get(id)).thenReturn(gebruiker);

        String nieuweEmail = "nieuweEmail@gmail.com";
        String nieuweGebruikersnaam = "naam";
        String nieuwWachtwoord = "@Wachtwoord123";
        Rollen nieuweRol = Rollen.ADMINISTRATOR;

        gebruikerController.updateGebruiker(
                id,
                nieuweEmail,
                nieuweGebruikersnaam,
                nieuwWachtwoord,
                GELDIGE_STATUS,
                nieuweRol
        );

        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).get(id);
        verify(gebruikerRepo).commitTransaction();
        verify(gebruikerRepo, never()).rollbackTransaction();

        assertEquals(nieuweEmail, gebruiker.getEmail());
        assertEquals(nieuweGebruikersnaam, gebruiker.getGebruikersnaam());
        assertEquals(nieuwWachtwoord, gebruiker.getWachtwoord());
        assertEquals(GELDIGE_STATUS, gebruiker.getStatus());
        assertEquals(nieuweRol, gebruiker.getRol());
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

        assertThrows(IllegalArgumentException.class, () -> gebruikerController.updateGebruiker(GELDIG_ID, email, GELDIGE_GEBRUIKERSNAAM, GELDIG_WACHTWOORD, GELDIGE_STATUS, GELDIGE_ROL)
        );

        verifyGebruikerRepoAfterUpdate();
    }

    @ParameterizedTest
    @MethodSource("ongeldigeGebruikersnaam")
    void updateGebruiker_OngeldigeGebruikersnaam_GooitException(String gebruikersnaam) {
        Gebruiker gebruiker = gebruikerBuilder.build();

        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        assertThrows(IllegalArgumentException.class, () -> gebruikerController.updateGebruiker(GELDIG_ID, GELDIGE_EMAIL, gebruikersnaam, GELDIG_WACHTWOORD, GELDIGE_STATUS, GELDIGE_ROL)
        );

        verifyGebruikerRepoAfterUpdate();
    }

    @ParameterizedTest
    @MethodSource("ongeldigWachtwoord")
    void updateGebruiker_OngeldigWachtwoord_GooitException(String wachtwoord) {
        Gebruiker gebruiker = gebruikerBuilder.build();

        when(gebruikerRepo.get(GELDIG_ID)).thenReturn(gebruiker);

        assertThrows(IllegalArgumentException.class, () -> gebruikerController.updateGebruiker(GELDIG_ID, GELDIGE_EMAIL, GELDIGE_GEBRUIKERSNAAM, wachtwoord, GELDIGE_STATUS, GELDIGE_ROL)
        );

        verifyGebruikerRepoAfterUpdate();
    }

    @Test
    void updateGebruiker_OnbestaandeGebruiker_GooitException() {
        assertThrows(IllegalArgumentException.class, () -> gebruikerController.updateGebruiker(GELDIG_ID, GELDIGE_EMAIL, GELDIGE_GEBRUIKERSNAAM, GELDIG_WACHTWOORD, GELDIGE_STATUS, GELDIGE_ROL)
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
                GELDIGE_EMAIL,
                GELDIGE_GEBRUIKERSNAAM,
                GELDIG_WACHTWOORD,
                GebruikerStatus.VERWIJDERD,
                GELDIGE_ROL
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
