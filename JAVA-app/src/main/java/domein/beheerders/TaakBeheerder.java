package domein.beheerders;

import domein.entiteiten.Taak;
import exception.TaakException;
import repository.TaakDao;
import repository.TaakDaoJpa;

import java.util.List;

public class TaakBeheerder {

    private final TaakDao taakRepo;

    public TaakBeheerder(TaakDao taakRepo) {
        this.taakRepo = taakRepo;
    }

    // TODO tijdelijk voor devFase -> Mockito
    public TaakBeheerder() {
        this(new TaakDaoJpa(Taak.class));
    }

    public List<Taak> getAllTaken() {
        return taakRepo.findAll();
    }

    public void addTaak(String type, String omschrijving, int duurtijd) throws TaakException {
        Taak nieuweTaak = Taak.builder()
                .type(type)
                .omschrijving(omschrijving)
                .duurtijd(duurtijd)
                .build();

        taakRepo.startTransaction();
        try {
            taakRepo.insert(nieuweTaak);
            taakRepo.commitTransaction();
        } catch (RuntimeException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void updateTaak(long id, String type, String omschrijving, int duurtijd) throws TaakException {
        taakRepo.startTransaction();
        try {
            Taak taak = taakRepo.get(id);

            if (taak == null) {
                throw new IllegalArgumentException("Taak niet gevonden.");
            }

            taak.update(type, omschrijving, duurtijd);
            taakRepo.commitTransaction();
        } catch (TaakException | RuntimeException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
        }
    }

    public void deleteTaak(long id) {
        taakRepo.startTransaction();
        try {
            Taak taak = taakRepo.get(id);

            if (taak == null) {
                throw new IllegalArgumentException("Taak niet gevonden.");
            }

            taakRepo.delete(taak);
            taakRepo.commitTransaction();
        } catch (RuntimeException ex) {
            taakRepo.rollbackTransaction();
            throw ex;
        }
    }

    public List<String> getAllTaakTypes() {
        return taakRepo.findAllTaakTypes();
    }
}