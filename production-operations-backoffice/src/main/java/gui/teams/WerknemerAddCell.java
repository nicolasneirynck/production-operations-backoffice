package gui.teams;

import dto.GebruikerDTO;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

import java.util.Comparator;

public class WerknemerAddCell extends TableCell<GebruikerDTO, GebruikerDTO> {

    private final ObservableList<GebruikerDTO> geselecteerdeWerknemers;
    private final Button addBtn = new Button("+");

    public WerknemerAddCell(ObservableList<GebruikerDTO> geselecteerdeWerknemers) {
        this.geselecteerdeWerknemers = geselecteerdeWerknemers;

        addBtn.getStyleClass().add("icon-button");
        addBtn.setStyle("""
                -fx-background-color: #5F86F6;
                -fx-text-fill: white;
                -fx-background-radius: 999;
                -fx-min-width: 28;
                -fx-min-height: 28;
                -fx-max-width: 28;
                -fx-max-height: 28;
                -fx-font-weight: bold;
                """);

        setAlignment(Pos.CENTER);

        addBtn.setOnAction(e -> {
            GebruikerDTO medewerker = getItem();
            if (medewerker != null && !geselecteerdeWerknemers.contains(medewerker)) {
                geselecteerdeWerknemers.add(medewerker);
                geselecteerdeWerknemers.sort(Comparator
                        .comparing(GebruikerDTO::naam, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(GebruikerDTO::voornaam, String.CASE_INSENSITIVE_ORDER));
            }
        });
    }

    @Override
    protected void updateItem(GebruikerDTO item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        addBtn.setDisable(geselecteerdeWerknemers.contains(item));
        setGraphic(addBtn);
    }
}