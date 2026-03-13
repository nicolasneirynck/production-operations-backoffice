package gui.taken;

import domein.controllers.TaakController;
import dto.TaakDTO;
import exception.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

public class ObservableTaken {
    private final TaakController controller;

    private final ObservableList<TaakDTO> observableTaakList;
    @Getter private final FilteredList<TaakDTO> filteredTaakList;

    public ObservableTaken(TaakController controller){
        this.controller = controller;
        this.observableTaakList = FXCollections.observableArrayList();
        this.filteredTaakList = new FilteredList<>(observableTaakList,t -> true);
    }

    public void addTaak(String type, String omschrijving, Integer duurtijd) throws ValidationException {
        controller.addTaak(type,omschrijving,duurtijd);
        reload();
    }

    public void updateTaak(long id,String type, String omschrijving, Integer duurtijd) throws ValidationException{
        controller.updateTaak(id, type, omschrijving, duurtijd);
        reload();
    }

    public void deleteTaak(long id){
        controller.deleteTaak(id);
        observableTaakList.removeIf(t -> t.taakId() == id);
        reload();
    }

    // voor expliciet syncen met DB
    public void reload() {
        observableTaakList.setAll(controller.getAllTaken());
    }
}
