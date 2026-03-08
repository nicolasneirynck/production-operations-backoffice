import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import domein.Locatie;
import domein.Site;
import domein.SiteController;
import dto.SiteDTO;
import exception.SiteException;
import exception.TaakException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GenericDao;
import repository.SiteDao;
import util.OperationeleStatus;
import util.ProductieStatus;

@ExtendWith(MockitoExtension.class)
public class SiteControllerTest {

    private final String GELDIGE_NAAM = "SITE-A";

    private final String GELDIGE_STRAAT = "Kortrijksesteenweg";
    private final String GELDIGE_NUMMER = "80";
    private final String GELDIGE_POSTCODE = "9000";
    private final String GELDIGE_GEMEENTE = "Gent";
    private final String GELDIGE_LAND = "België";
    private final String GELDIGE_LOCATIE_STRING =
            "%s %s, %s %s, %s".formatted(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND);

    @Mock
    private SiteDao siteRepo;

    @InjectMocks
    private SiteController siteController;

    @Test
    public void getAllSites_geeftAlleSites() throws Exception{

        Site eenSite = Site.builder()
                .naam(GELDIGE_NAAM)
                .locatie(Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.findAll()).thenReturn(Arrays.asList(eenSite));

        List<SiteDTO> sites = siteController.getAllSites();

        assertEquals(1, sites.size());
        assertEquals(GELDIGE_NAAM, sites.getFirst().naam());
        assertEquals(GELDIGE_LOCATIE_STRING, sites.getFirst().locatie());
        verify(siteRepo).findAll();
    }

    @Test
    public void addSite_GeldigeParameters_voegtSiteToe() throws Exception {

        siteController.addSite(
                GELDIGE_NAAM,
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
        verify(siteRepo).insert(any(Site.class));
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
    }

    private static Stream<Arguments> ongeldigeParameters() {
        return Stream.of(
                Arguments.of("", "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of(null, "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE_A", "", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),   // straat
                Arguments.of("SITE_A", null, "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE_A", "Straat", "", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND), // nummer
                Arguments.of("SITE_A", "Straat", null, "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE_A", "Straat", "1", "", "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),   // postcode
                Arguments.of("SITE_A", "Straat", "1", null, "Gent", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE_A", "Straat", "1", "9000", "", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),   // gemeente
                Arguments.of("SITE_A", "Straat", "1", "9000", null, "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                Arguments.of("SITE_A", "Straat", "1", "9000", "Gent", "", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),     // land
                Arguments.of("SITE_A", "Straat", "1", "9000", "Gent", null, 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                // capaciteit ongeldig
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", -10, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 0, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),

                // NON_ACTIEF maar productie niet OFFLINE
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.NON_ACTIEF, ProductieStatus.GEZOND),

                // status null
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 100, null, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "Straat", "1", "9000", "Gent", "België", 100, OperationeleStatus.ACTIEF, null)
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void addSite_ongeldigeParameters_gooitException_enRaaktRepoNiet(String naam, String straat, String nummer, String postcode,
                                                                           String gemeente, String land, int capaciteit,
                                                                           OperationeleStatus op, ProductieStatus prod) {

        assertThrows(SiteException.class, () ->
                siteController.addSite(naam, straat, nummer, postcode, gemeente, land, capaciteit, op, prod)
        );

        verifyNoInteractions(siteRepo);
    }

    @Test
    public void updateSite_geldigeParameters_pastSiteAan() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie(Locatie.builder("Oudstraat", "1", "1000", "Brussel", "België"))                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);

        siteController.updateSite(
                id,
                GELDIGE_NAAM,
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
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();

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

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void updateSite_ongeldigeParameters_gooitException_enRollback(
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

        assertThrows(SiteException.class, () ->
                siteController.updateSite(id, naam, straat, nummer, postcode, gemeente, land, capaciteit, op, prod)
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
    }

    @Test
    public void deleteSite_bestaandeSite_verwijdertEnCommit() throws Exception {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam(GELDIGE_NAAM)
                .locatie(Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND))
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);

        siteController.deleteSite(id);

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).delete(bestaande);
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
    }

    @Test
    public void deleteSite_onbestaandeSite_gooitException_enRollback() {
        long id = 99L;
        when(siteRepo.get(id)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> siteController.deleteSite(id));

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
        verify(siteRepo, never()).delete(any());
    }

}