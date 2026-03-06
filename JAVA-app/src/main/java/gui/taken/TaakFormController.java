package gui.taken;

import dto.TaakDTO;
import exception.TaakException;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;
import util.TaakType;

import java.util.stream.IntStream;

public class TaakFormController implements NavigableController{
    @FXML
    private ComboBox<TaakType> typeBx;
    @FXML private TextArea omschrijvingTxt;
    @FXML private ComboBox<Integer> duurtijdBx;

    @FXML private Label typeErrorLbl;
    @FXML private Label omschrijvingErrorLbl;
    @FXML private Label formErrorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Navigator navigator;
    private AppContext ctx;

    private ObservableTaken observableTaken;
    private Long editingId = null; // null = nieuw, anders edit

    public void setContext(AppContext ctx) {
        this.observableTaken = ctx.getObservableTaken();
        // ctx niet nodig dus ik zet hem niet expliciet, maar de setter wordt wel aangeroepen
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
    }

    public void loadForEdit(TaakDTO dto) {
        this.editingId = dto.taakId();

       // typeBx.setValue(dto.taakType());
        omschrijvingTxt.setText(dto.omschrijving());
        duurtijdBx.setValue(dto.duurtijd());
    }

    @FXML
    private void onSave() {
        clearErrors();

        try {
           // String type = typeBx.getValue();
            String omschrijving = omschrijvingTxt.getText();
            Integer duurtijd = duurtijdBx.getValue();

            if (editingId == null) {
                observableTaken.addTaak("TIJDELIJK", omschrijving, duurtijd);
            } else {
                observableTaken.editTaak(editingId, "TIJDELIJK", omschrijving, duurtijd);
            }
            close();
        } catch (TaakException ex) {
            showValidationErrors(ex);
        } catch (IllegalArgumentException ex) {
            formErrorLbl.setText(ex.getMessage());
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

    private void showValidationErrors(TaakException ex) {
        clearErrors();

        ex.getExceptionMap().forEach((field, iae) -> {
            String msg = iae.getMessage();

            switch (field) {
                case "taakType" -> {
                    typeErrorLbl.setText(msg);
                    typeBx.getStyleClass().add("field-error");
                }
                case "omschrijving" -> {
                    omschrijvingErrorLbl.setText(msg);
                    omschrijvingTxt.getStyleClass().add("field-error");
                }
                default -> {
                    if (formErrorLbl != null) formErrorLbl.setText(msg);
                }
            }
        });
    }

    private void clearErrors() {
        typeErrorLbl.setText("");
        omschrijvingErrorLbl.setText("");
        if (formErrorLbl != null) formErrorLbl.setText("");

        typeBx.getStyleClass().remove("field-error");
        omschrijvingTxt.getStyleClass().remove("field-error");
        duurtijdBx.getStyleClass().remove("field-error");

    }
}
