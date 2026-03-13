package gui.teams;

import dto.GebruikerDTO;
import dto.SiteDTO;
import dto.TeamDTO;
import gui.navigation.ClosableFormGuard;
import gui.navigation.FormLoader;
import gui.navigation.NavigableController;
import gui.navigation.NavigationGuard;
import gui.navigation.Navigator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.View;

public abstract class AbstractTeamOverviewController implements NavigableController, NavigationGuard {

    @FXML protected Label titleLabel;
    @FXML protected Button addBtn;
    @FXML protected VBox formHost;

    @FXML protected TableView<TeamDTO> teamTable;
    @FXML protected TableColumn<TeamDTO, String> siteCol;
    @FXML protected TableColumn<TeamDTO, String> verantwoordelijkeCol;
    @FXML protected TableColumn<TeamDTO, TeamDTO> medewerkersCol;
    @FXML protected TableColumn<TeamDTO, TeamDTO> actiesCol;

    @Setter protected Navigator navigator;
    protected ClosableFormGuard activeFormGuard;

    protected AppContext context;
    protected ObservableTeams observableTeams;
    protected SortedList<TeamDTO> sortedList;

    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableTeams = ctx.getObservableTeams();
    }

    @FXML
    private void initialize() {
        configureColumns();
        configureTableLayout();
    }

    protected void configureColumns() {
        siteCol.setCellValueFactory(cellData -> {
            SiteDTO site = cellData.getValue().site();
            return new SimpleStringProperty(site == null ? "-" : site.naam());
        });

        verantwoordelijkeCol.setCellValueFactory(c -> {
            GebruikerDTO verantwoordelijke = c.getValue().site().verantwoordelijke();
            return new SimpleStringProperty(
                    verantwoordelijke == null ? "-" : verantwoordelijke.naam().toUpperCase() + " " + verantwoordelijke.voornaam()
            );
        });

        medewerkersCol.setCellValueFactory(cellData -> Bindings.createObjectBinding(cellData::getValue));
        medewerkersCol.setCellFactory(col -> new TeamLedenTableCell());

        medewerkersCol.setSortable(false);
        actiesCol.setSortable(false);
    }

    protected void configureTableLayout() {
        siteCol.setStyle("-fx-alignment: CENTER-LEFT;");
        verantwoordelijkeCol.setStyle("-fx-alignment: CENTER;");
        medewerkersCol.setStyle("-fx-alignment: CENTER-LEFT;");
        actiesCol.setStyle("-fx-alignment: CENTER;");

        teamTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        teamTable.setFixedCellSize(84);
    }

    @Override
    public void loadData() {
        configureScreen();
        loadTeams();
        initializeTableItems();
    }

    protected abstract void configureScreen();

    protected abstract void loadTeams();

    protected abstract void configureActiesColumn();

    protected void initializeTableItems() {
        if (sortedList == null) {
            sortedList = new SortedList<>(observableTeams.getFilteredTeamList());
            sortedList.comparatorProperty().bind(teamTable.comparatorProperty());
            teamTable.setItems(sortedList);
        }
    }

    protected void openCreateForm() {
        TeamFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.TEAMS_FORM.fxml,
                c -> {
                    c.setManagerMode(true);
                    c.loadForCreate();
                }
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    protected void closeForm() {
        FormLoader.hideForm(formHost);
        activeFormGuard = null;
    }

    protected abstract boolean isManagerMode();


    protected void onEdit(TeamDTO team) {
        TeamFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.TEAMS_FORM.fxml,
                c -> {
                    c.setManagerMode(isManagerMode());
                    c.loadForEdit(team);
                }
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    protected void delete(TeamDTO team) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Team verwijderen");
        alert.setHeaderText("Team verwijderen");
        alert.setContentText("Ben je zeker dat je dit team wil verwijderen?");

        ButtonType yesBtn = new ButtonType("Verwijderen");
        ButtonType cancelBtn = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                try {
                    observableTeams.deleteTeam(team.teamCode());
                } catch (RuntimeException ex) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Fout");
                    error.setHeaderText("Team kon niet verwijderd worden");
                    error.setContentText(ex.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    @Override
    public boolean canNavigateAway() {
        return activeFormGuard == null || activeFormGuard.canClose();
    }
}
