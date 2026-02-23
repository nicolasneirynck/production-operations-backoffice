package gui;

import domein.TaakController;
import dto.SiteDTO;
import dto.TaakDTO;
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
import util.TaakType;

public class TaakOverviewController implements NavigableController {
    @FXML
    private TableView<TaakDTO> taakTable;

    @FXML private TableColumn<TaakDTO, Long> idCol;
    @FXML private TableColumn<TaakDTO, TaakType> typeCol;
    @FXML private TableColumn<TaakDTO, String> omschrijvingCol;
    @FXML private TableColumn<TaakDTO, Integer> duurtijdCol;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button refreshBtn;

    @Setter
    private Navigator navigator;
    private AppContext context;
   // private final Stage stage;
    private ObservableTaken observableTaken;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableTaken = ctx.getObservableTaken();
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().taakId()));
        typeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().taakType()));
        omschrijvingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().omschrijving()));
        duurtijdCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().duurtijd()));

        // SortedList in GUI
        SortedList<TaakDTO> sortedList = new SortedList<>(observableTaken.getFilteredTaakList());
        //binding voor kolomsortering
        sortedList.comparatorProperty().bind(taakTable.comparatorProperty());

        taakTable.setItems(sortedList);

        //default sortering
        idCol.setSortType(TableColumn.SortType.ASCENDING);
        taakTable.getSortOrder().add(idCol);
        taakTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);// kolommen vullen automatisch de breedte

        editBtn.disableProperty().bind(taakTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(taakTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onAdd() {
        navigator.showDialog(View.TAKEN_FORM,"Template taak toevoegen",null);
        observableTaken.reload();
    }

    @FXML
    private void onEdit() {
        TaakDTO selected = taakTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        navigator.showDialog(View.TAKEN_FORM,"Template taak wijzigen",controller -> {
                TaakFormController form = (TaakFormController) controller;
                ((TaakFormController) controller).loadForEdit(selected);
            });

        observableTaken.reload();
    }

    @FXML
    private void onDelete() {
        TaakDTO selected = taakTable.getSelectionModel().getSelectedItem();
        if (selected == null) return; // error?

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Taak verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze taak wil verwijderen?");
        alert.setContentText(selected.taakType() + " (" + selected.omschrijving() + ")");

        ButtonType deleteBtn = new ButtonType("Verwijderen");
        ButtonType cancelBtn = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == deleteBtn) {
                try {
                    observableTaken.deleteTaak(selected.taakId());
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });

        observableTaken.reload();
    }

    @FXML
    private void onBack() {
        navigator.goTo(View.MAIN_MENU);
    }


    @FXML
    private void onRefresh() {
        observableTaken.reload();
    }

}
