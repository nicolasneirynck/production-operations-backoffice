package gui;

import dto.TaakDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;
import util.TaakType;

import java.util.stream.IntStream;

public class TaakFormController {
    @FXML
    private ComboBox<TaakType> typeBx;
    @FXML private TextField omschrijvingTxt;
    @FXML private ComboBox<Integer> duurtijdBx;

    @FXML private Label errorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private final ObservableTaken observableTaken;
    private Long editingId = null; // null = nieuw, anders edit

    public TaakFormController(ObservableTaken observableTaken){
        this.observableTaken = observableTaken;
    }

    @FXML
    private void initialize() {
        typeBx.setItems(FXCollections.observableArrayList(TaakType.values()));
        duurtijdBx.setItems(FXCollections.observableArrayList(
                        IntStream.rangeClosed(1, 16)
                                .map(i -> i * 15)
                                .boxed() //int naar Stream<Integer>
                                .toList()));

        duurtijdBx.setValue(15);
        errorLbl.setText("");
    }

    public void loadForEdit(TaakDTO dto) {
        this.editingId = dto.taakId();

        typeBx.setValue(dto.taakType());
        omschrijvingTxt.setText(dto.omschrijving());
        duurtijdBx.setValue(dto.duurtijd());
    }

    @FXML
    private void onSave() {
        try {
            TaakType type = typeBx.getValue();
            String omschrijving = omschrijvingTxt.getText();
            int duurtijd = duurtijdBx.getValue();

            if (editingId == null) {
                observableTaken.addTaak(type, omschrijving, duurtijd);
            } else {
                observableTaken.editTaak(editingId, type, omschrijving, duurtijd);
            }
            close();
        } catch (IllegalArgumentException ex) {
            errorLbl.setText(ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Annuleren");
        alert.setHeaderText("Wijzigingen annuleren?");
        alert.setContentText("Niet-opgeslagen wijzigingen gaan verloren.");

        ButtonType yesBtn = new ButtonType("Ja");
        ButtonType noBtn = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                close();
            }
        });
    }

    private void close() {
        Stage stage = (Stage) omschrijvingTxt.getScene().getWindow();
        stage.close();
    }
}
