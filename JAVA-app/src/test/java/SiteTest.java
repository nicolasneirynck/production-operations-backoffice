import domein.Site;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SiteTest {

    private Site testSite;

    @BeforeEach
    void setUp() {
        testSite = new Site("Gent", "België", 100, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND
        );
    }

    static Stream<Arguments> geldigeCombinaties() {
        return Stream.of(
                Arguments.of(
                        Site.OperationeleStatus.ACTIEF,
                        Site.ProductieStatus.GEZOND
                ),
                Arguments.of(
                        Site.OperationeleStatus.ACTIEF,
                        Site.ProductieStatus.PROBLEMEN
                ),
                Arguments.of(
                        Site.OperationeleStatus.ACTIEF,
                        Site.ProductieStatus.OFFLINE
                ),
                Arguments.of(
                        Site.OperationeleStatus.NON_ACTIEF,
                        Site.ProductieStatus.OFFLINE
                )
        );
    }

    @ParameterizedTest
    @MethodSource("geldigeCombinaties")
    void constructor_GeldigeCombinaties_GeenException(Site.OperationeleStatus operationeleStatus,
            Site.ProductieStatus productieStatus) {

        Site site = new Site("Site-A", "Antwerpen", 100, operationeleStatus, productieStatus);

        assertEquals("Site-A", site.getNaam());
        assertEquals("Antwerpen", site.getLocatie());
        assertEquals(100, site.getCapaciteit());
        assertEquals(operationeleStatus, site.getOperationeleStatus());

        if (operationeleStatus == Site.OperationeleStatus.NON_ACTIEF) {
            assertEquals(Site.ProductieStatus.OFFLINE, site.getProductieStatus());
        } else {
            assertEquals(productieStatus, site.getProductieStatus());
        }
    }

    @ParameterizedTest
    @EnumSource(
            value = Site.ProductieStatus.class,
            names = {"OFFLINE"},
            mode = EnumSource.Mode.EXCLUDE
    )
    void constructor_NonActiefMetNietOffline_GooitException(Site.ProductieStatus productieStatus) {
        assertThrows(IllegalArgumentException.class, () ->
                new Site("Brugge", "België", 100,
                        Site.OperationeleStatus.NON_ACTIEF,
                        productieStatus)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void constructor_OngeldigeCapaciteit_GooitException(int cap) {
        assertThrows(IllegalArgumentException.class, () ->
                new Site("X", "Y", cap, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND)
        );
    }

    @ParameterizedTest
    @EnumSource(
            value = Site.ProductieStatus.class,
            names = {"OFFLINE"},
            mode = EnumSource.Mode.EXCLUDE
    )
    void setProductieStatus_NonActiefMetNietOffline_GooitException(Site.ProductieStatus productieStatus) {
        testSite.setOperationeleStatus(Site.OperationeleStatus.NON_ACTIEF);

        assertThrows(IllegalArgumentException.class, () ->
                testSite.setProductieStatus(productieStatus)
        );
    }

    @Test
    void setOperationeleStatus_NonActief_ZetProductieOffline() {
        testSite.setOperationeleStatus(Site.OperationeleStatus.NON_ACTIEF);
        assertEquals(Site.ProductieStatus.OFFLINE, testSite.getProductieStatus());
    }


    @Test
    void setOperationeleStatus_Null_GooitException() {
        assertThrows(IllegalArgumentException.class, () ->
                testSite.setOperationeleStatus(null)
        );
    }


    @Test
    void setProductieStatus_GezondWanneerActief_Werkt() {
        testSite.setProductieStatus(Site.ProductieStatus.GEZOND);
        assertEquals(Site.ProductieStatus.GEZOND, testSite.getProductieStatus());
    }


    @Test
    void setProductieStatus_Null_GooitException() {

        assertThrows(IllegalArgumentException.class, () ->
                testSite.setProductieStatus(null)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void setNaam_OngeldigeWaarden_GooitException(String ongeldigeNaam) {
        assertThrows(IllegalArgumentException.class, () ->
                testSite.setNaam(ongeldigeNaam)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void setLocatie_OngeldigeWaarden_GooitException(String ongeldigeLocatie) {
        assertThrows(IllegalArgumentException.class, () ->
                testSite.setLocatie(ongeldigeLocatie)
        );
    }

    @Test
    void setCapaciteit_GeldigeWaarde_Werkt() {
        testSite.setCapaciteit(250);
        assertEquals(250, testSite.getCapaciteit());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void setCapaciteit_OngeldigeWaarden_GooitException(int ongeldigeCapaciteit) {
        assertThrows(IllegalArgumentException.class, () ->
                testSite.setCapaciteit(ongeldigeCapaciteit)
        );
    }

}

