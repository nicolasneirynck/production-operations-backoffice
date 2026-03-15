package gui.gebruikers;

import dto.GebruikerDTO;
import gui.factories.ActionColumnFactory;
import gui.navigation.ClosableFormGuard;
import gui.navigation.FormLoader;
import gui.navigation.NavigationGuard;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import domein.services.GebruikerService;
import util.GebruikerStatus;
import util.Rollen;
import util.View;
import javafx.collections.transformation.SortedList;

public class GebruikerOverviewController implements NavigableController, NavigationGuard {

    @FXML private TableView<GebruikerDTO> gebruikerTable;
    @FXML private VBox formHost;

    @FXML private TableColumn<GebruikerDTO, String> naamCol;
    @FXML private TableColumn<GebruikerDTO, String> voornaamCol;
    @FXML private TableColumn<GebruikerDTO, Rollen> rolCol;
    @FXML private TableColumn<GebruikerDTO, GebruikerStatus> statusCol;
    @FXML private TableColumn<GebruikerDTO, GebruikerDTO> actiesCol;

    @FXML private javafx.scene.control.Button addBtn;

    @Setter private Navigator navigator;
    private AppContext context;
    private GebruikerService gebruikerService;
    private ObservableGebruikers observableGebruikers;
    private SortedList<GebruikerDTO> sortedList;
    private ClosableFormGuard activeFormGuard;

    @Override
    public void setContext(AppContext context) {
        this.context = context;
        this.gebruikerService = context.getGebruikerService();
        this.observableGebruikers = context.getObservableGebruikers();
    }

    @FXML
    private void initialize() {
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        voornaamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().voornaam()));
        rolCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().rol()));
        statusCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().status()));
        configureStatusColumn();

        ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::showEditForm, this::deleteGebruiker);

        naamCol.setStyle("-fx-alignment: center-left;");
        voornaamCol.setStyle("-fx-alignment: center-left;");
        rolCol.setStyle("-fx-alignment: CENTER;");
        statusCol.setStyle("-fx-alignment: CENTER;");
        actiesCol.setStyle("-fx-alignment: CENTER;");
        gebruikerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        gebruikerTable.setFixedCellSize(44);
        gebruikerTable.setSelectionModel(null);

        addBtn.managedProperty().bind(formHost.visibleProperty().not());
        addBtn.visibleProperty().bind(formHost.visibleProperty().not());
    }

    private void configureStatusColumn() {
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();

            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(GebruikerStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                label.setText(status.toString());
                label.getStyleClass().setAll("status-badge");

                switch (status) {
                    case ACTIEF -> label.getStyleClass().add("status-green");
                    case INACTIEF -> label.getStyleClass().add("status-red");
                }

                setGraphic(label);
            }
        });
    }

    @Override
    public void loadData() {
        observableGebruikers.setGebruikers(gebruikerService.getAllGebruikers());
        initializeTableItems();
    }

    @FXML
    private void onAdd() {
        openCreateForm();
    }

    private void initializeTableItems() {
        if (sortedList == null) {
            sortedList = new SortedList<>(observableGebruikers.getFilteredGebruikerList());
            sortedList.comparatorProperty().bind(gebruikerTable.comparatorProperty());
            gebruikerTable.setItems(sortedList);

            naamCol.setSortType(TableColumn.SortType.ASCENDING);
            gebruikerTable.getSortOrder().add(naamCol);

            actiesCol.setSortable(false);
        }
    }

    private void openCreateForm() {
        GebruikerFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.GEBRUIKER_FORM.fxml,
                GebruikerFormController::loadForCreate
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    private void showEditForm(GebruikerDTO gebruiker) {
        GebruikerFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.GEBRUIKER_FORM.fxml,
                c -> c.loadForEdit(gebruiker)
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    private void closeForm() {
        FormLoader.hideForm(formHost);
        activeFormGuard = null;
    }

    private void deleteGebruiker(GebruikerDTO gebruiker) {
        if (gebruiker.status() == GebruikerStatus.INACTIEF) {
            new Alert(Alert.AlertType.WARNING, "Gebruiker is al verwijderd.").showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gebruiker verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze gebruiker wil verwijderen?");
        alert.setContentText(gebruiker.naam() + ", " + gebruiker.voornaam() + " (" + gebruiker.email() + ")");

        ButtonType delete = new ButtonType("Verwijderen");
        ButtonType cancel = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(delete, cancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == delete) {
                try {
                    gebruikerService.deleteGebruiker(gebruiker.gebruikerId());
                    observableGebruikers.removeGebruiker(gebruiker.gebruikerId());
                    loadData();
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

    @Override
    public boolean canNavigateAway() {
        return activeFormGuard == null || activeFormGuard.canClose();
    }
}
