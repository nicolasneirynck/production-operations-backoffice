package gui;

import domein.Site;
import domein.SiteController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SiteFormController {

    @FXML private TextField naamTxt;
    @FXML private TextField locatieTxt;
    @FXML private TextField capaciteitTxt;

    @FXML private ComboBox<Site.OperationeleStatus> operationeleBx;
    @FXML private ComboBox<Site.ProductieStatus> productieBx;

    @FXML private Label errorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private SiteController sc;

    public SiteFormController(SiteController sc){
        this.sc = sc;
    }

    @FXML
    private void initialize() {
        operationeleBx.setItems(FXCollections.observableArrayList(Site.OperationeleStatus.values()));
        productieBx.setItems(FXCollections.observableArrayList(Site.ProductieStatus.values()));

        operationeleBx.setValue(Site.OperationeleStatus.ACTIEF);
        productieBx.setValue(Site.ProductieStatus.GEZOND);

        operationeleBx.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == Site.OperationeleStatus.NON_ACTIEF) {
                productieBx.setValue(Site.ProductieStatus.OFFLINE);
                productieBx.setDisable(true);
            } else {
                productieBx.setDisable(false);
            }
        });

        errorLbl.setText("");
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

            Site.OperationeleStatus op = operationeleBx.getValue();
            Site.ProductieStatus prod = productieBx.getValue();

            sc.addSite(naam,locatie,capaciteit,op,prod);

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
