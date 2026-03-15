import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import domein.beheerders.SiteBeheerder;
import domein.entiteiten.Gebruiker;
import domein.entiteiten.Locatie;
import domein.entiteiten.Site;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GebruikerDao;
import repository.SiteDao;
import util.GebruikerStatus;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.time.LocalDate;
@ExtendWith(MockitoExtension.class)
class SiteBeheerderTest {

    private final String GELDIGE_NAAM = "SITE-A";

    private final String GELDIGE_STRAAT = "Kortrijksesteenweg";
    private final String GELDIGE_NUMMER = "80";
    private final String GELDIGE_POSTCODE = "9000";
    private final String GELDIGE_GEMEENTE = "Gent";
    private final String GELDIGE_LAND = "België";

    private final Long GELDIGE_VERANTWOORDELIJKE_ID = 10L;

    @Mock
    private SiteDao siteRepo;

    @Mock
    private GebruikerDao gebruikerRepo;

    @InjectMocks
    private SiteBeheerder siteBeheerder;

    @Test
    void getAllSites_geeftAlleSites() throws Exception {
        Site eenSite = Site.builder()
                .naam(GELDIGE_NAAM)
                .locatie(Locatie.builder(
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND
                ))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.findAll()).thenReturn(Arrays.asList(eenSite));

        List<Site> sites = siteBeheerder.getAllSites();

