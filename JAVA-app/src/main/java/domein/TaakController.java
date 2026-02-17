package domein;

import dto.TaakDTO;
import repository.GenericDao;
import repository.GenericDaoJpa;
import util.TaakType;

import java.util.List;

public class TaakController {

    private final GenericDao<Taak> taakRepo;

    public TaakController() {
        this(new GenericDaoJpa<>(Taak.class));
    }

    // TODO tijdelijk voor devFase -> Mockito
    public TaakController(GenericDao<Taak> taakRepo) {
        this.taakRepo = taakRepo;
    }

    public List<TaakDTO> getAllTaken() {
        return taakRepo.findAll().stream()
                .map(t -> new TaakDTO(
                        t.getTaakId(),
                        t.getTaakType(),
                        t.getOmschrijving(),
                        t.getDuurtijd()
                ))
                .toList();
    }

    public void addTaak(TaakType type, String omschrijving, int duurtijdInMinuten) {
        Taak nieuweTaak = new Taak(type, omschrijving, duurtijdInMinuten);

        taakRepo.startTransaction();
        try {
            taakRepo.insert(nieuweTaak);
            taakRepo.commitTransaction();
        } catch (RuntimeException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void updateTaak(long id, TaakType type, String omschrijving, int duurtijd) {
        taakRepo.startTransaction();
        try {
            Taak taak = taakRepo.get(id);

            if (taak == null)
                throw new IllegalArgumentException("Taak niet gevonden.");

            taak.update(type, omschrijving, duurtijd);
            taakRepo.commitTransaction();
        } catch (RuntimeException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void deleteTaak(long id) {
        taakRepo.startTransaction();
        try {
            Taak taak = taakRepo.get(id);

            if (taak == null)
                throw new IllegalArgumentException("Taak niet gevonden.");

            taakRepo.delete(taak);
            taakRepo.commitTransaction();
        } catch (RuntimeException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
        }
    }
}
