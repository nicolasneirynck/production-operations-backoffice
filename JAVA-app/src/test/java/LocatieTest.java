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
    private static final String GELDIGE_GEMEENTE = "Gent";
    private static final String GELDIGE_LAND = "België";

    @Test
    void builder_GeldigeLocatie_GeenException() throws Exception {

        Locatie locatie = Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND);

        assertEquals(GELDIGE_STRAAT, locatie.getStraat());
        assertEquals(GELDIGE_NUMMER, locatie.getNummer());
        assertEquals(GELDIGE_POSTCODE, locatie.getPostcode());
        assertEquals(GELDIGE_GEMEENTE, locatie.getGemeente());
        assertEquals(GELDIGE_LAND, locatie.getLand());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigeStraat_GooitException(String straat) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(straat, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigNummer_GooitException(String nummer) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, nummer, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigePostcode_GooitException(String postcode) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, postcode, GELDIGE_GEMEENTE, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigeGemeente_GooitException(String gemeente) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, gemeente, GELDIGE_LAND)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void builder_OngeldigLand_GooitException(String land) {
        assertThrows(SiteException.class, () ->
                Locatie.builder(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, land)
        );
    }

    static Stream<Arguments> ongeldigeVelden() {
        return Stream.of(
                Arguments.of(null, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, null, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, GELDIGE_NUMMER, null, GELDIGE_GEMEENTE, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, null, GELDIGE_LAND),
                Arguments.of(GELDIGE_STRAAT, GELDIGE_NUMMER, GELDIGE_POSTCODE, GELDIGE_GEMEENTE, null)
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeVelden")
    void builder_OngeldigeVelden_GooitException(String straat, String nummer, String postcode, String gemeente, String land) {
        assertThrows(SiteException.class, () -> Locatie.builder(straat, nummer, postcode, gemeente, land));
    }
}