        assertEquals(1, sites.size());
        assertEquals(GELDIGE_NAAM, sites.getFirst().getNaam());
        verify(siteRepo).findAll();
    }

    @Test
    void getSitesZonderVerantwoordelijke_geeftCorrecteSitesTerug() throws Exception {
        Site site = Site.builder()
                .naam("SITE-ZONDER-V")
                .locatie(Locatie.builder(
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND
                ))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.findSitesZonderVerantwoordelijke()).thenReturn(List.of(site));

        List<Site> result = siteBeheerder.getSitesZonderVerantwoordelijke();

        assertEquals(1, result.size());
        assertEquals("SITE-ZONDER-V", result.getFirst().getNaam());
        verify(siteRepo).findSitesZonderVerantwoordelijke();
    }

    @Test
    void addSite_geldigeParameters_zonderVerantwoordelijke_voegtSiteToe() throws Exception {
        when(siteRepo.existsByName(GELDIGE_NAAM, null)).thenReturn(false);

        siteBeheerder.addSite(
                GELDIGE_NAAM,
                null,
                GELDIGE_STRAAT,
                GELDIGE_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIGE_LAND,
                100,
                OperationeleStatus.ACTIEF,
                ProductieStatus.GEZOND
        );

        verify(siteRepo).existsByName(GELDIGE_NAAM, null);
        verify(siteRepo).startTransaction();
        verify(siteRepo).insert(any(Site.class));
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    void addSite_geldigeParameters_metVerantwoordelijke_voegtSiteToe() throws Exception {
        Gebruiker verantwoordelijke = mock(Gebruiker.class);
        when(verantwoordelijke.getRol()).thenReturn(Rollen.VERANTWOORDELIJKE);

        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(verantwoordelijke);
        when(siteRepo.existsByName(GELDIGE_NAAM, null)).thenReturn(false);

        siteBeheerder.addSite(
                GELDIGE_NAAM,
                GELDIGE_VERANTWOORDELIJKE_ID,
                GELDIGE_STRAAT,
                GELDIGE_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIGE_LAND,
                100,
                OperationeleStatus.ACTIEF,
                ProductieStatus.GEZOND
        );

        verify(gebruikerRepo).get(GELDIGE_VERANTWOORDELIJKE_ID);
        verify(siteRepo).existsByName(GELDIGE_NAAM, null);
        verify(siteRepo).startTransaction();
        verify(siteRepo).insert(any(Site.class));
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
    }

    @Test
    void addSite_bestaandeNaam_gooitException_enRaaktTransactieNiet() {
        when(siteRepo.existsByName(GELDIGE_NAAM, null)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                siteBeheerder.addSite(
                        GELDIGE_NAAM,
                        null,
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND,
                        100,
                        OperationeleStatus.ACTIEF,
                        ProductieStatus.GEZOND
                )
        );

        verify(siteRepo).existsByName(GELDIGE_NAAM, null);
        verify(siteRepo, never()).startTransaction();
        verify(siteRepo, never()).insert(any());
        verify(siteRepo, never()).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
    }

    @Test
    void addSite_onbestaandeVerantwoordelijke_gooitValidationException() {
        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(null);

        assertThrows(ValidationException.class, () ->
                siteBeheerder.addSite(
                        GELDIGE_NAAM,
                        GELDIGE_VERANTWOORDELIJKE_ID,
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND,
                        100,
                        OperationeleStatus.ACTIEF,
                        ProductieStatus.GEZOND
                )
        );

        verify(gebruikerRepo).get(GELDIGE_VERANTWOORDELIJKE_ID);
        verify(siteRepo, never()).existsByName(anyString(), any());
        verify(siteRepo, never()).startTransaction();
    }

    @Test
    void addSite_verantwoordelijkeMetVerkeerdeRol_gooitValidationException() {
        Gebruiker gebruiker = mock(Gebruiker.class);
        when(gebruiker.getRol()).thenReturn(Rollen.ADMINISTRATOR);
        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(gebruiker);

        assertThrows(ValidationException.class, () ->
                siteBeheerder.addSite(
                        GELDIGE_NAAM,
                        GELDIGE_VERANTWOORDELIJKE_ID,
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND,
                        100,
                        OperationeleStatus.ACTIEF,
                        ProductieStatus.GEZOND
                )
        );

        verify(gebruikerRepo).get(GELDIGE_VERANTWOORDELIJKE_ID);
        verify(siteRepo, never()).existsByName(anyString(), any());
        verify(siteRepo, never()).startTransaction();
    }

    private static Stream<Arguments> ongeldigeParameters() {
        return Stream.of(
                Arguments.of("", "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of(null, "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", null, "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "Straat", "", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", null, "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "Straat", "1", "", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", "1", null, "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "Straat", "1", "9000", "", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", "1", "9000", null, "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", null, 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", -10, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 0, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.NON_ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 100, null, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, null)
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    void addSite_ongeldigeParameters_gooitException_enRaaktRepoNiet(
            String naam, String straat, String nummer, String postcode,
            String gemeente, String land, int capaciteit,
            OperationeleStatus op, ProductieStatus prod) {

        assertThrows(ValidationException.class, () ->
                siteBeheerder.addSite(
                        naam,
                        null,
                        straat,
                        nummer,
                        postcode,
                        gemeente,
                        land,
                        capaciteit,
                        op,
                        prod
                )
        );

        verifyNoInteractions(siteRepo);
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    void updateSite_geldigeParameters_zonderVerantwoordelijke_pastSiteAan() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie(Locatie.builder("Oudstraat", "1", "1000", "Brussel", "België"))
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);
        when(siteRepo.existsByName(GELDIGE_NAAM, id)).thenReturn(false);

        siteBeheerder.updateSite(
                id,
                GELDIGE_NAAM,
                null,
                GELDIGE_STRAAT,
                GELDIGE_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIGE_LAND,
                100,
                OperationeleStatus.ACTIEF,
                ProductieStatus.GEZOND
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).existsByName(GELDIGE_NAAM, id);
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
        verifyNoInteractions(gebruikerRepo);

        assertEquals(GELDIGE_NAAM, bestaande.getNaam());
        assertEquals(GELDIGE_STRAAT, bestaande.getLocatie().getStraat());
        assertEquals(GELDIGE_NUMMER, bestaande.getLocatie().getNummer());
        assertEquals(GELDIGE_POSTCODE, bestaande.getLocatie().getPostcode());
        assertEquals(GELDIGE_GEMEENTE, bestaande.getLocatie().getGemeente());
        assertEquals(GELDIGE_LAND, bestaande.getLocatie().getLand());
        assertEquals(100, bestaande.getCapaciteit());
        assertEquals(OperationeleStatus.ACTIEF, bestaande.getOperationeleStatus());
        assertEquals(ProductieStatus.GEZOND, bestaande.getProductieStatus());
    }

    @Test
    void updateSite_geldigeParameters_metVerantwoordelijke_pastSiteAan() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie(Locatie.builder("Oudstraat", "1", "1000", "Brussel", "België"))
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        Gebruiker verantwoordelijke = mock(Gebruiker.class);
        when(verantwoordelijke.getRol()).thenReturn(Rollen.VERANTWOORDELIJKE);

        when(siteRepo.get(id)).thenReturn(bestaande);
        when(siteRepo.existsByName(GELDIGE_NAAM, id)).thenReturn(false);
        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(verantwoordelijke);

        siteBeheerder.updateSite(
                id,
                GELDIGE_NAAM,
                GELDIGE_VERANTWOORDELIJKE_ID,
                GELDIGE_STRAAT,
                GELDIGE_NUMMER,
                GELDIGE_POSTCODE,
                GELDIGE_GEMEENTE,
                GELDIGE_LAND,
                100,
                OperationeleStatus.ACTIEF,
                ProductieStatus.GEZOND
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).existsByName(GELDIGE_NAAM, id);
        verify(gebruikerRepo).get(GELDIGE_VERANTWOORDELIJKE_ID);
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();

        assertEquals(GELDIGE_NAAM, bestaande.getNaam());
        assertEquals(verantwoordelijke, bestaande.getVerantwoordelijke());
        assertEquals(GELDIGE_STRAAT, bestaande.getLocatie().getStraat());
        assertEquals(GELDIGE_NUMMER, bestaande.getLocatie().getNummer());
        assertEquals(GELDIGE_POSTCODE, bestaande.getLocatie().getPostcode());
        assertEquals(GELDIGE_GEMEENTE, bestaande.getLocatie().getGemeente());
        assertEquals(GELDIGE_LAND, bestaande.getLocatie().getLand());
        assertEquals(100, bestaande.getCapaciteit());
        assertEquals(OperationeleStatus.ACTIEF, bestaande.getOperationeleStatus());
        assertEquals(ProductieStatus.GEZOND, bestaande.getProductieStatus());

    }

    @Test
    void updateSite_onbestaandeVerantwoordelijke_gooitValidationException_enRollback() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie(Locatie.builder("Oudstraat", "1", "1000", "Brussel", "België"))
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);
        when(siteRepo.existsByName(GELDIGE_NAAM, id)).thenReturn(false);
        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(null);

        assertThrows(ValidationException.class, () ->
                siteBeheerder.updateSite(
                        id,
                        GELDIGE_NAAM,
                        GELDIGE_VERANTWOORDELIJKE_ID,
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND,
                        100,
                        OperationeleStatus.ACTIEF,
                        ProductieStatus.GEZOND
                )
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).existsByName(GELDIGE_NAAM, id);
        verify(gebruikerRepo).get(GELDIGE_VERANTWOORDELIJKE_ID);
        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
    }

    @Test
    void updateSite_verantwoordelijkeMetVerkeerdeRol_gooitValidationException_enRollback() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie(Locatie.builder("Oudstraat", "1", "1000", "Brussel", "België"))
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        Gebruiker gebruiker = mock(Gebruiker.class);
        when(gebruiker.getRol()).thenReturn(Rollen.ADMINISTRATOR);

        when(siteRepo.get(id)).thenReturn(bestaande);
        when(siteRepo.existsByName(GELDIGE_NAAM, id)).thenReturn(false);
        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(gebruiker);

        assertThrows(ValidationException.class, () ->
                siteBeheerder.updateSite(
                        id,
                        GELDIGE_NAAM,
                        GELDIGE_VERANTWOORDELIJKE_ID,
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND,
                        100,
                        OperationeleStatus.ACTIEF,
                        ProductieStatus.GEZOND
                )
        );

        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    void updateSite_ongeldigeParameters_gooitException_enRollback(
            String naam, String straat, String nummer, String postcode, String gemeente, String land,
            int capaciteit, OperationeleStatus op, ProductieStatus prod) throws Exception {

        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie(Locatie.builder("Oudstraat", "1", "1000", "Brussel", "België"))
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);
        when(siteRepo.existsByName(nullable(String.class), eq(id))).thenReturn(false);

        assertThrows(ValidationException.class, () ->
                siteBeheerder.updateSite(id, naam, null, straat, nummer, postcode, gemeente, land, capaciteit, op, prod)
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
        verifyNoInteractions(gebruikerRepo);
    }

    @Test
    void deleteSite_bestaandeSite_verwijdertEnCommit() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam(GELDIGE_NAAM)
                .locatie(Locatie.builder(
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND
                ))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);

        siteBeheerder.deleteSite(id);

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).delete(bestaande);
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
    }

    @Test
    void deleteSite_onbestaandeSite_gooitException_enRollback() {
        long id = 99L;
        when(siteRepo.get(id)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> siteBeheerder.deleteSite(id));

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
        verify(siteRepo, never()).delete(any());
    }
    @Test
    void addSite_verantwoordelijkeHeeftAlAndereSite_gooitValidationException() throws Exception {
        Gebruiker verantwoordelijke = Gebruiker.builder()
                .personeelsnummer(1001)
                .naam("Janssens")
                .voornaam("Jan")
                .geboortedatum(LocalDate.parse("2000-01-01"))
                .locatie(Locatie.builder("Teststraat", "1", "9000", "Gent", "België"))
                .email("verantwoordelijke@example.com")
                .gsm("0470123456")
                .rol(Rollen.VERANTWOORDELIJKE)
                .status(GebruikerStatus.ACTIEF)
                .wachtwoord("Test123!")
                .build();

        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(verantwoordelijke);
        when(siteRepo.verantwoordelijkeHeeftAndereSite(GELDIGE_VERANTWOORDELIJKE_ID, null)).thenReturn(true);

        assertThrows(ValidationException.class, () ->
                siteBeheerder.addSite(
                        GELDIGE_NAAM,
                        GELDIGE_VERANTWOORDELIJKE_ID,
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND,
                        100,
                        OperationeleStatus.ACTIEF,
                        ProductieStatus.GEZOND
                )
        );
    }

    @Test
    void updateSite_verantwoordelijkeHeeftAndereSite_gooitValidationException_enRollback() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie(Locatie.builder("Oudstraat", "1", "1000", "Brussel", "België"))
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        Gebruiker verantwoordelijke = Gebruiker.builder()
                .personeelsnummer(1001)
                .naam("Janssens")
                .voornaam("Jan")
                .geboortedatum(LocalDate.parse("2000-01-01"))
                .locatie(Locatie.builder("Teststraat", "1", "9000", "Gent", "België"))
                .email("verantwoordelijke@example.com")
                .gsm("0470123456")
                .rol(Rollen.VERANTWOORDELIJKE)
                .status(GebruikerStatus.ACTIEF)
                .wachtwoord("Test123!")
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);
        when(siteRepo.existsByName(GELDIGE_NAAM, id)).thenReturn(false);
        when(gebruikerRepo.get(GELDIGE_VERANTWOORDELIJKE_ID)).thenReturn(verantwoordelijke);
        when(siteRepo.verantwoordelijkeHeeftAndereSite(GELDIGE_VERANTWOORDELIJKE_ID, id)).thenReturn(true);

        assertThrows(ValidationException.class, () ->
                siteBeheerder.updateSite(
                        id,
                        GELDIGE_NAAM,
                        GELDIGE_VERANTWOORDELIJKE_ID,
                        GELDIGE_STRAAT,
                        GELDIGE_NUMMER,
                        GELDIGE_POSTCODE,
                        GELDIGE_GEMEENTE,
                        GELDIGE_LAND,
                        100,
                        OperationeleStatus.ACTIEF,
                        ProductieStatus.GEZOND
                )
        );

        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
    }
}
