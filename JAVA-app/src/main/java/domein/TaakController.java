package domein;

import dto.TaakDTO;
import exception.TaakException;
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

    public void addTaak(String type, String omschrijving, int duurtijd) throws TaakException {

        Taak nieuweTaak = Taak.builder().type(type).omschrijving(omschrijving).duurtijd(duurtijd).build();
        //Taak nieuweTaak = new Taak(type, omschrijving, duurtijd);

            taakRepo.startTransaction();
            try {
                taakRepo.insert(nieuweTaak);
                taakRepo.commitTransaction();
            } catch (RuntimeException ex) {
                taakRepo.rollbackTransaction();
                throw ex;
            }

           // return createDto(nieuweTaak);
    }

    public void updateTaak(long id, String type, String omschrijving, int duurtijd) throws TaakException{
        taakRepo.startTransaction();
        try {
            Taak taak = taakRepo.get(id);
            if (taak == null)
                throw new IllegalArgumentException("Taak niet gevonden.");

            taak.update(type, omschrijving, duurtijd);
            taakRepo.commitTransaction();
            //return createDto(taak); // gebruiken we dit nog?

        } catch (TaakException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
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
