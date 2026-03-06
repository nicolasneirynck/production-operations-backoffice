package gui.sites;

import dto.SiteDTO;
import gui.LayoutController;
import gui.factories.ActionColumnFactory;
import gui.navigation.FormLoader;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
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

public class SiteOverviewController implements NavigableController {

    @FXML private VBox formHost;
    @FXML private Button addBtn;

    @FXML private TableView<SiteDTO> siteTable;
    @FXML private TableColumn<SiteDTO, String> naamCol;
    @FXML private TableColumn<SiteDTO, String> locatieCol;
    @FXML private TableColumn<SiteDTO, Integer> capaciteitCol;
    @FXML private TableColumn<SiteDTO, OperationeleStatus> operationeleCol;
    @FXML private TableColumn<SiteDTO, ProductieStatus> productieCol;
    @FXML private TableColumn<SiteDTO, SiteDTO> actiesCol;

    private AppContext context;
    @Setter private LayoutController layout;
    @Setter private Navigator navigator;
    private ObservableSites observableSites;

    private SortedList<SiteDTO> sortedList;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableSites = ctx.getObservableSites();
    }

    // TODO -> badge-factory maken?
    @FXML
    private void initialize() {
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        locatieCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().locatie().volledigeLocatie()));
        capaciteitCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().capaciteit()));

        configureStatusColumns();
        ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::showEditForm, this::deleteSite);

        naamCol.setStyle("-fx-alignment: center-left;");
        locatieCol.setStyle("-fx-alignment: center-left;");
        capaciteitCol.setStyle("-fx-alignment: CENTER;");
        operationeleCol.setStyle("-fx-alignment: CENTER;");
        productieCol.setStyle("-fx-alignment: CENTER;");
        actiesCol.setStyle("-fx-alignment: CENTER;");
        siteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        siteTable.setFixedCellSize(44); // rijhoogte
        siteTable.setSelectionModel(null);


        //addBtn.disableProperty().bind(formHost.visibleProperty());
        addBtn.managedProperty().bind(formHost.visibleProperty().not());
        addBtn.visibleProperty().bind(formHost.visibleProperty().not());
    }

    private void configureStatusColumns() {

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

    @Override
    public void loadData() {
        observableSites.reload();

        if (sortedList == null) {
            sortedList = new SortedList<>(observableSites.getFilteredSiteList());
            sortedList.comparatorProperty().bind(siteTable.comparatorProperty());
            siteTable.setItems(sortedList);
        }
    }

    @FXML
    private void onAdd() {
        FormLoader.showForm(
                formHost,
                context,
                View.SITES_FORM.fxml,
                SiteFormController::loadForCreate
        );
    }

    private void showEditForm(SiteDTO site) {
        FormLoader.showForm(
                formHost,
                context,
                View.SITES_FORM.fxml,
                (SiteFormController controller) -> controller.loadForEdit(site)
        );
    }

    private void deleteSite(SiteDTO site) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Site verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze site wil verwijderen?");
        alert.setContentText(site.naam() + " (" + site.locatie().volledigeLocatie() + ")");

        ButtonType delete = new ButtonType("Verwijderen");
        ButtonType cancel = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(delete, cancel);

        alert.showAndWait().ifPresent(choice -> {
            if (choice == delete) {
                try {
                    observableSites.deleteSite(site.siteId()); // moet bestaan
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

