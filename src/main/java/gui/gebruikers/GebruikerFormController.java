package gui.gebruikers;

import domein.services.GebruikerService;
import dto.GebruikerDTO;
import dto.LocatieDTO;
import gui.navigation.ClosableFormGuard;
import gui.navigation.FormController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import main.AppContext;
import org.apache.commons.validator.routines.EmailValidator;
import util.GebruikerStatus;
import util.Rollen;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GebruikerFormController implements FormController, ClosableFormGuard {
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance(false, false);

    @FXML private Label titleLabel;
    @FXML private TextField naamTxt;
    @FXML private TextField voornaamTxt;
    @FXML private DatePicker geboorteDatumDp;
    @FXML private TextField straatTxt;
    @FXML private TextField nummerTxt;
    @FXML private TextField postcodeTxt;
    @FXML private TextField gemeenteTxt;
    @FXML private ComboBox<String> landCb;
    @FXML private TextField emailTxt;
    @FXML private TextField gsmTxt;
    @FXML private TextField wachtwoordTxt;

    @FXML private ComboBox<GebruikerStatus> statusBx;
    @FXML private ComboBox<Rollen> rolBx;

    @FXML private Label naamErr;
    @FXML private Label voornaamErr;
    @FXML private Label geboorteDatumErr;
    @FXML private Label straatErr;
    @FXML private Label nummerErr;
    @FXML private Label postcodeErr;
    @FXML private Label gemeenteErr;
    @FXML private Label landErr;
    @FXML private Label emailErr;
    @FXML private Label gsmErr;
    @FXML private Label wachtwoordErr;
    @FXML private Label statusErr;
    @FXML private Label rolErr;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Runnable onClose;

    private GebruikerService gebruikerService;
    private ObservableGebruikers observableGebruikers;
    private Long editingId = null; // null = nieuw, anders edit
    private GebruikerFormData initialFormData;

    @Override
    public void setContext(AppContext context) {
        this.gebruikerService = context.getGebruikerService();
        this.observableGebruikers = context.getObservableGebruikers();
    }

    @FXML
    private void initialize() {
        statusBx.setItems(FXCollections.observableArrayList(GebruikerStatus.values()));
        rolBx.setItems(FXCollections.observableArrayList(Rollen.values()));

        statusBx.setValue(GebruikerStatus.ACTIEF);
        statusBx.setDisable(true);
        rolBx.setValue(Rollen.WERKNEMER);

        configureValidationListeners();
        configureLandComboBox();
    }

    @Override
    public void loadData() {
        observableGebruikers.setGebruikers(gebruikerService.getAllGebruikers());
    }

    public void loadForCreate() {
        editingId = null;
        titleLabel.setText("Gebruiker aanmaken");
        saveBtn.setText("Opslaan");

        naamTxt.clear();
        voornaamTxt.clear();
        geboorteDatumDp.setValue(null);
        straatTxt.clear();
        nummerTxt.clear();
        postcodeTxt.clear();
        gemeenteTxt.clear();
        landCb.setValue(null);
        emailTxt.clear();
        gsmTxt.clear();
        wachtwoordTxt.clear();
        statusBx.setValue(GebruikerStatus.ACTIEF);
        statusBx.setDisable(true);
        rolBx.setValue(Rollen.WERKNEMER);
        rolBx.setDisable(false);

        setFieldsDisabled(false);
        clearErrors();
        initialFormData = currentFormData();
    }

    public void loadForEdit(GebruikerDTO dto) {
        this.editingId = dto.gebruikerId();
        titleLabel.setText("Gebruiker wijzigen");

        naamTxt.setText(dto.naam());
        voornaamTxt.setText(dto.voornaam());
        geboorteDatumDp.setValue(dto.geboortedatum());
        LocatieDTO locatie = dto.locatie();
        straatTxt.setText(locatie == null ? "" : locatie.straat());
        nummerTxt.setText(locatie == null ? "" : locatie.nummer());
        postcodeTxt.setText(locatie == null ? "" : locatie.postcode());
        gemeenteTxt.setText(locatie == null ? "" : locatie.gemeente());
        landCb.setValue(locatie == null ? null : locatie.land());
        emailTxt.setText(dto.email());
        gsmTxt.setText(dto.gsm());
        wachtwoordTxt.clear();
        statusBx.setValue(dto.status());
        rolBx.setValue(dto.rol());

        if (dto.status() == GebruikerStatus.INACTIEF) {
            setFieldsDisabled(true);
            statusBx.setDisable(false);
            rolBx.setDisable(true);
        } else {
            setFieldsDisabled(false);
            statusBx.setDisable(false);
            rolBx.setDisable(false);
        }

        saveBtn.setText("Wijzigen");
        wachtwoordTxt.setPromptText("Leeg laten om te behouden");
        clearErrors();
        initialFormData = currentFormData();
    }

    @FXML
    private void onSave() {
        clearErrors();

        if (!validateForm()) {
            return;
        }

        try {
            String naam = naamTxt.getText();
            String voornaam = voornaamTxt.getText();
            LocalDate geboortedatum = geboorteDatumDp.getValue();
            String straat = straatTxt.getText();
            String nummer = nummerTxt.getText();
            String postcode = postcodeTxt.getText();
            String gemeente = gemeenteTxt.getText();
            String land = landCb.getValue();
            String email = emailTxt.getText();
            String gsm = gsmTxt.getText();
            String wachtwoord = wachtwoordTxt.getText();

            GebruikerStatus status = statusBx.getValue();
            Rollen rol = rolBx.getValue();

            if (editingId == null) {
                gebruikerService.addGebruiker(
                        naam, voornaam, geboortedatum, straat, nummer, postcode, gemeente, land,
                        email, gsm, rol, status, wachtwoord
                );
            } else {
                gebruikerService.updateGebruiker(
                        editingId, naam, voornaam, geboortedatum, straat, nummer, postcode, gemeente, land,
                        email, gsm, rol, status, wachtwoord
                );
            }
            observableGebruikers.setGebruikers(gebruikerService.getAllGebruikers());
            close();
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        if (canClose()) {
            close();
        }
    }

    private void close() {
        if (onClose != null) onClose.run();
    }

    @Override
    public boolean canClose() {
        if (!hasUnsavedChanges()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Formulier sluiten");
        alert.setHeaderText("Niet-opgeslagen wijzigingen");
        alert.setContentText("Je wijzigingen werden niet opgeslagen. Ben je zeker dat je de pagina wil verlaten?");

        ButtonType ja = new ButtonType("Ja");
        ButtonType nee = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(ja, nee);

        return alert.showAndWait().orElse(nee) == ja;
    }

    private void configureValidationListeners() {
        naamTxt.textProperty().addListener((o, a, b) -> clearError("naam"));
        voornaamTxt.textProperty().addListener((o, a, b) -> clearError("voornaam"));
        geboorteDatumDp.valueProperty().addListener((o, a, b) -> clearError("geboortedatum"));
        straatTxt.textProperty().addListener((o, a, b) -> clearError("locatie.straat"));
        nummerTxt.textProperty().addListener((o, a, b) -> clearError("locatie.nummer"));
        postcodeTxt.textProperty().addListener((o, a, b) -> clearError("locatie.postcode"));
        gemeenteTxt.textProperty().addListener((o, a, b) -> clearError("locatie.gemeente"));
        landCb.valueProperty().addListener((o, a, b) -> clearError("locatie.land"));
        emailTxt.textProperty().addListener((o, a, b) -> clearError("email"));
        gsmTxt.textProperty().addListener((o, a, b) -> clearError("gsm"));
        wachtwoordTxt.textProperty().addListener((o, a, b) -> clearError("wachtwoord"));
        statusBx.valueProperty().addListener((o, a, b) -> clearError("status"));
        rolBx.valueProperty().addListener((o, a, b) -> clearError("rol"));
    }

    private void configureLandComboBox() {
        List<String> landen = Arrays.stream(Locale.getISOCountries())
                .map(code -> Locale.of("", code).getDisplayCountry())
                .sorted()
                .toList();

        landCb.setItems(FXCollections.observableArrayList(landen));
        landCb.setVisibleRowCount(10);
    }

    private void setFieldsDisabled(boolean disabled) {
        naamTxt.setDisable(disabled);
        voornaamTxt.setDisable(disabled);
        geboorteDatumDp.setDisable(disabled);
        straatTxt.setDisable(disabled);
        nummerTxt.setDisable(disabled);
        postcodeTxt.setDisable(disabled);
        gemeenteTxt.setDisable(disabled);
        landCb.setDisable(disabled);
        emailTxt.setDisable(disabled);
        gsmTxt.setDisable(disabled);
        wachtwoordTxt.setDisable(disabled);
    }

    private boolean validateForm() {
        boolean valid = true;

        if (isBlank(naamTxt.getText())) {
            setError("naam", "Naam is verplicht.");
            valid = false;
        }
        if (isBlank(voornaamTxt.getText())) {
            setError("voornaam", "Voornaam is verplicht.");
            valid = false;
        }
        if (geboorteDatumDp.getValue() == null) {
            setError("geboortedatum", "Geboortedatum is verplicht.");
            valid = false;
        } else if (geboorteDatumDp.getValue().isAfter(LocalDate.now())) {
            setError("geboortedatum", "Geboortedatum mag niet in de toekomst liggen.");
            valid = false;
        }
        if (isBlank(straatTxt.getText())) {
            setError("locatie.straat", "Straat vereist.");
            valid = false;
        }
        if (isBlank(nummerTxt.getText())) {
            setError("locatie.nummer", "Nummer vereist.");
            valid = false;
        }
        if (isBlank(postcodeTxt.getText())) {
            setError("locatie.postcode", "Postcode vereist.");
            valid = false;
        }
        if (isBlank(gemeenteTxt.getText())) {
            setError("locatie.gemeente", "Gemeente vereist.");
            valid = false;
        }
        if (isBlank(landCb.getValue())) {
            setError("locatie.land", "Land vereist.");
            valid = false;
        }
        if (isBlank(emailTxt.getText())) {
            setError("email", "Email is verplicht.");
            valid = false;
        } else if (!EMAIL_VALIDATOR.isValid(emailTxt.getText().trim())) {
            setError("email", "Email moet een geldig formaat hebben.");
            valid = false;
        }
        if (statusBx.getValue() == null) {
            setError("status", "Status is verplicht.");
            valid = false;
        }
        if (rolBx.getValue() == null) {
            setError("rol", "Rol is verplicht.");
            valid = false;
        }
        if (editingId == null && isBlank(wachtwoordTxt.getText())) {
            setError("wachtwoord", "Wachtwoord is verplicht.");
            valid = false;
        }

        return valid;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void setError(String key, String message) {
        switch (key) {
            case "naam" -> {
                naamErr.setText(message);
                naamTxt.getStyleClass().add("field-error");
            }
            case "voornaam" -> {
                voornaamErr.setText(message);
                voornaamTxt.getStyleClass().add("field-error");
            }
            case "geboortedatum" -> {
                geboorteDatumErr.setText(message);
                geboorteDatumDp.getStyleClass().add("field-error");
            }
            case "locatie.straat" -> {
                straatErr.setText(message);
                straatTxt.getStyleClass().add("field-error");
            }
            case "locatie.nummer" -> {
                nummerErr.setText(message);
                nummerTxt.getStyleClass().add("field-error");
            }
            case "locatie.postcode" -> {
                postcodeErr.setText(message);
                postcodeTxt.getStyleClass().add("field-error");
            }
            case "locatie.gemeente" -> {
                gemeenteErr.setText(message);
                gemeenteTxt.getStyleClass().add("field-error");
            }
            case "locatie.land" -> {
                landErr.setText(message);
                landCb.getStyleClass().add("field-error");
            }
            case "email" -> {
                emailErr.setText(message);
                emailTxt.getStyleClass().add("field-error");
            }
            case "gsm" -> {
                gsmErr.setText(message);
                gsmTxt.getStyleClass().add("field-error");
            }
            case "wachtwoord" -> {
                wachtwoordErr.setText(message);
                wachtwoordTxt.getStyleClass().add("field-error");
            }
            case "status" -> {
                statusErr.setText(message);
                statusBx.getStyleClass().add("field-error");
            }
            case "rol" -> {
                rolErr.setText(message);
                rolBx.getStyleClass().add("field-error");
            }
        }
    }

    private void clearError(String key) {
        switch (key) {
            case "naam" -> {
                naamErr.setText("");
                naamTxt.getStyleClass().remove("field-error");
            }
            case "voornaam" -> {
                voornaamErr.setText("");
                voornaamTxt.getStyleClass().remove("field-error");
            }
            case "geboortedatum" -> {
                geboorteDatumErr.setText("");
                geboorteDatumDp.getStyleClass().remove("field-error");
            }
            case "locatie.straat" -> {
                straatErr.setText("");
                straatTxt.getStyleClass().remove("field-error");
            }
            case "locatie.nummer" -> {
                nummerErr.setText("");
                nummerTxt.getStyleClass().remove("field-error");
            }
            case "locatie.postcode" -> {
                postcodeErr.setText("");
                postcodeTxt.getStyleClass().remove("field-error");
            }
            case "locatie.gemeente" -> {
                gemeenteErr.setText("");
                gemeenteTxt.getStyleClass().remove("field-error");
            }
            case "locatie.land" -> {
                landErr.setText("");
                landCb.getStyleClass().remove("field-error");
            }
            case "email" -> {
                emailErr.setText("");
                emailTxt.getStyleClass().remove("field-error");
            }
            case "gsm" -> {
                gsmErr.setText("");
                gsmTxt.getStyleClass().remove("field-error");
            }
            case "wachtwoord" -> {
                wachtwoordErr.setText("");
                wachtwoordTxt.getStyleClass().remove("field-error");
            }
            case "status" -> {
                statusErr.setText("");
                statusBx.getStyleClass().remove("field-error");
            }
            case "rol" -> {
                rolErr.setText("");
                rolBx.getStyleClass().remove("field-error");
            }
        }
    }

    private void clearErrors() {
        naamErr.setText("");
        voornaamErr.setText("");
        geboorteDatumErr.setText("");
        straatErr.setText("");
        nummerErr.setText("");
        postcodeErr.setText("");
        gemeenteErr.setText("");
        landErr.setText("");
        emailErr.setText("");
        gsmErr.setText("");
        wachtwoordErr.setText("");
        statusErr.setText("");
        rolErr.setText("");

        naamTxt.getStyleClass().remove("field-error");
        voornaamTxt.getStyleClass().remove("field-error");
        geboorteDatumDp.getStyleClass().remove("field-error");
        straatTxt.getStyleClass().remove("field-error");
        nummerTxt.getStyleClass().remove("field-error");
        postcodeTxt.getStyleClass().remove("field-error");
        gemeenteTxt.getStyleClass().remove("field-error");
        landCb.getStyleClass().remove("field-error");
        emailTxt.getStyleClass().remove("field-error");
        gsmTxt.getStyleClass().remove("field-error");
        wachtwoordTxt.getStyleClass().remove("field-error");
        statusBx.getStyleClass().remove("field-error");
        rolBx.getStyleClass().remove("field-error");
    }

    private GebruikerFormData currentFormData() {
        return new GebruikerFormData(
                naamTxt.getText(),
                voornaamTxt.getText(),
                geboorteDatumDp.getValue(),
                straatTxt.getText(),
                nummerTxt.getText(),
                postcodeTxt.getText(),
                gemeenteTxt.getText(),
                landCb.getValue(),
                emailTxt.getText(),
                gsmTxt.getText(),
                wachtwoordTxt.getText(),
                statusBx.getValue(),
                rolBx.getValue()
        );
    }

    private boolean hasUnsavedChanges() {
        return !currentFormData().equals(initialFormData);
    }

    private record GebruikerFormData(
            String naam,
            String voornaam,
            LocalDate geboortedatum,
            String straat,
            String nummer,
            String postcode,
            String gemeente,
            String land,
            String email,
            String gsm,
            String wachtwoord,
            GebruikerStatus status,
            Rollen rol
    ) {
    }
}
