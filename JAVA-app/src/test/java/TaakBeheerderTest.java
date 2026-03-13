import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import domein.beheerders.TaakBeheerder;
import domein.entiteiten.Taak;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TaakDao;

@ExtendWith(MockitoExtension.class)
public class TaakBeheerderTest {

    private final String GELDIGE_OMSCHRIJVING = "Maandelijks onderhoud compressor";
    private final String GELDIG_TYPE = "Onderhoud";
    private final int GELDIGE_DUURTIJD = 60;

    @Mock
    private TaakDao taakRepo;

    @InjectMocks
    private TaakBeheerder taakBeheerder;

    @Test
    public void getAllTaken_geeftAlleTaken() throws Exception {
        Taak eenTaak = Taak.builder()
                .type(GELDIG_TYPE)
                .omschrijving(GELDIGE_OMSCHRIJVING)
                .duurtijd(GELDIGE_DUURTIJD)
                .build();

        when(taakRepo.findAll()).thenReturn(Arrays.asList(eenTaak));

        List<Taak> taken = taakBeheerder.getAllTaken();

        assertEquals(1, taken.size());
        assertEquals(GELDIGE_OMSCHRIJVING, taken.getFirst().getOmschrijving());
        assertEquals(GELDIG_TYPE.toUpperCase(), taken.getFirst().getTaakType());
        assertEquals(GELDIGE_DUURTIJD, taken.getFirst().getDuurtijd());

        verify(taakRepo).findAll();
    }

    @Test
    public void addTaak_geldigeParameters_voegtTaakToe() throws Exception {
        taakBeheerder.addTaak(
                GELDIG_TYPE,
                GELDIGE_OMSCHRIJVING,
                GELDIGE_DUURTIJD
        );

        verify(taakRepo).startTransaction();
        verify(taakRepo).insert(any(Taak.class));
        verify(taakRepo).commitTransaction();
        verify(taakRepo, never()).rollbackTransaction();
    }

    private static Stream<Arguments> ongeldigeParameters() {
        return Stream.of(
                Arguments.of(null, "Omschrijving", 60),
                Arguments.of("", "Omschrijving", 60),
                Arguments.of("   ", "Omschrijving", 60),

                Arguments.of("Onderhoud", "", 60),
                Arguments.of("Onderhoud", null, 60),
                Arguments.of("Onderhoud", "   ", 60),

                Arguments.of("Onderhoud", "Test", 0),
                Arguments.of("Onderhoud", "Test", -15),
                Arguments.of("Onderhoud", "Test", 14),
                Arguments.of("Onderhoud", "Test", 16),
                Arguments.of("Onderhoud", "Test", 241)
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void addTaak_ongeldigeParameters_gooitException_enRaaktRepoNiet(
            String type, String omschrijving, int duurtijd) {

        assertThrows(ValidationException.class, () ->
                taakBeheerder.addTaak(type, omschrijving, duurtijd)
        );

        verifyNoInteractions(taakRepo);
    }

    @Test
    public void updateTaak_geldigeParameters_pastTaakAan() throws Exception {
        long id = 1L;

        Taak bestaande = Taak.builder()
                .type(GELDIG_TYPE)
                .omschrijving("OUD")
                .duurtijd(30)
                .build();

        when(taakRepo.get(id)).thenReturn(bestaande);

        taakBeheerder.updateTaak(
                id,
                GELDIG_TYPE,
                GELDIGE_OMSCHRIJVING,
                GELDIGE_DUURTIJD
        );

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).commitTransaction();
        verify(taakRepo, never()).rollbackTransaction();

        assertEquals(GELDIG_TYPE.toUpperCase(), bestaande.getTaakType());
        assertEquals(GELDIGE_OMSCHRIJVING, bestaande.getOmschrijving());
        assertEquals(GELDIGE_DUURTIJD, bestaande.getDuurtijd());
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void updateTaak_ongeldigeParameters_gooitException_enRollback(
            String type, String omschrijving, int duurtijd) throws Exception {

        long id = 1L;

        Taak bestaande = Taak.builder()
                .type(GELDIG_TYPE)
                .omschrijving("OUD")
                .duurtijd(30)
                .build();

        when(taakRepo.get(id)).thenReturn(bestaande);

        assertThrows(ValidationException.class, () ->
                taakBeheerder.updateTaak(id, type, omschrijving, duurtijd)
        );

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).rollbackTransaction();
        verify(taakRepo, never()).commitTransaction();
    }

    @Test
    public void updateTaak_onbestaandeTaak_gooitException_enRollback() {
        long id = 99L;
        when(taakRepo.get(id)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                taakBeheerder.updateTaak(id, GELDIG_TYPE, GELDIGE_OMSCHRIJVING, GELDIGE_DUURTIJD)
        );

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).rollbackTransaction();
        verify(taakRepo, never()).commitTransaction();
    }

    @Test
    public void deleteTaak_bestaandeTaak_verwijdertEnCommit() throws Exception {
        long id = 1L;

        Taak bestaande = Taak.builder()
                .type(GELDIG_TYPE)
                .omschrijving(GELDIGE_OMSCHRIJVING)
                .duurtijd(GELDIGE_DUURTIJD)
                .build();

        when(taakRepo.get(id)).thenReturn(bestaande);

        taakBeheerder.deleteTaak(id);

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).delete(bestaande);
        verify(taakRepo).commitTransaction();
        verify(taakRepo, never()).rollbackTransaction();
    }

    @Test
    public void deleteTaak_onbestaandeTaak_gooitException_enRollback() {
        long id = 99L;
        when(taakRepo.get(id)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taakBeheerder.deleteTaak(id));

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).rollbackTransaction();
        verify(taakRepo, never()).commitTransaction();
        verify(taakRepo, never()).delete(any());
    }
}
