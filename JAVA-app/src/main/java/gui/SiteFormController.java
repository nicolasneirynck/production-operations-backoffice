package gui;

import domein.SiteController;
import dto.SiteDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteFormController {

    @FXML private TextField naamTxt;
    @FXML private TextField locatieTxt;
    @FXML private TextField capaciteitTxt;

    @FXML private ComboBox<OperationeleStatus> operationeleBx;
    @FXML private ComboBox<ProductieStatus> productieBx;

    @FXML private Label errorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private final SiteController sc;
    private Long editingId = null; // null = nieuw, anders edit


    public SiteFormController(SiteController sc){
        this.sc = sc;
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

        errorLbl.setText("");
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
        try {
            String naam = naamTxt.getText();
            String locatie = locatieTxt.getText();

            int capaciteit;
            try {
                capaciteit = Integer.parseInt(capaciteitTxt.getText().trim());
            } catch (Exception e) {
                throw new IllegalArgumentException("Capaciteit moet een getal zijn.");
            }

            OperationeleStatus op = operationeleBx.getValue();
            ProductieStatus prod = productieBx.getValue();

            if (editingId == null) {
                sc.addSite(naam, locatie, capaciteit, op, prod);
            } else {
                sc.updateSite(editingId, naam, locatie, capaciteit, op, prod);
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
        Stage stage = (Stage) naamTxt.getScene().getWindow();
        stage.close();
    }
}
