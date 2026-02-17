package gui;

import domein.TaakController;
import dto.TaakDTO;
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
import util.TaakType;

public class TaakOverviewController {
    @FXML
    private TableView<TaakDTO> taakTable;

    @FXML private TableColumn<TaakDTO, Long> idCol;
    @FXML private TableColumn<TaakDTO, TaakType> typeCol;
    @FXML private TableColumn<TaakDTO, String> omschrijvingCol;
    @FXML private TableColumn<TaakDTO, Integer> duurtijdCol;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    private final TaakController tc;
    private final ObservableList<TaakDTO> taken = FXCollections.observableArrayList();

    public TaakOverviewController(TaakController tc){
        this.tc = tc;
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().taakId()));
        typeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().taakType()));
        omschrijvingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().omschrijving()));
        duurtijdCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().duurtijd()));

        taken.addAll(tc.getAllTaken());
        taakTable.setItems(taken); // ObservableList linken

        editBtn.disableProperty().bind(taakTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(taakTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onAdd() {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TaakFormView.fxml"));
            loader.setControllerFactory(type ->
            {
                if (type == TaakFormController.class)
                    return new TaakFormController(tc);
                try
                {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Nieuwe Taak");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            taken.setAll(tc.getAllTaken());

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onEdit() {
        TaakDTO selected = taakTable.getSelectionModel().getSelectedItem();
        if (selected == null) return; // of error?

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TaakFormView.fxml"));
            loader.setControllerFactory(type -> {
                if (type == TaakFormController.class) return new TaakFormController(tc);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });

            Parent root = loader.load();
            TaakFormController form = loader.getController();
            form.loadForEdit(selected);

            Stage dialog = new Stage();
            dialog.setTitle("Taak wijzigen");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            taken.setAll(tc.getAllTaken());

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }


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
                    tc.deleteTaak(selected.taakId());
                    taken.setAll(tc.getAllTaken()); // refresh table
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

}
