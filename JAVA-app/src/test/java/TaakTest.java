import domein.Taak;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import util.TaakType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TaakTest {

    static Stream<Arguments> geldigeTaken() {
        return Stream.of(
                Arguments.of(TaakType.ONDERHOUD, "Maandelijks onderhoud compressor", 60),
                Arguments.of(TaakType.INSPECTIE, "Visuele controle", 15),
                Arguments.of(TaakType.HERSTEL, "Vervang riem", 240),
                Arguments.of(TaakType.SCHOONMAAK, "Machine schoonmaken", 30)
        );
    }

    @ParameterizedTest
    @MethodSource("geldigeTaken")
    void constructor_GeldigeTaak_GeenException(TaakType type, String omschrijving, int duurtijd) {

        Taak taak = new Taak(type, omschrijving, duurtijd);

        assertEquals(type, taak.getTaakType());
        assertEquals(omschrijving, taak.getOmschrijving());
        assertEquals(duurtijd, taak.getDuurtijd());
    }

    @ParameterizedTest
    @NullSource
    void constructor_GeenTaakType_GooitException(TaakType type) {
        assertThrows(IllegalArgumentException.class, () ->
                new Taak(type, "Test", 60)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructor_OngeldigeOmschrijving_GooitException(String omschrijving) {
        assertThrows(IllegalArgumentException.class, () ->
                new Taak(TaakType.ONDERHOUD, omschrijving, 60)
        );
    }

    static Stream<Integer> ongeldigeDuur() {
        return Stream.of(1, 14, 16, 29, 31, 59, 61, 239,0,-1,-100,241,1000);
    }

    @ParameterizedTest
    @MethodSource("ongeldigeDuur")
    void constructor_OngeldigeDuur_GooitException(int minuten) {
        assertThrows(IllegalArgumentException.class, () ->
                new Taak(TaakType.INSPECTIE, "Test", minuten)
        );
    }
}
