package gui;

import exception.SiteException;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteFormController implements NavigableController {

    @FXML private VBox root;

    @FXML private TextField naamTf;
    @FXML private TextField capaciteitTf;

    @FXML private ComboBox<OperationeleStatus> operationeelCb;
    @FXML private ComboBox<ProductieStatus> productieCb;

    @FXML private TextField straatTf;
    @FXML private TextField nummerTf;
    @FXML private TextField postcodeTf;
    @FXML private TextField stadTf;
    @FXML private TextField landTf;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Navigator navigator;
    private AppContext context;
    private ObservableSites observableSites;

    @Setter private Runnable onClose;

    @Override
    public void setContext(AppContext ctx) {
        context = ctx;
        this.observableSites = ctx.getObservableSites();
        this.observableSites.reload();
    }

    @FXML
    private void initialize() {
        operationeelCb.setItems(FXCollections.observableArrayList(OperationeleStatus.values()));
        productieCb.setItems(FXCollections.observableArrayList(ProductieStatus.values()));

        operationeelCb.getSelectionModel().select(OperationeleStatus.ACTIEF);
        productieCb.getSelectionModel().select(ProductieStatus.GEZOND);
    }

    public void loadForCreate() {
        naamTf.clear();
        capaciteitTf.clear();
        straatTf.clear();
        nummerTf.clear();
        postcodeTf.clear();
        stadTf.clear();
        if (landTf.getText() == null || landTf.getText().isBlank()) landTf.setText("België");

        operationeelCb.getSelectionModel().select(OperationeleStatus.ACTIEF);
        productieCb.getSelectionModel().select(ProductieStatus.GEZOND);
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        if (onClose != null) onClose.run();
    }

    @FXML
    private void onSave() {
        try {
            String naam = naamTf.getText();
            String straat = straatTf.getText();
            String nummer = nummerTf.getText();
            String postcode = postcodeTf.getText();
            String stad = stadTf.getText();
            String land = landTf.getText();

            Integer capaciteit = parseCapaciteit(capaciteitTf.getText());
            OperationeleStatus op = operationeelCb.getValue();
            ProductieStatus prod = productieCb.getValue();

            observableSites.addSite(
                    naam, straat, nummer, postcode, stad, land,
                    capaciteit, op, prod
            );

            observableSites.reload();
            close();

        } catch (SiteException ex) {
            new Alert(Alert.AlertType.ERROR, "Ongeldige invoer. Controleer de velden.").showAndWait();
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
        }
    }

    private Integer parseCapaciteit(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Capaciteit moet een getal zijn.");
        }
    }

}