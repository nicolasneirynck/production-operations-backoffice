import domein.Locatie;
import exception.SiteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class LocatieTest {

    private static final String GELDIGE_STRAAT = "Kortrijksesteenweg";
    private static final String GELDIGE_NUMMER = "80";
    private static final String GELDIGE_POSTCODE = "9000";
    private static final String GELDIGE_STAD = "Gent";
    private static final String GELDIGE_LAND = "België";

    @Test
    void builder_GeldigeLocatie_GeenException() throws Exception {

        Locatie locatie = Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_STAD, GELDIGE_LAND);

        assertEquals(GELDIGE_STRAAT, locatie.getStraat());
        assertEquals(GELDIGE_NUMMER, locatie.getNummer());
        assertEquals(GELDIGE_POSTCODE, locatie.getPostcode());
        assertEquals(GELDIGE_STAD, locatie.getStad());
        assertEquals(GELDIGE_LAND, locatie.getLand());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigeStraat_GooitException(String straat) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(straat, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_STAD, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigNummer_GooitException(String nummer) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, nummer, GELDIGE_POSTCODE, GELDIGE_STAD, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigePostcode_GooitException(String postcode) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, postcode, GELDIGE_STAD, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigeStad_GooitException(String stad) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, stad, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigLand_GooitException(String land) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_STAD, land)
        );
    }

    static Stream<Arguments> ongeldigeVelden() {
        return Stream.of(
                Arguments.of(null, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_STAD, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, null, GELDIGE_POSTCODE, GELDIGE_STAD, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, GELDIGE_NUMMER, null, GELDIGE_STAD, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, null, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_STAD, null)
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeVelden")
    void builder_OngeldigeVelden_GooitException(String straat, String nummer, String postcode, String stad, String land) {
        assertThrows(SiteException.class, () -> Locatie.builder(straat, nummer, postcode, stad, land));
    }
}