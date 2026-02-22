package gui;

import domein.SiteController;
import dto.SiteDTO;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import gui.navigation.View;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteOverviewController implements NavigableController {

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
    @FXML private Button refreshBtn;

    @Setter private Navigator navigator;
   private AppContext context;
    private ObservableSites observableSites;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableSites = ctx.getObservableSites();
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().siteId()));
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().locatie()));
        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().capaciteit()));
        operationeleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().operationeleStatus()));
        productieCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().productieStatus()));

        // SortedList in GUI
        SortedList<SiteDTO> sortedList = new SortedList<>(observableSites.getFilteredSiteList());
        //binding voor kolomsortering
        sortedList.comparatorProperty().bind(siteTable.comparatorProperty());

        siteTable.setItems(sortedList);

        //default sortering
        idCol.setSortType(TableColumn.SortType.ASCENDING);
        siteTable.getSortOrder().add(idCol);
        siteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);// kolommen vullen automatisch de breedte

        editBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onAdd() {
        navigator.showDialog(View.SITES_FORM, "Site toevoegen",null);
    }

    @FXML
    private void onEdit() {
        SiteDTO selected = siteTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        navigator.showDialog(View.SITES_FORM, "Site wijzigen", controller -> {
                SiteFormController form = (SiteFormController) controller;
                form.loadForEdit(selected); // prefill
        });
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
                    observableSites.deleteSite(selected.siteId());
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

    @FXML
    private void onBack() {
        navigator.goTo(View.MAIN_MENU);
    }

    @FXML
    private void onRefresh() {
        observableSites.reload();
    }

}