package gui;

import dto.SiteDTO;
import exception.SiteException;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteFormController implements NavigableController {

    @FXML private TextField naamTxt;
    @FXML private TextField locatieTxt;
    @FXML private TextField capaciteitTxt;

    @FXML private ComboBox<OperationeleStatus> operationeleBx;
    @FXML private ComboBox<ProductieStatus> productieBx;

    @FXML private Label naamErrorLbl;
    @FXML private Label locatieErrorLbl;
    @FXML private Label capaciteitErrorLbl;
    @FXML private Label operationeleErrorLbl;
    @FXML private Label productieErrorLbl;
    @FXML private Label formErrorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Navigator navigator;

    private ObservableSites observableSites;
    private Long editingId = null;

    public void setContext(AppContext ctx) {
        this.observableSites = ctx.getObservableSites();
    }

    @FXML
    private void initialize() {
        operationeleBx.setItems(FXCollections.observableArrayList(OperationeleStatus.values()));
        productieBx.setItems(FXCollections.observableArrayList(ProductieStatus.values()));

        operationeleBx.setValue(OperationeleStatus.ACTIEF);
        productieBx.setValue(ProductieStatus.GEZOND);

        operationeleBx.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == OperationeleStatus.NON_ACTIEF) {
                productieBx.setValue(ProductieStatus.OFFLINE);
                productieBx.setDisable(true);
            } else {
                productieBx.setDisable(false);
            }
        });

        clearErrors();
    }

    public void loadForEdit(SiteDTO dto) {
        this.editingId = dto.siteId();

        naamTxt.setText(dto.naam());
        locatieTxt.setText(dto.locatie());
        capaciteitTxt.setText(String.valueOf(dto.capaciteit()));
        operationeleBx.setValue(dto.operationeleStatus());
        productieBx.setValue(dto.productieStatus());
    }

    @FXML
    private void onSave() {
//        clearErrors();
//
//        try {
//            String naam = naamTxt.getText();
//            String locatie = locatieTxt.getText();
//            Integer capaciteit = parseCapaciteitNullable(capaciteitTxt.getText());
//
//            OperationeleStatus op = operationeleBx.getValue();
//            ProductieStatus prod = productieBx.getValue();
//
//            if (editingId == null) {
//                //observableSites.addSite(naam, locatie, capaciteit, op, prod);
//            } else {
//                observableSites.editSite(editingId, naam, locatie, capaciteit, op, prod);
//            }
//            close();
//
//        } catch (SiteException ex) {
//            showValidationErrors(ex);
//        } catch (IllegalArgumentException ex) {
//            formErrorLbl.setText(ex.getMessage());
//        }
    }

    // GUI check
    private Integer parseCapaciteitNullable(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty()) return null;

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            capaciteitErrorLbl.setText("Capaciteit moet een getal zijn.");
            capaciteitTxt.getStyleClass().add("field-error");
            throw new IllegalArgumentException("Capaciteit moet een getal zijn.");
        }
    }

    private void showValidationErrors(SiteException ex) {
        clearErrors();

        ex.getExceptionMap().forEach((field, iae) -> {
            String msg = iae.getMessage();

            switch (field) {
                case "naam" -> {
                    naamErrorLbl.setText(msg);
                    naamTxt.getStyleClass().add("field-error");
                }
                case "locatie" -> {
                    locatieErrorLbl.setText(msg);
                    locatieTxt.getStyleClass().add("field-error");
                }
                case "capaciteit" -> {
                    capaciteitErrorLbl.setText(msg);
                    capaciteitTxt.getStyleClass().add("field-error");
                }
                case "operationeleStatus" -> {
                    operationeleErrorLbl.setText(msg);
                    operationeleBx.getStyleClass().add("field-error");
                }
                case "productieStatus" -> {
                    productieErrorLbl.setText(msg);
                    productieBx.getStyleClass().add("field-error");
                }
                default -> formErrorLbl.setText(msg);
            }
        });
    }

    private void clearErrors() {
        naamErrorLbl.setText("");
        locatieErrorLbl.setText("");
        capaciteitErrorLbl.setText("");
        operationeleErrorLbl.setText("");
        productieErrorLbl.setText("");
        formErrorLbl.setText("");

        naamTxt.getStyleClass().remove("field-error");
        locatieTxt.getStyleClass().remove("field-error");
        capaciteitTxt.getStyleClass().remove("field-error");
        operationeleBx.getStyleClass().remove("field-error");
        productieBx.getStyleClass().remove("field-error");
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
            if (response == yesBtn) close();
        });
    }

    private void close() {
        Stage stage = (Stage) naamTxt.getScene().getWindow();
        stage.close();
    }
}