package domein.controllers;

import domein.entiteiten.Taak;
import dto.TaakDTO;
import domein.beheerders.TaakBeheerder;
import exception.ValidationException;

import java.util.List;

public class TaakController {

    private final TaakBeheerder taakBeheerder;

    public TaakController(TaakBeheerder taakBeheerder) {
        this.taakBeheerder = taakBeheerder;
    }

    // TODO tijdelijk voor devFase -> Mockito
    public TaakController() {
        this(new TaakBeheerder());
    }

    public List<TaakDTO> getAllTaken() {
        return taakBeheerder.getAllTaken().stream()
                .map(this::toDto)
                .toList();
    }

    public void addTaak(String type, String omschrijving, int duurtijd) throws ValidationException {
        taakBeheerder.addTaak(type, omschrijving, duurtijd);
    }

    public void updateTaak(long id, String type, String omschrijving, int duurtijd) throws ValidationException {
        taakBeheerder.updateTaak(id, type, omschrijving, duurtijd);
    }

    public void deleteTaak(long id) {
        taakBeheerder.deleteTaak(id);
    }

    public List<String> getAllTaakTypes() {
        return taakBeheerder.getAllTaakTypes();
    }

    private TaakDTO toDto(Taak taak) {
        return new TaakDTO(
                taak.getId(),
                taak.getTaakType(),
                taak.getOmschrijving(),
                taak.getDuurtijd()
        );
    }
}