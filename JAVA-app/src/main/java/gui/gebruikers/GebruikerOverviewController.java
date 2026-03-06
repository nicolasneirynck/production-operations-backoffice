package gui.gebruikers;

import domein.GebruikerController;
import dto.GebruikerDTO;
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
import util.GebruikerStatus;
import util.Rollen;

public class GebruikerOverviewController {

    @FXML private TableView<GebruikerDTO> gebruikerTable;

    @FXML private TableColumn<GebruikerDTO, Long> idCol;
    @FXML private TableColumn<GebruikerDTO, String> emailCol;
    @FXML private TableColumn<GebruikerDTO, String> gebruikersnaamCol;
    @FXML private TableColumn<GebruikerDTO, String> wachtwoordCol;
    @FXML private TableColumn<GebruikerDTO, GebruikerStatus> statusCol;
    @FXML private TableColumn<GebruikerDTO, Rollen> rolCol;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    // ObservableList maakt wijzigingen in deze lijst "observeerbaar"
    private final ObservableList<GebruikerDTO> gebruikers = FXCollections.observableArrayList();

    private final GebruikerController gc;

    public GebruikerOverviewController(GebruikerController gc){
        this.gc = gc;
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().gebruikerId()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().email()));
        gebruikersnaamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().gebruikersnaam()));
        wachtwoordCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().wachtwoord()));
        statusCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().status()));
        rolCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().rol()));

        // gebruikers ophalen uit DB
        gebruikers.addAll(gc.getAllGebruikers());
        gebruikerTable.setItems(gebruikers); // ObservableList linken

        editBtn.disableProperty().bind(gebruikerTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(gebruikerTable.getSelectionModel().selectedItemProperty().isNull());
    }

    private FXMLLoader createLoader() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GebruikerFormView.fxml"));
        loader.setControllerFactory(type ->
        {
            if (type == GebruikerFormController.class)
                return new GebruikerFormController(gc);
            try
            {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return loader;
    }

    @FXML
    private void onAdd() {
        try
        {
            FXMLLoader loader = createLoader();

            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Nieuwe Gebruiker");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            gebruikers.setAll(gc.getAllGebruikers());

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onEdit() {
        GebruikerDTO selected = gebruikerTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = createLoader();

            Parent root = loader.load();
            GebruikerFormController form = loader.getController();
            form.loadForEdit(selected);

            Stage dialog = new Stage();
            dialog.setTitle("Gebruiker wijzigen");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            gebruikers.setAll(gc.getAllGebruikers());

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onDelete() {
        GebruikerDTO selected = gebruikerTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if(selected.status() == GebruikerStatus.VERWIJDERD) {
            new Alert(Alert.AlertType.WARNING, "Gebruiker is al verwijderd.").showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gebruiker verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze gebruiker wil verwijderen?");
        alert.setContentText(selected.gebruikersnaam() + " (" + selected.email() + ")");

        ButtonType deleteBtn = new ButtonType("Verwijderen");
        ButtonType cancelBtn = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == deleteBtn) {
                try {
                    gc.deleteGebruiker(selected.gebruikerId());
                    gebruikers.setAll(gc.getAllGebruikers()); // refresh table
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

    private void loadFxml(String resource){}
}