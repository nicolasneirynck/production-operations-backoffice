import domein.entiteiten.Taak;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TaakTest {

    static Stream<Arguments> geldigeTaken() {
        return Stream.of(
                Arguments.of("Onderhoud", "Maandelijks onderhoud compressor", 60),
                Arguments.of("Inspectie", "Visuele controle", 15),
                Arguments.of("Herstel", "Vervang riem", 240),
                Arguments.of("Herstel", "Machine schoonmaken", 30)
        );
    }

    @ParameterizedTest
    @MethodSource("geldigeTaken")
    void constructor_GeldigeTaak_GeenException(String type, String omschrijving, int duurtijd) throws Exception {

        Taak taak = Taak.builder()
                .type(type)
                .omschrijving(omschrijving)
                .duurtijd(duurtijd)
                .build();

        assertEquals(type.toUpperCase(), taak.getTaakType());
        assertEquals(omschrijving, taak.getOmschrijving());
        assertEquals(duurtijd, taak.getDuurtijd());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    void constructor_GeenOfOngeldigTaakType_GooitException(String type) {
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
                        .type("Onderhoud")
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
                        .type("Onderhoud")
                        .omschrijving("Test")
                        .duurtijd(minuten)
                        .build()
        );
        assertTrue(ex.getExceptionMap().containsKey("duurtijd"));
    }
}
