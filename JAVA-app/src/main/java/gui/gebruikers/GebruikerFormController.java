package gui.gebruikers;

import domein.GebruikerController;
import dto.GebruikerDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.GebruikerStatus;
import util.Rollen;

public class GebruikerFormController {
    @FXML private TextField emailTxt;
    @FXML private TextField gebruikersnaamTxt;
    @FXML private TextField wachtwoordTxt;

    @FXML private ComboBox<GebruikerStatus> statusBx;
    @FXML private ComboBox<Rollen> rolBx;

    @FXML private Label errorLbl;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private final GebruikerController gc;
    private Long editingId = null; // null = nieuw, anders edit

    public GebruikerFormController(GebruikerController gc){
        this.gc = gc;
    }

    @FXML
    private void initialize() {
        statusBx.setItems(FXCollections.observableArrayList(GebruikerStatus.values()));
        rolBx.setItems(FXCollections.observableArrayList(Rollen.values()));

        statusBx.setValue(GebruikerStatus.ACTIEF);
        // Disablen omdat gebruiker altijd begint als actief:
        statusBx.setDisable(true);
        rolBx.setValue(Rollen.WERKNEMER);

        errorLbl.setText("");
    }

    public void loadForEdit(GebruikerDTO dto) {
        this.editingId = dto.gebruikerId();

        emailTxt.setText(dto.email());
        gebruikersnaamTxt.setText(dto.gebruikersnaam());
        wachtwoordTxt.setText(String.valueOf(dto.wachtwoord()));
        statusBx.setValue(dto.status());
        rolBx.setValue(dto.rol());

        // Je mag de gegevens van een verwijderde (= blokkeerde) gebruiker niet wijzigen, dit mag pas als je de
        // gebruiker gedeblokkeerd hebt.
        if (dto.status() == GebruikerStatus.VERWIJDERD) {
            emailTxt.setDisable(true);
            gebruikersnaamTxt.setDisable(true);
            wachtwoordTxt.setDisable(true);
            statusBx.setDisable(false);
            rolBx.setDisable(true);
        } else {
            emailTxt.setDisable(false);
            gebruikersnaamTxt.setDisable(false);
            wachtwoordTxt.setDisable(false);
            statusBx.setDisable(false);
            rolBx.setDisable(false);
        }

        saveBtn.setText("Wijzigen");
    }

    @FXML
    private void onSave() {
        try {
            String email = emailTxt.getText();
            String gebruikersnaam = gebruikersnaamTxt.getText();
            String wachtwoord = wachtwoordTxt.getText();

            GebruikerStatus status = statusBx.getValue();
            Rollen rol = rolBx.getValue();

            if (editingId == null) {
                gc.addGebruiker(email, gebruikersnaam, wachtwoord, status, rol);
            } else {
                gc.updateGebruiker(editingId, email, gebruikersnaam, wachtwoord, status, rol);
            }
            close();
        } catch (IllegalArgumentException ex) {
            errorLbl.setText(ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Annuleren");
        alert.setHeaderText("Wijzigingen annuleren?");
        alert.setContentText("Niet-opgeslagen wijzigingen gaan verloren.");

        ButtonType yesBtn = new ButtonType("Ja");
        ButtonType noBtn = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                close();
            }
        });
    }

    private void close() {
        Stage stage = (Stage) emailTxt.getScene().getWindow();
        stage.close();
    }
}
