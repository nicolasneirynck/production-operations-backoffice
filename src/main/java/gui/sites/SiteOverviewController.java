package gui.sites;

import domein.services.SiteService;
import dto.GebruikerDTO;
import dto.SiteDTO;
import gui.factories.ActionColumnFactory;
import gui.navigation.*;
import util.View;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.OperationeleStatus;
import util.ProductieStatus;

public class SiteOverviewController implements NavigableController, NavigationGuard {
    @FXML private Button addBtn;
    @FXML private VBox formHost;

    @FXML private TableView<SiteDTO> siteTable;
    @FXML private TableColumn<SiteDTO, String> naamCol;
    @FXML private TableColumn<SiteDTO, String> verantwoordelijkeCol;
    @FXML private TableColumn<SiteDTO, String> locatieCol;
    @FXML private TableColumn<SiteDTO, Integer> capaciteitCol;
    @FXML private TableColumn<SiteDTO, OperationeleStatus> operationeleCol;
    @FXML private TableColumn<SiteDTO, ProductieStatus> productieCol;
    @FXML private TableColumn<SiteDTO, SiteDTO> actiesCol;

    @Setter private Navigator navigator;
    private ClosableFormGuard activeFormGuard;

    private AppContext context;
    private SiteService siteService;
    private ObservableSites observableSites;
    private SortedList<SiteDTO> sortedList;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.siteService = ctx.getSiteService();
        this.observableSites = ctx.getObservableSites();
    }

    @FXML
    private void initialize() {
        configureColumns();
        configureStatusColumns();
        configureActiesColumn();
        configureTableLayout();

        addBtn.managedProperty().bind(formHost.visibleProperty().not());
        addBtn.visibleProperty().bind(formHost.visibleProperty().not());
    }

    protected void configureColumns() {

        naamCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().naam()));
        locatieCol.setCellValueFactory(c -> {
            SiteDTO site = c.getValue();
            String locatie = site.locatie() == null ? "-" : site.locatie().volledigeLocatie();
            return new SimpleStringProperty(locatie);
        });
        verantwoordelijkeCol.setCellValueFactory(c -> {
            GebruikerDTO verantwoordelijke = c.getValue().verantwoordelijke();
            return new SimpleStringProperty(
                    verantwoordelijke == null ? "-" : verantwoordelijke.naam().toUpperCase() + " " + verantwoordelijke.voornaam()
            );
        });
        capaciteitCol.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().capaciteit()));


    }

    private void configureStatusColumns() {
        configureOperationeleColumn();
        configureProductieColumn();
    }

    private void configureOperationeleColumn() {
        operationeleCol.setCellValueFactory(
                c -> new SimpleObjectProperty<>(c.getValue().operationeleStatus())
        );

        operationeleCol.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();

            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(OperationeleStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(status.toString());
                label.getStyleClass().setAll("status-badge");

                switch (status) {
                    case ACTIEF -> label.getStyleClass().add("status-green");
                    case NON_ACTIEF -> label.getStyleClass().add("status-red");
                }

                setGraphic(label);
            }
        });
    }

    private void configureProductieColumn() {
        productieCol.setCellValueFactory(
                c -> new SimpleObjectProperty<>(c.getValue().productieStatus())
        );

        productieCol.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label();

            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(ProductieStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(status.toString());
                label.getStyleClass().setAll("status-badge");

                switch (status) {
                    case GEZOND -> label.getStyleClass().add("status-green");
                    case PROBLEMEN -> label.getStyleClass().add("status-yellow");
                    case OFFLINE -> label.getStyleClass().add("status-red");
                }

                setGraphic(label);
            }
        });
    }

    private void configureActiesColumn() {
        ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::showEditForm, this::deleteSite);
    }

    private void configureTableLayout() {
        naamCol.setStyle("-fx-alignment: center-left;");
        verantwoordelijkeCol.setStyle("-fx-alignment: CENTER;");
        locatieCol.setStyle("-fx-alignment: center-left;");
        capaciteitCol.setStyle("-fx-alignment: CENTER;");
        operationeleCol.setStyle("-fx-alignment: CENTER;");
        productieCol.setStyle("-fx-alignment: CENTER;");
        actiesCol.setStyle("-fx-alignment: CENTER;");

        siteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        siteTable.setFixedCellSize(44); // rijhoogte
        siteTable.setSelectionModel(null);
    }

    @Override
    public void loadData() {
        loadSites();
        initializeTableItems();
    }

    private void loadSites() {
        observableSites.setSites(siteService.getAllSites());
    }

    private void initializeTableItems() {
        if (sortedList == null) {
            sortedList = new SortedList<>(observableSites.getFilteredSiteList());

            // binding voor kolomsortering
            sortedList.comparatorProperty().bind(siteTable.comparatorProperty());
            siteTable.setItems(sortedList);

            // default sortering
            naamCol.setSortType(TableColumn.SortType.ASCENDING);
            siteTable.getSortOrder().add(naamCol);

            locatieCol.setSortable(false);
            operationeleCol.setSortable(false);
            productieCol.setSortable(false);
            actiesCol.setSortable(false);
        }
    }

    @FXML
    private void onAdd() {
        openCreateForm();
    }

    protected void openCreateForm() {
        SiteFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.SITES_FORM.fxml,
                SiteFormController::loadForCreate
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    private void showEditForm(SiteDTO site) {
        SiteFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.SITES_FORM.fxml,
                c -> c.loadForEdit(site)
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    private void closeForm() {
        FormLoader.hideForm(formHost);
        activeFormGuard = null;
    }

    @Override
    public boolean canNavigateAway() {
        return activeFormGuard == null || activeFormGuard.canClose();
    }


    private void deleteSite(SiteDTO site) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Site verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze site wil verwijderen?");
        String locatieTekst = site.locatie() == null ? "-" : site.locatie().volledigeLocatie();
        alert.setContentText(site.naam() + " (" + locatieTekst + ")");

        ButtonType delete = new ButtonType("Verwijderen");
        ButtonType cancel = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(delete, cancel);

        alert.showAndWait().ifPresent(choice -> {
            if (choice == delete) {
                try {
                    siteService.deleteSite(site.siteId());
                    observableSites.removeSite(site.siteId());
                    loadData();
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });

    }
}
