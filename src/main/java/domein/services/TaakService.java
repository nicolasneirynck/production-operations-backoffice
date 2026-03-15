package domein.services;

import dto.DTOMapper;
import dto.TaakDTO;
import domein.beheerders.TaakBeheerder;
import exception.ValidationException;

import java.util.List;

public class TaakService {

    private final TaakBeheerder taakBeheerder;

    public TaakService(TaakBeheerder taakBeheerder) {
        this.taakBeheerder = taakBeheerder;
    }

    // TODO tijdelijk voor devFase -> Mockito
    public TaakService() {
        this(new TaakBeheerder());
    }

    public List<TaakDTO> getAllTaken() {
        return taakBeheerder.getAllTaken().stream()
                .map(DTOMapper::toTaakDTO)
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
}
