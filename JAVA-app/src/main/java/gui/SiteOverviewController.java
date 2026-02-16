package gui;

import domein.SiteController;
import dto.SiteDTO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteOverviewController {

    @FXML private TableView<SiteDTO> siteTable;

    @FXML private TableColumn<SiteDTO, Long> idCol;
    @FXML private TableColumn<SiteDTO, String> naamCol;
    @FXML private TableColumn<SiteDTO, String> locatieCol;
    @FXML private TableColumn<SiteDTO, Integer> capaciteitCol;
    @FXML private TableColumn<SiteDTO, OperationeleStatus> operationeleCol;
    @FXML private TableColumn<SiteDTO, ProductieStatus> productieCol;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;


    // ObservableList maakt wijzigingen in deze lijst "observeerbaar"
    private final ObservableList<SiteDTO> sites = FXCollections.observableArrayList();

    private final SiteController sc;

    public SiteOverviewController(SiteController sc){
        this.sc = sc;
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().siteId()));
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().locatie()));
        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().capaciteit()));
        operationeleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().operationeleStatus()));
        productieCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().productieStatus()));

        // sites ophalen uit DB
        sites.addAll(sc.getAllSites());
        siteTable.setItems(sites); // ObservableList linken

        editBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onAdd() {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteFormView.fxml"));
            loader.setControllerFactory(type ->
            {
                if (type == SiteFormController.class)
                    return new SiteFormController(sc);
                try
                {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Nieuwe Site");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            sites.setAll(sc.getAllSites());

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onEdit() {
        SiteDTO selected = siteTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteFormView.fxml"));
            loader.setControllerFactory(type -> {
                if (type == SiteFormController.class) return new SiteFormController(sc);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });

            Parent root = loader.load();
            SiteFormController form = loader.getController();
            form.loadForEdit(selected);

            Stage dialog = new Stage();
            dialog.setTitle("Site wijzigen");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            sites.setAll(sc.getAllSites());

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onDelete() {
        SiteDTO selected = siteTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Site verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze site wil verwijderen?");
        alert.setContentText(selected.naam() + " (" + selected.locatie() + ")");

        ButtonType deleteBtn = new ButtonType("Verwijderen");
        ButtonType cancelBtn = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == deleteBtn) {
                try {
                    sc.deleteSite(selected.siteId());
                    sites.setAll(sc.getAllSites()); // refresh table
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

    private void loadFxml(String resource){

    }
}