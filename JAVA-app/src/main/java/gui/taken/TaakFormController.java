package gui.taken;

import dto.TaakDTO;
import exception.ValidationException;
import gui.navigation.ClosableFormGuard;
import gui.navigation.FormController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;

import java.util.stream.IntStream;

public class TaakFormController implements FormController, ClosableFormGuard {

    @FXML private VBox root;
    @FXML private Label titleLabel;

    @FXML private ComboBox<String> typeBx;
    @FXML private ComboBox<Integer> duurtijdBx;
    @FXML private TextArea omschrijvingTxt;

    @FXML private Label typeErrorLbl;
    @FXML private Label omschrijvingErrorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Runnable onClose;

    private AppContext ctx;
    private ObservableTaken observableTaken;

    private Long editingId = null;

    public void setContext(AppContext ctx) {
        this.ctx = ctx;
        this.observableTaken = ctx.getObservableTaken();
    }

    @FXML
    private void initialize() {
        duurtijdBx.setItems(FXCollections.observableArrayList(
                        IntStream.rangeClosed(1, 16)
                                .map(i -> i * 15)
                                .boxed() //int naar Stream<Integer>
                                .toList()));

        duurtijdBx.setValue(15);

        typeBx.valueProperty().addListener((o,a,b) -> clearError("taakType"));
        omschrijvingTxt.textProperty().addListener((o, a, b) -> clearError("omschrijving"));

    }

    public void loadData() {
        observableTaken.reload();

        typeBx.setItems(FXCollections.observableArrayList(
                ctx.getTaakController().getAllTaakTypes()
        ));
    }

    public void loadForCreate() {
        editingId = null;

        titleLabel.setText("Taak aanmaken");
        typeBx.setValue(null);
        duurtijdBx.setValue(15);
        omschrijvingTxt.clear();

        clearErrors();
    }

    public void loadForEdit(TaakDTO dto) {
        this.editingId = dto.taakId();
        titleLabel.setText("Taak wijzigen");

        typeBx.setValue(dto.taakType());
        duurtijdBx.setValue(dto.duurtijd());
        omschrijvingTxt.setText(dto.omschrijving());

        clearErrors();
    }

    @FXML
    private void onSave() {

        clearErrors();

        try {
            String type = typeBx.isEditable() ? typeBx.getEditor().getText() : typeBx.getValue();
            String omschrijving = omschrijvingTxt.getText();
            Integer duurtijd = duurtijdBx.getValue();

            if (editingId == null) {
                observableTaken.addTaak(type, omschrijving, duurtijd);
            } else {
                observableTaken.editTaak(editingId, type, omschrijving, duurtijd);
            }

            observableTaken.reload();
            close();
        } catch (ValidationException ex) {
            showValidationErrors(ex);
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
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
        if (onClose != null) onClose.run();
    }


    private void showValidationErrors(ValidationException ex) {
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
                default -> {new Alert(Alert.AlertType.ERROR, msg).showAndWait();}
            }
        });
    }

    private void clearError(String key) {
        switch (key) {
            case "taakType" -> {
                typeErrorLbl.setText("");
                typeBx.getStyleClass().remove("field-error");
            }
            case "omschrijving" -> {
                omschrijvingErrorLbl.setText("");
                omschrijvingErrorLbl.getStyleClass().remove("field-error");
            }
        }
    }

    private void clearErrors() {
        typeErrorLbl.setText("");
        omschrijvingErrorLbl.setText("");

        typeBx.getStyleClass().remove("field-error");
        omschrijvingTxt.getStyleClass().remove("field-error");
        duurtijdBx.getStyleClass().remove("field-error");

    }

    @Override
    public boolean canClose() {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Formulier sluiten");
        alert.setHeaderText("Niet-opgeslagen wijzigingen");
        alert.setContentText("Mogelijke wijzigen werden niet opgeslaan. Wil je de pagina verlaten?");

        ButtonType ja = new ButtonType("Ja");
        ButtonType nee = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ja, nee);

        return alert.showAndWait().orElse(nee) == ja;
    }
}
