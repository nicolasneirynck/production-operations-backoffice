package gui;

import domein.TaakController;
import dto.TaakDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import util.TaakType;

public class ObservableTaken {
    private final TaakController controller;
    private final ObservableList<TaakDTO> taakObservableList;
    @Getter
    private final FilteredList<TaakDTO> filteredTaakList;

    public ObservableTaken(TaakController controller){
        this.controller = controller;
        this.taakObservableList = FXCollections.observableArrayList(); // maak observable list aan
        taakObservableList.addAll(controller.getAllTaken());
        this.filteredTaakList = new FilteredList<>(taakObservableList,t -> true);
    }

    // eventueel hier TaakDTO teruggeven?
    public void addTaak(TaakType type, String omschrijving, int duurtijd){
        TaakDTO t = controller.addTaak(type,omschrijving,duurtijd);
        taakObservableList.add(t);
    }

    public TaakDTO editTaak(long id,TaakType type, String omschrijving, int duurtijd){
        TaakDTO updated = controller.updateTaak(id, type, omschrijving, duurtijd);
        int idx = indexOf(id);
        if (idx >= 0) taakObservableList.set(idx, updated);
        return updated;
    }

    public void deleteTaak(long id){
        controller.deleteTaak(id);
        taakObservableList.removeIf(t -> t.taakId() == id);
    }

    private int indexOf(long id) {
        for (int i = 0; i < taakObservableList.size(); i++) {
            if (taakObservableList.get(i).taakId() == id) return i;
        }
        return -1;
    }
}
