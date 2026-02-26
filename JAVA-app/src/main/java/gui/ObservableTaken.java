package gui;

import domein.TaakController;
import dto.TaakDTO;
import exception.TaakException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import util.TaakType;

public class ObservableTaken {
    private final TaakController controller;
    private final ObservableList<TaakDTO> observableTaakList;
    @Getter
    private final FilteredList<TaakDTO> filteredTaakList;

    public ObservableTaken(TaakController controller){
        this.controller = controller;
        this.observableTaakList = FXCollections.observableArrayList(); // maak observable list aan
        observableTaakList.addAll(controller.getAllTaken());
        this.filteredTaakList = new FilteredList<>(observableTaakList,t -> true);
    }

    public void addTaak(TaakType type, String omschrijving, Integer duurtijd) throws TaakException {
        controller.addTaak(type,omschrijving,duurtijd);
        reload();
    }

    public void editTaak(long id,TaakType type, String omschrijving, Integer duurtijd) throws TaakException{
        controller.updateTaak(id, type, omschrijving, duurtijd);
        reload();
       // return updated;
    }

    public void deleteTaak(long id){
        controller.deleteTaak(id);
        observableTaakList.removeIf(t -> t.taakId() == id);
    }

    // voor expliciet syncen met DB
    public void reload() {
        observableTaakList.setAll(controller.getAllTaken());
    }

    private int indexOf(long id) {
        for (int i = 0; i < observableTaakList.size(); i++) {
            if (observableTaakList.get(i).taakId() == id) return i;
        }
        return -1;
    }
}
