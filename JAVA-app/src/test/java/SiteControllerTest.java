import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import domein.Site;
import domein.SiteController;
import dto.SiteDTO;
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
    private final String GELDIGE_LOCATIE = "BELGIË";

    @Mock
    private SiteDao siteRepo;

    @InjectMocks
    private SiteController siteController;

    @Test
    public void getAllSites_geeftAlleSites() {

        Site eenSite = Site.builder()
                .naam(GELDIGE_NAAM)
                .locatie(GELDIGE_LOCATIE)
                .capaciteit(100)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.findAll()).thenReturn(Arrays.asList(eenSite));

        List<SiteDTO> sites = siteController.getAllSites();

        assertEquals(1, sites.size());
        assertEquals(GELDIGE_NAAM, sites.getFirst().naam());
        verify(siteRepo).findAll();
    }

    @Test
    public void addSite_GeldigeParameters_voegtSiteToe() {

        siteController.addSite(
                GELDIGE_NAAM,
                GELDIGE_LOCATIE,
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
                Arguments.of("", "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of(null, "België", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE_A", "", 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE_A", null, 100, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", -10, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", 0, OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                // NON_ACTIEF maar productie niet OFFLINE
                Arguments.of("SITE-A", "België", 100, OperationeleStatus.NON_ACTIEF, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", 100, null, ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", 100, OperationeleStatus.ACTIEF, null)
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void addSite_ongeldigeParameters_gooitException_enRaaktRepoNiet(String naam, String locatie, int capaciteit, OperationeleStatus op, ProductieStatus prod) {

        assertThrows(IllegalArgumentException.class, () ->
                siteController.addSite(naam, locatie, capaciteit, op, prod)
        );

        verifyNoInteractions(siteRepo);
    }

    @Test
    public void updateSite_geldigeParameters_pastSiteAan() {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie("OUD")
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);

        siteController.updateSite(
                id,
                GELDIGE_NAAM,
                GELDIGE_LOCATIE,
                100,
                OperationeleStatus.ACTIEF,
                ProductieStatus.GEZOND
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();

        assertEquals(GELDIGE_NAAM, bestaande.getNaam());
        assertEquals(GELDIGE_LOCATIE, bestaande.getLocatie());
        assertEquals(100, bestaande.getCapaciteit());
        assertEquals(OperationeleStatus.ACTIEF, bestaande.getOperationeleStatus());
        assertEquals(ProductieStatus.GEZOND, bestaande.getProductieStatus());
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void updateSite_ongeldigeParameters_gooitException_enRollback(
            String naam, String locatie, int capaciteit,
            OperationeleStatus op, ProductieStatus prod) {

        long id = 1L;

        Site bestaande = Site.builder()
                .naam("OUD")
                .locatie("OUD")
                .capaciteit(50)
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .productieStatus(ProductieStatus.GEZOND)
                .build();

        when(siteRepo.get(id)).thenReturn(bestaande);

        assertThrows(IllegalArgumentException.class, () ->
                siteController.updateSite(id, naam, locatie, capaciteit, op, prod)
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).get(id);
        verify(siteRepo).rollbackTransaction();
        verify(siteRepo, never()).commitTransaction();
    }

    @Test
    public void deleteSite_bestaandeSite_verwijdertEnCommit() {
        long id = 1L;

        Site bestaande = Site.builder()
                .naam(GELDIGE_NAAM)
                .locatie(GELDIGE_LOCATIE)
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