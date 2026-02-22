import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import domein.Taak;
import domein.TaakController;
import dto.TaakDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GenericDao;
import util.TaakType;

@ExtendWith(MockitoExtension.class)
public class TaakControllerTest {

    private final String GELDIGE_OMSCHRIJVING = "Maandelijks onderhoud compressor";
    private final TaakType GELDIG_TYPE = TaakType.ONDERHOUD;
    private final int GELDIGE_DUURTIJD = 60; // minuten

    @Mock
    private GenericDao<Taak> taakRepo;

    @InjectMocks
    private TaakController taakController;

    @Test
    public void getAllTaken_geeftAlleTaken() {

        Taak eenTaak = new Taak(GELDIG_TYPE, GELDIGE_OMSCHRIJVING, GELDIGE_DUURTIJD);

        when(taakRepo.findAll()).thenReturn(Arrays.asList(eenTaak));

        List<TaakDTO> taken = taakController.getAllTaken();

        assertEquals(1, taken.size());
        assertEquals(GELDIGE_OMSCHRIJVING, taken.getFirst().omschrijving());
        assertEquals(GELDIG_TYPE, taken.getFirst().taakType());
        assertEquals(GELDIGE_DUURTIJD, taken.getFirst().duurtijd());

        verify(taakRepo).findAll();
    }

    @Test
    public void addTaak_GeldigeParameters_voegtTaakToe() {

        taakController.addTaak(
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
                Arguments.of(null, "Omschrijving", 60),                    // type null
                Arguments.of(TaakType.ONDERHOUD, "", 60),                 // omschrijving leeg
                Arguments.of(TaakType.ONDERHOUD, null, 60),               // omschrijving null
                Arguments.of(TaakType.ONDERHOUD, "   ", 60),              // omschrijving blank
                Arguments.of(TaakType.ONDERHOUD, "Test", 0),              // duurtijd 0
                Arguments.of(TaakType.ONDERHOUD, "Test", -15),            // duurtijd negatief
                Arguments.of(TaakType.ONDERHOUD, "Test", 14),             // niet deelbaar door 15
                Arguments.of(TaakType.ONDERHOUD, "Test", 16),             // niet deelbaar door 15
                Arguments.of(TaakType.ONDERHOUD, "Test", 241)             // > 4 uur
        );
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void addTaak_ongeldigeParameters_gooitException_enRaaktRepoNiet(
            TaakType type, String omschrijving, int duurtijd) {

        assertThrows(IllegalArgumentException.class, () ->
                taakController.addTaak(type, omschrijving, duurtijd)
        );

        verifyNoInteractions(taakRepo);
    }

    @Test
    public void updateTaak_geldigeParameters_pastTaakAan() {
        long id = 1L;

        Taak bestaande = new Taak(TaakType.INSPECTIE, "OUD", 30);

        when(taakRepo.get(id)).thenReturn(bestaande);

        taakController.updateTaak(
                id,
                GELDIG_TYPE,
                GELDIGE_OMSCHRIJVING,
                GELDIGE_DUURTIJD
        );

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).commitTransaction();
        verify(taakRepo, never()).rollbackTransaction();

        assertEquals(GELDIG_TYPE, bestaande.getTaakType());
        assertEquals(GELDIGE_OMSCHRIJVING, bestaande.getOmschrijving());
        assertEquals(GELDIGE_DUURTIJD, bestaande.getDuurtijd());
    }

    @ParameterizedTest
    @MethodSource("ongeldigeParameters")
    public void updateTaak_ongeldigeParameters_gooitException_enRollback(
            TaakType type, String omschrijving, int duurtijd) {

        long id = 1L;

        Taak bestaande = new Taak(TaakType.INSPECTIE, "OUD", 30);

        when(taakRepo.get(id)).thenReturn(bestaande);

        assertThrows(IllegalArgumentException.class, () ->
                taakController.updateTaak(id, type, omschrijving, duurtijd)
        );

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).rollbackTransaction();
        verify(taakRepo, never()).commitTransaction();
    }

    @Test
    public void deleteTaak_bestaandeTaak_verwijdertEnCommit() {
        long id = 1L;

        Taak bestaande = new Taak(GELDIG_TYPE, GELDIGE_OMSCHRIJVING, GELDIGE_DUURTIJD);

        when(taakRepo.get(id)).thenReturn(bestaande);

        taakController.deleteTaak(id);

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

        assertThrows(IllegalArgumentException.class, () -> taakController.deleteTaak(id));

        verify(taakRepo).startTransaction();
        verify(taakRepo).get(id);
        verify(taakRepo).rollbackTransaction();
        verify(taakRepo, never()).commitTransaction();
        verify(taakRepo, never()).delete(any());
    }
}
