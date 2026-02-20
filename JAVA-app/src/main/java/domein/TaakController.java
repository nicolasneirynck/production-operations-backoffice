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
                .map(this::createDto)
                .toList();
    }

    public TaakDTO addTaak(TaakType type, String omschrijving, int duurtijd) {
        Taak nieuweTaak = new Taak(type, omschrijving, duurtijd);

        taakRepo.startTransaction();
        try {
            taakRepo.insert(nieuweTaak);
            taakRepo.commitTransaction();
        } catch (RuntimeException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
        }

        return createDto(nieuweTaak);
    }

    public TaakDTO updateTaak(long id, TaakType type, String omschrijving, int duurtijd) {
        taakRepo.startTransaction();
        try {
            Taak taak = taakRepo.get(id);

            if (taak == null)
                throw new IllegalArgumentException("Taak niet gevonden.");

            taak.update(type, omschrijving, duurtijd);
            taakRepo.commitTransaction();

            return createDto(taak);
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

    private TaakDTO createDto(Taak taak){
        return new TaakDTO(taak.getTaakId(),
                taak.getTaakType(),
                taak.getOmschrijving(),
                taak.getDuurtijd()
        );
    }
}
