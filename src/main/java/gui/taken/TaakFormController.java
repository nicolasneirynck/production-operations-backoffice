package gui.taken;

import domein.services.TaakService;
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
    private TaakService taakService;
    private ObservableTaken observableTaken;

    private Long editingId = null;
    private TaakFormData initialFormData;

    @Override
    public void setContext(AppContext ctx) {
        this.ctx = ctx;
        this.taakService = ctx.getTaakService();
        this.observableTaken = ctx.getObservableTaken();
    }

    @FXML
    private void initialize() {
        configureDuurtijdComboBox();
        configureValidationListeners();
    }

    private void configureDuurtijdComboBox() {
        duurtijdBx.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 16)
                        .map(i -> i * 15)
                        .boxed()
                        .toList()));

        duurtijdBx.setValue(15);
    }

    private void configureValidationListeners() {
        typeBx.valueProperty().addListener((o, a, b) -> clearError("taakType"));
        if (typeBx.isEditable()) {
            typeBx.getEditor().textProperty().addListener((o, a, b) -> clearError("taakType"));
        }
        duurtijdBx.valueProperty().addListener((o, a, b) -> clearError("duurtijd"));
        omschrijvingTxt.textProperty().addListener((o, a, b) -> clearError("omschrijving"));
    }

    @Override
    public void loadData() {
        observableTaken.setTaken(taakService.getAllTaken());

        typeBx.setItems(FXCollections.observableArrayList(taakService.getAllTaakTypes()));
    }

    public void loadForCreate() {
        editingId = null;
        titleLabel.setText("Taak aanmaken");

        typeBx.setValue(null);
        duurtijdBx.setValue(15);
        omschrijvingTxt.clear();

        clearErrors();
        initialFormData = currentFormData();
    }

    public void loadForEdit(TaakDTO dto) {
        editingId = dto.taakId();
        titleLabel.setText("Taak wijzigen");

        typeBx.setValue(dto.taakType());
        duurtijdBx.setValue(dto.duurtijd());
        omschrijvingTxt.setText(dto.omschrijving());

        clearErrors();
        initialFormData = currentFormData();
    }

    @FXML
    private void onSave() {

        clearErrors();

        try {
            String type = typeBx.isEditable() ? typeBx.getEditor().getText() : typeBx.getValue();
            String omschrijving = omschrijvingTxt.getText();
            Integer duurtijd = duurtijdBx.getValue();

            if (editingId == null) {
                taakService.addTaak(type, omschrijving, duurtijd);
            } else {
                taakService.updateTaak(editingId, type, omschrijving, duurtijd);
            }
            observableTaken.setTaken(taakService.getAllTaken());

            close();
        } catch (ValidationException ex) {
            showValidationErrors(ex);
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        if (canClose()) {
            close();
        }
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
                case "duurtijd" -> {
                    duurtijdBx.getStyleClass().add("field-error");
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
                omschrijvingTxt.getStyleClass().remove("field-error");
            }
            case "duurtijd" -> {
                duurtijdBx.getStyleClass().remove("field-error");
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
        if (!hasUnsavedChanges()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Formulier sluiten");
        alert.setHeaderText("Niet-opgeslagen wijzigingen");
        alert.setContentText("Je wijzigingen werden niet opgeslagen. Ben je zeker dat je de pagina wil verlaten?");

        ButtonType ja = new ButtonType("Ja");
        ButtonType nee = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ja, nee);

        return alert.showAndWait().orElse(nee) == ja;
    }

    private TaakFormData currentFormData() {
        String type = typeBx.isEditable() ? typeBx.getEditor().getText() : typeBx.getValue();

        return new TaakFormData(
                type,
                duurtijdBx.getValue(),
                omschrijvingTxt.getText()
        );
    }

    private boolean hasUnsavedChanges() {
        return !currentFormData().equals(initialFormData);
    }

    private record TaakFormData(
            String taakType,
            Integer duurtijd,
            String omschrijving
    ) {
    }
}
