package gui.gebruikers;

import dto.GebruikerDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.List;

public class ObservableGebruikers {

    private final ObservableList<GebruikerDTO> observableGebruikerList;
    @Getter private final FilteredList<GebruikerDTO> filteredGebruikerList;

    public ObservableGebruikers() {
        this.observableGebruikerList = FXCollections.observableArrayList();
        this.filteredGebruikerList = new FilteredList<>(observableGebruikerList, gebruiker -> true);
    }

    public void setGebruikers(List<GebruikerDTO> gebruikers) {
        observableGebruikerList.setAll(gebruikers);
    }

    public void removeGebruiker(long id) {
        observableGebruikerList.removeIf(gebruiker -> gebruiker.gebruikerId() == id);
    }
}
