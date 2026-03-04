import domein.Taak;
import exception.TaakException;
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
    void constructor_GeldigeTaak_GeenException(TaakType type, String omschrijving, int duurtijd) throws Exception {

        Taak taak = Taak.builder()
                .type(type)
                .omschrijving(omschrijving)
                .duurtijd(duurtijd)
                .build();

        assertEquals(type, taak.getTaakType());
        assertEquals(omschrijving, taak.getOmschrijving());
        assertEquals(duurtijd, taak.getDuurtijd());
    }

    @ParameterizedTest
    @NullSource
    void constructor_GeenTaakType_GooitException(TaakType type) {
        TaakException ex = assertThrows(TaakException.class, () ->
                Taak.builder()
                        .type(type)
                        .omschrijving("Test")
                        .duurtijd(60)
                        .build()
        );
        assertTrue(ex.getExceptionMap().containsKey("taakType"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructor_OngeldigeOmschrijving_GooitException(String omschrijving) {
        TaakException ex = assertThrows(TaakException.class, () ->
                Taak.builder()
                        .type(TaakType.ONDERHOUD)
                        .omschrijving(omschrijving)
                        .duurtijd(60)
                        .build()
        );
        assertTrue(ex.getExceptionMap().containsKey("omschrijving"));
    }

    static Stream<Integer> ongeldigeDuur() {
        return Stream.of(1, 14, 16, 29, 31, 59, 61, 239,0,-1,-100,241,1000);
    }

    @ParameterizedTest
    @MethodSource("ongeldigeDuur")
    void constructor_OngeldigeDuur_GooitException(int minuten) {
        TaakException ex = assertThrows(TaakException.class, () ->
                Taak.builder()
                        .type(TaakType.INSPECTIE)
                        .omschrijving("Test")
                        .duurtijd(minuten)
                        .build()
        );
        assertTrue(ex.getExceptionMap().containsKey("duurtijd"));
    }
}
