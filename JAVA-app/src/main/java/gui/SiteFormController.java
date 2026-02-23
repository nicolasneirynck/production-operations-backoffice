package gui;

import domein.SiteController;
import dto.SiteDTO;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteFormController implements NavigableController{

    @FXML private TextField naamTxt;
    @FXML private TextField locatieTxt;
    @FXML private TextField capaciteitTxt;

    @FXML private ComboBox<OperationeleStatus> operationeleBx;
    @FXML private ComboBox<ProductieStatus> productieBx;

    @FXML private Label errorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Navigator navigator;
    private ObservableSites observableSites;
    private Long editingId = null; // null = nieuw, anders edit

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
                observableSites.addSite(naam, locatie, capaciteit, op, prod);
            } else {
                observableSites.editSite(editingId, naam, locatie, capaciteit, op, prod);
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

//    @FXML
//    private void onBack() {
//        navigator.goTo(View.SITES_OVERVIEW,null);
//    }


    private void close() {
        Stage stage = (Stage) naamTxt.getScene().getWindow();
        stage.close();
    }
}
