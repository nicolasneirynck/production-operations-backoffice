package gui.taken;

import dto.TaakDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.List;

public class ObservableTaken {
    private final ObservableList<TaakDTO> observableTaakList;
    @Getter private final FilteredList<TaakDTO> filteredTaakList;

    public ObservableTaken() {
        this.observableTaakList = FXCollections.observableArrayList();
        this.filteredTaakList = new FilteredList<>(observableTaakList, t -> true);
    }

    public void setTaken(List<TaakDTO> taken) {
        observableTaakList.setAll(taken);
    }

    public void removeTaak(long id) {
        observableTaakList.removeIf(t -> t.taakId() == id);
    }
}
