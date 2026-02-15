import domein.Site;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SiteTest {

    static Stream<Arguments> geldigeCombinaties() {
        return Stream.of(
                Arguments.of(OperationeleStatus.ACTIEF, ProductieStatus.GEZOND),
                Arguments.of(OperationeleStatus.ACTIEF, ProductieStatus.PROBLEMEN),
                Arguments.of(OperationeleStatus.ACTIEF, ProductieStatus.OFFLINE),
                Arguments.of(OperationeleStatus.NON_ACTIEF, ProductieStatus.OFFLINE)
        );
    }

    @ParameterizedTest
    @MethodSource("geldigeCombinaties")
    void builder_GeldigeCombinaties_GeenException(OperationeleStatus op, ProductieStatus prod) {

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
            value = ProductieStatus.class,
            names = {"OFFLINE"},
            mode = EnumSource.Mode.EXCLUDE
    )
    void builder_NonActiefMetNietOffline_GooitException(ProductieStatus prod) {
        assertThrows(IllegalArgumentException.class, () ->
                Site.builder()
                        .naam("Brugge")
                        .locatie("België")
                        .capaciteit(100)
                        .operationeleStatus(OperationeleStatus.NON_ACTIEF)
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
                        .operationeleStatus(OperationeleStatus.ACTIEF)
                        .productieStatus(ProductieStatus.GEZOND)
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
                        .operationeleStatus(OperationeleStatus.ACTIEF)
                        .productieStatus(ProductieStatus.GEZOND)
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
                        .operationeleStatus(OperationeleStatus.ACTIEF)
                        .productieStatus(ProductieStatus.GEZOND)
                        .build()
        );
    }
}
