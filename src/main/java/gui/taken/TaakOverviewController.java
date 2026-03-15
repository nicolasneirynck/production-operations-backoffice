package gui.taken;

import dto.TaakDTO;
import gui.navigation.*;
import gui.LayoutController;
import gui.factories.ActionColumnFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.View;

public class TaakOverviewController implements NavigableController, NavigationGuard {
    @FXML private VBox formHost;
    @FXML private Button addBtn;

    @FXML private TableView<TaakDTO> taakTable;
    @FXML private TableColumn<TaakDTO, String> typeCol;
    @FXML private TableColumn<TaakDTO, String> omschrijvingCol;
    @FXML private TableColumn<TaakDTO, Integer> duurtijdCol;
    @FXML private TableColumn<TaakDTO, TaakDTO> actiesCol;

    private AppContext context;
    @Setter private Navigator navigator;
    private ObservableTaken observableTaken;

    private SortedList<TaakDTO> sortedList;

    private ClosableFormGuard activeFormGuard;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableTaken = ctx.getObservableTaken();
    }

    @FXML
    private void initialize() {
        configureColumns();
        configureActiesColumn();
        configureTableLayout();

        addBtn.managedProperty().bind(formHost.visibleProperty().not());
        addBtn.visibleProperty().bind(formHost.visibleProperty().not());
    }

    private void configureColumns() {
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().taakType()));
        omschrijvingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().omschrijving()));
        duurtijdCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().duurtijd()));
    }

    private void configureActiesColumn() {
        ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::showEditForm, this::deleteTaak);
    }

    private void configureTableLayout() {
        typeCol.setStyle("-fx-alignment: center-left;");
        omschrijvingCol.setStyle("-fx-alignment: center-left;");
        duurtijdCol.setStyle("-fx-alignment: CENTER;");
        actiesCol.setStyle("-fx-alignment: CENTER;");
        taakTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        taakTable.setFixedCellSize(44);
        taakTable.setSelectionModel(null);
    }

    @Override
    public void loadData() {
        loadTaken();
        initializeTableItems();
    }

    private void loadTaken() {
        observableTaken.reload();
    }

    private void initializeTableItems() {
        if (sortedList == null) {
            sortedList = new SortedList<>(observableTaken.getFilteredTaakList());
            sortedList.comparatorProperty().bind(taakTable.comparatorProperty());
            taakTable.setItems(sortedList);

            typeCol.setSortType(TableColumn.SortType.ASCENDING);
            taakTable.getSortOrder().add(typeCol);

            omschrijvingCol.setSortable(false);
            actiesCol.setSortable(false);
        }
    }

    @FXML
    private void onAdd() {
        openCreateForm();
    }

    private void openCreateForm() {
        TaakFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.TAKEN_FORM.fxml,
                TaakFormController::loadForCreate
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    private void showEditForm(TaakDTO taak) {
        TaakFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.TAKEN_FORM.fxml,
                c -> c.loadForEdit(taak)
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    private void closeForm() {
        FormLoader.hideForm(formHost);
        activeFormGuard = null;
    }

    private void deleteTaak(TaakDTO taak) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Taak verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze taak wil verwijderen?");
        alert.setContentText(taak.taakType() + " (" + taak.omschrijving() + ")");

        ButtonType delete = new ButtonType("Verwijderen");
        ButtonType cancel = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(delete, cancel);

        alert.showAndWait().ifPresent(choice -> {
            if (choice == delete) {
                try {
                    observableTaken.deleteTaak(taak.taakId());
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
