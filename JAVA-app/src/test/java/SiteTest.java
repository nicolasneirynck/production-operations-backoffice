import domein.Locatie;
import domein.Site;
import exception.SiteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import util.OperationeleStatus;
import util.ProductieStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SiteTest {

    private static final String STRAAT = "Meir";
    private static final String NUMMER = "1";
    private static final String POSTCODE = "2000";
    private static final String STAD = "Antwerpen";
    private static final String LAND = "België";

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
    void builder_GeldigeCombinaties_GeenException(OperationeleStatus op, ProductieStatus prod) throws Exception {

        Site site = Site.builder()
                .naam("Site-A")
                .locatie(Locatie.builder(STRAAT, NUMMER, POSTCODE, STAD, LAND))
                .capaciteit(100)
                .operationeleStatus(op)
                .productieStatus(prod)
                .build();

        assertEquals("Site-A", site.getNaam());

        assertEquals(STRAAT, site.getLocatie().getStraat());
        assertEquals(NUMMER, site.getLocatie().getNummer());
        assertEquals(POSTCODE, site.getLocatie().getPostcode());
        assertEquals(STAD, site.getLocatie().getStad());
        assertEquals(LAND, site.getLocatie().getLand());

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
    void builder_NonActiefMetNietOffline_GooitException(ProductieStatus prod){
        assertThrows(SiteException.class, () ->
                Site.builder()
                        .naam("Brugge")
                        .locatie(Locatie.builder(STRAAT, NUMMER, POSTCODE, STAD, LAND))
                        .capaciteit(100)
                        .operationeleStatus(OperationeleStatus.NON_ACTIEF)
                        .productieStatus(prod)
                        .build()
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void builder_OngeldigeCapaciteit_GooitException(int cap) {
        assertThrows(SiteException.class, () ->
                Site.builder()
                        .naam("X")
                        .locatie(Locatie.builder(STRAAT, NUMMER, POSTCODE, STAD, LAND))
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
        assertThrows(SiteException.class, () ->
                Site.builder()
                        .naam(naam)
                        .locatie(Locatie.builder(STRAAT, NUMMER, POSTCODE, STAD, LAND))
                        .capaciteit(10)
                        .operationeleStatus(OperationeleStatus.ACTIEF)
                        .productieStatus(ProductieStatus.GEZOND)
                        .build()
        );
    }

    static Stream<Arguments> ongeldigeLocatieVelden() {
        return Stream.of(
                Arguments.of(null, NUMMER, POSTCODE, STAD, LAND),
                Arguments.of("", NUMMER, POSTCODE, STAD, LAND),
                Arguments.of("   ", NUMMER, POSTCODE, STAD, LAND),

                Arguments.of(STRAAT, null, POSTCODE, STAD, LAND),
                Arguments.of(STRAAT, "", POSTCODE, STAD, LAND),
                Arguments.of(STRAAT, "   ", POSTCODE, STAD, LAND),

                Arguments.of(STRAAT, NUMMER, null, STAD, LAND),
                Arguments.of(STRAAT, NUMMER, "", STAD, LAND),
                Arguments.of(STRAAT, NUMMER, "   ", STAD, LAND),

                Arguments.of(STRAAT, NUMMER, POSTCODE, null, LAND),
                Arguments.of(STRAAT, NUMMER, POSTCODE, "", LAND),
                Arguments.of(STRAAT, NUMMER, POSTCODE, "   ", LAND),

                Arguments.of(STRAAT, NUMMER, POSTCODE, STAD, null),
                Arguments.of(STRAAT, NUMMER, POSTCODE, STAD, ""),
                Arguments.of(STRAAT, NUMMER, POSTCODE, STAD, "   ")
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeLocatieVelden")
    void builder_OngeldigeLocatie_GooitException(String straat, String nummer, String postcode, String stad, String land) {
        assertThrows(SiteException.class, () ->
                Site.builder()
                        .naam("X")
                        .locatie(Locatie.builder(straat, nummer, postcode, stad, land))
                        .capaciteit(10)
                        .operationeleStatus(OperationeleStatus.ACTIEF)
                        .productieStatus(ProductieStatus.GEZOND)
                        .build()
        );
    }
}
