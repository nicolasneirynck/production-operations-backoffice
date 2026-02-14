package gui;

import domein.Site;
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

    @FXML private TextField filterField;

    // ObservableList maakt wijzigingen in deze lijst "observeerbaar"
    private final ObservableList<Site> sites = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSiteId()));
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNaam()));
        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocatie()));
        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCapaciteit()));
        operationeleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getOperationeleStatus()));
        productieCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getProductieStatus()));

        // dummy data (tijdelijk)
        sites.add(new Site("Gent", "België", 100, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.GEZOND));
        sites.add(new Site("Antwerpen", "België", 80, Site.OperationeleStatus.ACTIEF, Site.ProductieStatus.PROBLEMEN));
        sites.add(new Site("Brugge", "België", 60, Site.OperationeleStatus.NON_ACTIEF, Site.ProductieStatus.OFFLINE));

        siteTable.setItems(sites); // ObservableList linken

        editBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML private void onAdd() { try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteFormView.fxml"));
        Parent root = loader.load();

        SiteFormController controller = loader.getController();

        Stage dialog = new Stage();
        dialog.setTitle("Nieuwe Site");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();

        Site newSite = controller.getResult();
        if (newSite != null) {
            sites.add(newSite); // TODO -> later via siteController.add(...)
        }
    } catch (Exception e) {
        e.printStackTrace();
    } }
    @FXML private void onEdit() { System.out.println("Edit"); }
    @FXML private void onDelete() { System.out.println("Delete"); }
}