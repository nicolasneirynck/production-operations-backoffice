package gui.teams;

import dto.GebruikerDTO;
import dto.SiteDTO;
import dto.TeamDTO;
import exception.ValidationException;
import gui.navigation.ClosableFormGuard;
import gui.navigation.FormController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.Rollen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TeamFormController implements FormController, ClosableFormGuard {

    @FXML private VBox root;
    @FXML private Label titleLabel;

    @FXML private ComboBox<SiteDTO> siteCb;
    @FXML private Label verantwoordelijkeBadge;
    @FXML private TextField zoekTf;

    @FXML private TableView<GebruikerDTO> beschikbaarTable;
    @FXML private TableColumn<GebruikerDTO, String> naamCol;
    @FXML private TableColumn<GebruikerDTO, String> voornaamCol;
    @FXML private TableColumn<GebruikerDTO, GebruikerDTO> addCol;

    @FXML private FlowPane selectedPane;

    @FXML private Label siteErr;
    @FXML private Label verantwoordelijkeErr;
    @FXML private Label medewerkersErr;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    @Setter private Runnable onClose;

    private AppContext context;
    private ObservableTeams observableTeams;

    private final ObservableList<GebruikerDTO> alleWerknemers = FXCollections.observableArrayList();
    private final ObservableList<GebruikerDTO> geselecteerdeWerknemers = FXCollections.observableArrayList();

    private FilteredList<GebruikerDTO> filteredBeschikbaar;

    private Long editingTeamId = null;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableTeams = ctx.getObservableTeams();
    }

    @FXML
    private void initialize() {
        configureSiteCombo();
        configureBeschikbaarTable();

        zoekTf.textProperty().addListener((obs, oldV, newV) -> applyFilter());
        siteCb.valueProperty().addListener((obs, oldV, newV) -> {
            updateVerantwoordelijkeDisplay();
            clearError("site");
        });

        geselecteerdeWerknemers.addListener((javafx.collections.ListChangeListener<GebruikerDTO>) change -> {
            rebuildSelectedBadges();
            beschikbaarTable.refresh();
            clearError("medewerkers");
        });
    }

    private void configureSiteCombo() {
        siteCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(SiteDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.naam());
            }
        });

        siteCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SiteDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.naam());
            }
        });
    }

    private void configureBeschikbaarTable() {
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        voornaamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().voornaam()));
        addCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));

        addCol.setCellFactory(col -> new TableCell<>() {
            private final Button addBtn = new Button("+");

            {
                addBtn.getStyleClass().add("icon-button");
                addBtn.setStyle("""
                        -fx-background-color: #5F86F6;
                        -fx-text-fill: white;
                        -fx-background-radius: 999;
                        -fx-min-width: 28;
                        -fx-min-height: 28;
                        -fx-max-width: 28;
                        -fx-max-height: 28;
                        -fx-font-weight: bold;
                        """);

                setAlignment(Pos.CENTER);

                addBtn.setOnAction(e -> {
                    GebruikerDTO medewerker = getItem();
                    if (medewerker != null && !geselecteerdeWerknemers.contains(medewerker)) {
                        geselecteerdeWerknemers.add(medewerker);
                        geselecteerdeWerknemers.sort(Comparator
                                .comparing(GebruikerDTO::naam, String.CASE_INSENSITIVE_ORDER)
                                .thenComparing(GebruikerDTO::voornaam, String.CASE_INSENSITIVE_ORDER));
                    }
                });
            }

            @Override
            protected void updateItem(GebruikerDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                addBtn.setDisable(geselecteerdeWerknemers.contains(item));
                setGraphic(addBtn);
            }
        });

        filteredBeschikbaar = new FilteredList<>(alleWerknemers, g -> true);
        beschikbaarTable.setItems(filteredBeschikbaar);

        beschikbaarTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        beschikbaarTable.setFixedCellSize(48);
        beschikbaarTable.setSelectionModel(null);

        naamCol.setSortable(true);
        voornaamCol.setSortable(true);
        addCol.setSortable(false);
    }

    @Override
    public void loadData() {
        loadSites();
        loadWerknemers();
    }

    public void loadForCreate() {
        editingTeamId = null;
        titleLabel.setText("Nieuw team");

        loadData();

        siteCb.setValue(null);
        geselecteerdeWerknemers.clear();
        zoekTf.clear();

        clearErrors();
        updateVerantwoordelijkeDisplay();
    }

    public void loadForEdit(TeamDTO team) {
        editingTeamId = team.teamCode();
        titleLabel.setText("Team wijzigen");

        loadData();

        siteCb.setValue(team.site());

        geselecteerdeWerknemers.setAll(team.teamleden().stream()
                .sorted(Comparator.comparing(GebruikerDTO::naam, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(GebruikerDTO::voornaam, String.CASE_INSENSITIVE_ORDER))
                .toList());

        zoekTf.clear();

        clearErrors();
        updateVerantwoordelijkeDisplay();

        // Als site wijzigen bij edit niet mag:
        // siteCb.setDisable(true);
    }

    private void loadSites() {
        List<SiteDTO> sites = context.getObservableSites()
                .getFilteredSiteList()
                .stream()
                .sorted(Comparator.comparing(SiteDTO::naam, String.CASE_INSENSITIVE_ORDER))
                .toList();

        siteCb.setItems(FXCollections.observableArrayList(sites));
    }

    private void loadWerknemers() {
        List<GebruikerDTO> werknemers = context.getGebruikerController()
                .getAllGebruikers()
                .stream()
                .filter(g -> g.rol() == Rollen.WERKNEMER)
                .sorted(Comparator.comparing(GebruikerDTO::naam, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(GebruikerDTO::voornaam, String.CASE_INSENSITIVE_ORDER))
                .toList();

        alleWerknemers.setAll(werknemers);
        applyFilter();
    }

    private void applyFilter() {
        String zoek = zoekTf.getText() == null ? "" : zoekTf.getText().trim().toLowerCase(Locale.ROOT);

        filteredBeschikbaar.setPredicate(g -> {
            if (geselecteerdeWerknemers.contains(g)) {
                return false;
            }

            if (zoek.isBlank()) {
                return true;
            }

            return g.naam().toLowerCase(Locale.ROOT).contains(zoek)
                    || g.voornaam().toLowerCase(Locale.ROOT).contains(zoek)
                    || g.volledigeNaam().toLowerCase(Locale.ROOT).contains(zoek);
        });
    }

    private void updateVerantwoordelijkeDisplay() {
        SiteDTO site = siteCb.getValue();

        if (site == null || site.verantwoordelijke() == null) {
            verantwoordelijkeBadge.setText("-");
            verantwoordelijkeBadge.getStyleClass().setAll("employee-badge");
            return;
        }

        verantwoordelijkeBadge.setText(site.verantwoordelijke().volledigeNaam());
        verantwoordelijkeBadge.getStyleClass().setAll("employee-badge");
    }

    private void rebuildSelectedBadges() {
        selectedPane.getChildren().clear();

        for (GebruikerDTO medewerker : geselecteerdeWerknemers) {
            HBox badge = new HBox(8);
            badge.setAlignment(Pos.CENTER_LEFT);
            badge.getStyleClass().add("employee-badge");

            Label naamLbl = new Label(medewerker.volledigeNaam());

            Button removeBtn = new Button("×");
            removeBtn.getStyleClass().add("icon-button");
            removeBtn.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: #667085;
                    -fx-padding: 0;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    """);

            removeBtn.setOnAction(e -> {
                geselecteerdeWerknemers.remove(medewerker);
                applyFilter();
            });

            badge.getChildren().addAll(naamLbl, removeBtn);
            selectedPane.getChildren().add(badge);
        }
    }

    @FXML
    private void onSave() {
        clearErrors();

        SiteDTO site = siteCb.getValue();

        if (!validateForm(site)) {
            return;
        }

        List<Long> medewerkerIds = geselecteerdeWerknemers.stream()
                .map(GebruikerDTO::gebruikerId)
                .toList();

        try {
            if (editingTeamId == null) {
                observableTeams.addTeam(site.siteId(), medewerkerIds);
            } else {
                observableTeams.updateTeam(editingTeamId, medewerkerIds);
            }

            observableTeams.reload();
            close();

        } catch (ValidationException ex) {
            showValidationErrors(ex);
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
        }
    }

    private boolean validateForm(SiteDTO site) {
        boolean valid = true;

        if (site == null) {
            siteErr.setText("Site is verplicht.");
            siteCb.getStyleClass().add("field-error");
            valid = false;
        }

        if (geselecteerdeWerknemers.size() < 3) {
            medewerkersErr.setText("Een team moet minstens 3 werknemers hebben.");
            valid = false;
        }

        return valid;
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
        if (onClose != null) {
            onClose.run();
        }
    }

    private void showValidationErrors(ValidationException ex) {
        clearErrors();

        ex.getExceptionMap().forEach((field, iae) -> {
            String msg = iae.getMessage();

            switch (field) {
                case "site" -> {
                    siteErr.setText(msg);
                    siteCb.getStyleClass().add("field-error");
                }
                case "leden", "ledenRol", "ledenDubbel" -> medewerkersErr.setText(msg);
                default -> new Alert(Alert.AlertType.ERROR, msg).showAndWait();
            }
        });
    }

    private void clearError(String key) {
        switch (key) {
            case "site" -> {
                siteErr.setText("");
                siteCb.getStyleClass().remove("field-error");
            }
            case "medewerkers" -> medewerkersErr.setText("");
        }
    }

    private void clearErrors() {
        siteErr.setText("");
        verantwoordelijkeErr.setText("");
        medewerkersErr.setText("");

        siteCb.getStyleClass().remove("field-error");
    }

    @Override
    public boolean canClose() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Formulier sluiten");
        alert.setHeaderText("Niet-opgeslagen wijzigingen");
        alert.setContentText("Mogelijke wijzigingen werden niet opgeslagen. Wil je de pagina verlaten?");

        ButtonType ja = new ButtonType("Ja");
        ButtonType nee = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ja, nee);

        return alert.showAndWait().orElse(nee) == ja;
    }
}