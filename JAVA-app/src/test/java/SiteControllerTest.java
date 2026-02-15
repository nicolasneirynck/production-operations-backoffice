import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import domein.Site;
import domein.SiteController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GenericDao;

@ExtendWith(MockitoExtension.class)
public class SiteControllerTest {

    private final String GELDIGE_NAAM = "SITE-A";
    private final String GELDIGE_LOCATIE = "BELGIË";

    @Mock
    private GenericDao<Site> siteRepo;

    @InjectMocks
    private SiteController siteController;

    @Test
    public void getAllSites_geeftAlleSites() {

        Site eenSite = Site.builder()
                .naam(GELDIGE_NAAM)
                .locatie(GELDIGE_LOCATIE)
                .capaciteit(100)
                .operationeleStatus(Site.OperationeleStatus.ACTIEF)
                .productieStatus(Site.ProductieStatus.GEZOND)
                .build();

        when(siteRepo.findAll()).thenReturn(Arrays.asList(eenSite));

        List<Site> sites = siteController.getAllSites();

        assertEquals(1, sites.size());
        assertEquals(GELDIGE_NAAM, sites.getFirst().getNaam());
        verify(siteRepo).findAll();
    }

    @Test
    public void addSite_GeldigeParameters_voegtSiteToe() {

        siteController.addSite(
                GELDIGE_NAAM,
                GELDIGE_LOCATIE,
                100,
                Site.OperationeleStatus.ACTIEF,
                Site.ProductieStatus.GEZOND
        );

        verify(siteRepo).startTransaction();
        verify(siteRepo).insert(any(Site.class));
        verify(siteRepo).commitTransaction();
        verify(siteRepo, never()).rollbackTransaction();
    }

    private static Stream<Arguments> ongeldigeParameters() {
        return Stream.of(
                Arguments.of("", "België", 100, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND),
                Arguments.of(null, "België", 100, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND),
                Arguments.of("SITE_A", "", 100, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND),
                Arguments.of("SITE_A", null, 100, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", -10, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", 0, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND),
                // NON_ACTIEF maar productie niet OFFLINE
                Arguments.of("SITE-A", "België", 100, Site.OperationeleStatus.NON_ACTIEF, Site.ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", 100, null, Site.ProductieStatus.GEZOND),
                Arguments.of("SITE-A", "België", 100, Site.OperationeleStatus.ACTIEF, null)
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void addSite_ongeldigeParameters_gooitException_enRaaktRepoNiet(String naam, String locatie, int capaciteit, Site.OperationeleStatus op, Site.ProductieStatus prod) {

        assertThrows(IllegalArgumentException.class, () ->
                siteController.addSite(naam, locatie, capaciteit, op, prod)
        );

        verifyNoInteractions(siteRepo);
    }
}