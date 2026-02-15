package gui;

import domein.Site;
import domein.SiteController;
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

public class SiteOverviewController {

    @FXML private TableView<Site> siteTable;

    @FXML private TableColumn<Site, Long> idCol;
    @FXML private TableColumn<Site, String> naamCol;
    @FXML private TableColumn<Site, String> locatieCol;
    @FXML private TableColumn<Site, Integer> capaciteitCol;
    @FXML private TableColumn<Site, Site.OperationeleStatus> operationeleCol;
    @FXML private TableColumn<Site, Site.ProductieStatus> productieCol;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;


    // ObservableList maakt wijzigingen in deze lijst "observeerbaar"
    private final ObservableList<Site> sites = FXCollections.observableArrayList();

    private final SiteController sc;

    public SiteOverviewController(SiteController sc){
        this.sc = sc;
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSiteId()));
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNaam()));
        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocatie()));
        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCapaciteit()));
        operationeleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getOperationeleStatus()));
        productieCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getProductieStatus()));

        // sites ophalen uit DB
        sites.addAll(sc.getAllSites());

        siteTable.setItems(sites); // ObservableList linken

        editBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteFormView.fxml"));
            loader.setControllerFactory(type -> {
                if (type == SiteFormController.class) return new SiteFormController(sc);
                try {
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

    @FXML private void onEdit() { System.out.println("Edit"); }
    @FXML private void onDelete() { System.out.println("Delete"); }
}