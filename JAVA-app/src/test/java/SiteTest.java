import domein.Site;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SiteTest {

    static Stream<Arguments> geldigeCombinaties() {
        return Stream.of(
                Arguments.of(Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND),
                Arguments.of(Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.PROBLEMEN),
                Arguments.of(Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.OFFLINE),
                Arguments.of(Site.OperationeleStatus.NON_ACTIEF, Site.ProductieStatus.OFFLINE)
        );
    }

    @ParameterizedTest
    @MethodSource("geldigeCombinaties")
    void builder_GeldigeCombinaties_GeenException(Site.OperationeleStatus op, Site.ProductieStatus prod) {

        Site site = Site.builder()
                .naam("Site-A")
                .locatie("Antwerpen")
                .capaciteit(100)
                .operationeleStatus(op)
                .productieStatus(prod)
                .build();

        assertEquals("Site-A", site.getNaam());
        assertEquals("Antwerpen", site.getLocatie());
        assertEquals(100, site.getCapaciteit());
        assertEquals(op, site.getOperationeleStatus());
        assertEquals(prod, site.getProductieStatus());
    }

    @ParameterizedTest
    @EnumSource(
            value = Site.ProductieStatus.class,
            names = {"OFFLINE"},
            mode = EnumSource.Mode.EXCLUDE
    )
    void builder_NonActiefMetNietOffline_GooitException(Site.ProductieStatus prod) {
        assertThrows(IllegalArgumentException.class, () ->
                Site.builder()
                        .naam("Brugge")
                        .locatie("België")
                        .capaciteit(100)
                        .operationeleStatus(Site.OperationeleStatus.NON_ACTIEF)
                        .productieStatus(prod)
                        .build()
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void builder_OngeldigeCapaciteit_GooitException(int cap) {
        assertThrows(IllegalArgumentException.class, () ->
                Site.builder()
                        .naam("X")
                        .locatie("Y")
                        .capaciteit(cap)
                        .operationeleStatus(Site.OperationeleStatus.ACTIEF)
                        .productieStatus(Site.ProductieStatus.GEZOND)
                        .build()
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigeNaam_GooitException(String naam) {
        assertThrows(IllegalArgumentException.class, () ->
                Site.builder()
                        .naam(naam)
                        .locatie("Y")
                        .capaciteit(10)
                        .operationeleStatus(Site.OperationeleStatus.ACTIEF)
                        .productieStatus(Site.ProductieStatus.GEZOND)
                        .build()
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigeLocatie_GooitException(String locatie) {
        assertThrows(IllegalArgumentException.class, () ->
                Site.builder()
                        .naam("X")
                        .locatie(locatie)
                        .capaciteit(10)
                        .operationeleStatus(Site.OperationeleStatus.ACTIEF)
                        .productieStatus(Site.ProductieStatus.GEZOND)
                        .build()
        );
    }
}
