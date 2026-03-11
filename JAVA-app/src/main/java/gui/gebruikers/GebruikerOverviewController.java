package gui.gebruikers;

import domein.GebruikerController;
import dto.GebruikerDTO;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import main.AppContext;
import util.GebruikerStatus;
import util.Rollen;

public class GebruikerOverviewController implements NavigableController {

    @FXML private TableView<GebruikerDTO> gebruikerTable;

    @FXML private TableColumn<GebruikerDTO, Long> idCol;
    @FXML private TableColumn<GebruikerDTO, String> naamCol;
    @FXML private TableColumn<GebruikerDTO, String> voornaamCol;
    @FXML private TableColumn<GebruikerDTO, Rollen> rolCol;
    @FXML private TableColumn<GebruikerDTO, GebruikerStatus> statusCol;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    @Setter private Navigator navigator;
    private AppContext context;
    private GebruikerController gc;

    private final ObservableList<GebruikerDTO> gebruikers = FXCollections.observableArrayList();

    @Override
    public void setContext(AppContext context) {
        this.context = context;
        this.gc = context.getGebruikerController();
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().gebruikerId()));
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        voornaamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().voornaam()));
        rolCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().rol()));
        statusCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().status()));

        gebruikerTable.setItems(gebruikers);

        editBtn.disableProperty().bind(gebruikerTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(gebruikerTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    public void loadData() {
        gebruikers.setAll(gc.getAllGebruikers());
    }

    private FXMLLoader createLoader() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/gebruikers/GebruikerFormView.fxml"));
        loader.setControllerFactory(type -> {
            if (type == GebruikerFormController.class) {
                return new GebruikerFormController(gc);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return loader;
    }

    @FXML
    private void onAdd() {
        try {
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
        if (selected.status() == GebruikerStatus.INACTIEF) {
            new Alert(Alert.AlertType.WARNING, "Gebruiker is al verwijderd.").showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gebruiker verwijderen");
        alert.setHeaderText("Ben je zeker dat je deze gebruiker wil verwijderen?");
        alert.setContentText(selected.naam() + ", " + selected.voornaam() + " (" + selected.email() + ")");

        ButtonType delete = new ButtonType("Verwijderen");
        ButtonType cancel = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(delete, cancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == delete) {
                try {
                    gc.deleteGebruiker(selected.gebruikerId());
                    gebruikers.setAll(gc.getAllGebruikers());
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                } catch (RuntimeException ex) {
                    new Alert(Alert.AlertType.ERROR, "Verwijderen mislukt: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }
}
