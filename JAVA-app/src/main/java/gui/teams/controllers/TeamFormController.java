package gui.teams.controllers;

import domein.controllers.AlleTeamsService;
import dto.GebruikerDTO;
import dto.SiteDTO;
import dto.TeamDTO;
import exception.ValidationException;
import gui.factories.BadgeFactory;
import gui.navigation.ClosableFormGuard;
import gui.navigation.FormController;
import gui.teams.WerknemerAddCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import lombok.Setter;
import main.AppContext;
import util.Rollen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public abstract class TeamFormController implements FormController, ClosableFormGuard {

    @FXML protected Label titleLabel;

    @FXML protected ComboBox<SiteDTO> siteCb;
    @FXML protected Label verantwoordelijkeBadge;
    @FXML protected TextField zoekTf;

    @FXML protected TableView<GebruikerDTO> werknemersTable;
    @FXML protected TableColumn<GebruikerDTO, String> naamCol;
    @FXML protected TableColumn<GebruikerDTO, String> voornaamCol;
    @FXML protected TableColumn<GebruikerDTO, GebruikerDTO> addCol;

    @FXML protected FlowPane selectedPane;

    @FXML protected Label siteErr;
    @FXML protected Label verantwoordelijkeErr;
    @FXML protected Label medewerkersErr;
    @FXML protected Label formInfoLbl;

    @FXML protected Button saveBtn;
    @FXML protected Button cancelBtn;

    @Setter private Runnable onClose;

    protected AppContext context;
    private AlleTeamsService alleTeamsService;
    protected final ObservableList<GebruikerDTO> alleWerknemers = FXCollections.observableArrayList();
    protected final ObservableList<GebruikerDTO> geselecteerdeWerknemers = FXCollections.observableArrayList();
    private FilteredList<GebruikerDTO> filteredBeschikbaar;
    private SortedList<GebruikerDTO> sortedBeschikbaar;

    protected String editingTeamId = null;
    private TeamFormData initialFormData;

    @Override
    public void setContext(AppContext context) {
        this.context = context;
        this.alleTeamsService = context.getAlleTeamsService();
    }

    @FXML
    private void initialize() {
        configureSiteComboBox();
        configureWerknemersTable();

        zoekTf.textProperty().addListener((obs, oldV, newV) -> applyFilter());
        siteCb.valueProperty().addListener((obs, oldV, newV) -> {
            updateVerantwoordelijkeDisplay();
            clearError("site");
        });

        geselecteerdeWerknemers.addListener((javafx.collections.ListChangeListener<GebruikerDTO>) change -> {
            rebuildSelectedBadges();
            werknemersTable.refresh();
            clearError("medewerkers");
        });
    }

    @Override
    public void loadData() {
        if (isManagerMode()) {
            loadSites();
        }
        loadWerknemers();
    }

    public void prepareForCreate() {
        if (!isManagerMode()) {
            throw new IllegalStateException("Alleen managers kunnen een team aanmaken.");
        }

        editingTeamId = null;
        titleLabel.setText("Nieuw team");

        loadSitesVoorCreate();
        loadWerknemers();

        siteCb.setValue(null);
        siteCb.setDisable(false);
        geselecteerdeWerknemers.clear();
        zoekTf.clear();

        clearErrors();
        updateVerantwoordelijkeDisplay();

        if (siteCb.getItems().isEmpty()) {
            formInfoLbl.setText("Er kan geen nieuw team worden aangemaakt, omdat alle sites al een team hebben.");
            saveBtn.setDisable(true);
        } else {
            saveBtn.setDisable(false);
        }

        initialFormData = currentFormData();
    }

    public void prepareForEdit(TeamDTO team) {
        editingTeamId = team.teamCode();
        titleLabel.setText("Team wijzigen");

        if (isManagerMode()) {
            loadSitesVoorEdit(team.site());
            siteCb.setDisable(false);
        } else {
            siteCb.setItems(FXCollections.observableArrayList(List.of(team.site())));
            siteCb.setDisable(true);
        }

        loadWerknemers();
        siteCb.setValue(team.site());

        geselecteerdeWerknemers.setAll(team.teamleden().stream()
                .sorted(Comparator.comparing(GebruikerDTO::naam, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(GebruikerDTO::voornaam, String.CASE_INSENSITIVE_ORDER))
                .toList());

        zoekTf.clear();
        clearErrors();
        updateVerantwoordelijkeDisplay();
        initialFormData = currentFormData();
    }

    protected abstract boolean isManagerMode();

    protected abstract void saveTeam(SiteDTO site, List<Long> medewerkerIds) throws ValidationException;

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
            saveTeam(site, medewerkerIds);
            close();
        } catch (ValidationException ex) {
            showValidationErrors(ex);
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Opslaan mislukt: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        if (canClose()) {
            close();
        }
    }

    private void configureSiteComboBox() {
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

    private void configureWerknemersTable() {
        naamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().naam()));
        voornaamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().voornaam()));
        addCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        addCol.setCellFactory(col -> new WerknemerAddCell(geselecteerdeWerknemers));

        filteredBeschikbaar = new FilteredList<>(alleWerknemers, g -> true);
        sortedBeschikbaar = new SortedList<>(filteredBeschikbaar);
        sortedBeschikbaar.comparatorProperty().bind(werknemersTable.comparatorProperty());
        werknemersTable.setItems(sortedBeschikbaar);

        werknemersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        werknemersTable.setFixedCellSize(48);
        werknemersTable.setSelectionModel(null);

        naamCol.setSortable(true);
        voornaamCol.setSortable(true);
        addCol.setSortable(false);
    }

    private void loadSites() {
        List<SiteDTO> sites = alleTeamsService.getBeschikbareSitesVoorNieuwTeam()
                .stream()
                .sorted(Comparator.comparing(SiteDTO::naam, String.CASE_INSENSITIVE_ORDER))
                .toList();

        siteCb.setItems(FXCollections.observableArrayList(sites));
    }

    private void loadSitesVoorCreate() {
        List<SiteDTO> sites = alleTeamsService.getBeschikbareSitesVoorNieuwTeam()
                .stream()
                .sorted(Comparator.comparing(SiteDTO::naam, String.CASE_INSENSITIVE_ORDER))
                .toList();

        siteCb.setItems(FXCollections.observableArrayList(sites));

        boolean geenBeschikbareSites = sites.isEmpty();

        siteCb.setDisable(geenBeschikbareSites);
        saveBtn.setDisable(geenBeschikbareSites);

        if (geenBeschikbareSites) {
            formInfoLbl.setText("Er kan geen nieuw team worden aangemaakt, omdat alle sites al een team hebben.");
            formInfoLbl.setVisible(true);
            formInfoLbl.setManaged(true);
        } else {
            formInfoLbl.setVisible(false);
            formInfoLbl.setManaged(false);
        }
    }

    private void loadSitesVoorEdit(SiteDTO huidigeSite) {
        List<SiteDTO> sites = new ArrayList<>(alleTeamsService.getBeschikbareSitesVoorNieuwTeam());

        boolean huidigeSiteAlAanwezig = sites.stream()
                .anyMatch(s -> s.siteId() == huidigeSite.siteId());

        if (!huidigeSiteAlAanwezig) {
            sites.add(huidigeSite);
        }

        sites.sort(Comparator.comparing(SiteDTO::naam, String.CASE_INSENSITIVE_ORDER));
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
            selectedPane.getChildren().add(
                    BadgeFactory.createRemovableBadge(
                            medewerker.volledigeNaam(),
                            () -> {
                                geselecteerdeWerknemers.remove(medewerker);
                                applyFilter();
                            },
                            "employee-badge"
                    )
            );
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
        if (!hasUnsavedChanges()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Formulier sluiten");
        alert.setHeaderText("Niet-opgeslagen wijzigingen");
        alert.setContentText("Mogelijke wijzigingen werden niet opgeslagen. Wil je de pagina verlaten?");

        ButtonType ja = new ButtonType("Ja");
        ButtonType nee = new ButtonType("Nee", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ja, nee);

        return alert.showAndWait().orElse(nee) == ja;
    }

    private boolean hasUnsavedChanges() {
        return initialFormData != null && !currentFormData().equals(initialFormData);
    }

    private TeamFormData currentFormData() {
        SiteDTO site = siteCb.getValue();

        List<Long> medewerkerIds = geselecteerdeWerknemers.stream()
                .map(GebruikerDTO::gebruikerId)
                .sorted()
                .toList();

        return new TeamFormData(
                site == null ? null : site.siteId(),
                medewerkerIds
        );
    }

    private record TeamFormData(Long siteId, List<Long> medewerkerIds) {
    }
}
