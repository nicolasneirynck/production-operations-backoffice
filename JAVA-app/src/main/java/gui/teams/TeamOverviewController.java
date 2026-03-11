package gui.teams;

import dto.GebruikerDTO;
import dto.SiteDTO;
import dto.TeamDTO;
import gui.LayoutController;
import gui.factories.ActionColumnFactory;
import gui.navigation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.View;

import java.util.List;
import java.util.Objects;

public class TeamOverviewController implements NavigableController, NavigationGuard, TeamBeheerTypeAware {

    @FXML private VBox formHost;
    @FXML private Button addBtn;

    @FXML private TableView<TeamDTO> teamTable;
    @FXML private TableColumn<TeamDTO, String> siteCol;
    @FXML private TableColumn<TeamDTO, String> verantwoordelijkeCol;
    @FXML private TableColumn<TeamDTO, TeamDTO> medewerkersCol;
    @FXML private TableColumn<TeamDTO, TeamDTO> actiesCol;

    private AppContext context;
    @Setter private LayoutController layout;
    @Setter private Navigator navigator;
    private ClosableFormGuard activeFormGuard;
    private TeamBeheerType beheerType = TeamBeheerType.MANAGER;

    private ObservableTeams observableTeams;
    private SortedList<TeamDTO> sortedList;


    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableTeams = ctx.getObservableTeams();
    }

    @Override
    public void setBeheerType(TeamBeheerType type) {
        this.beheerType = Objects.requireNonNull(type, "beheerType mag niet null zijn");
        configureActiesColumn();
    }

    @FXML
    private void initialize() {
        configureColumns();
        configureTableLayout();
    }

    private void configureColumns(){
        siteCol.setCellValueFactory(cellData -> {
                SiteDTO site = cellData.getValue().site();
                return new SimpleStringProperty(site == null ? "-" : site.naam());
                });

        verantwoordelijkeCol.setCellValueFactory(cellData -> {
            SiteDTO site = cellData.getValue().site();
            GebruikerDTO verantwoordelijke = site == null ? null : site.verantwoordelijke();
            return new SimpleStringProperty(
                    verantwoordelijke == null ? "-" : verantwoordelijke.volledigeNaam()
            );
        });

        medewerkersCol.setCellValueFactory(cellData -> Bindings.createObjectBinding(cellData::getValue));
        medewerkersCol.setCellFactory(col -> new TeamLedenTableCell());

        medewerkersCol.setSortable(false);
        actiesCol.setSortable(false);
    }

    private void configureTableLayout(){
        siteCol.setStyle("-fx-alignment: CENTER-LEFT;");
        verantwoordelijkeCol.setStyle("-fx-alignment: CENTER;");
        medewerkersCol.setStyle("-fx-alignment: CENTER-LEFT;");
        actiesCol.setStyle("-fx-alignment: CENTER;");

        teamTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        teamTable.setFixedCellSize(84);
    }


    @Override
    public void loadData() {

        configureActiesColumn();
        configureAddButton();
        loadTeams();
        initializeTableItems();
    }

    private void configureActiesColumn() {
        if (beheerType == TeamBeheerType.VERANTWOORDELIJKE) {
            ActionColumnFactory.configureEditOnlyColumn(actiesCol, this::onEdit);
        } else {
            ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::onEdit, this::delete);
        }
    }

    private void configureAddButton() {
        boolean manager = beheerType == TeamBeheerType.MANAGER;
        addBtn.setVisible(manager);
        addBtn.setManaged(manager);
    }

    private void loadTeams() {
        if (beheerType == TeamBeheerType.MANAGER) {
            observableTeams.reload();
        } else {
            observableTeams.setSingleTeam(context.getTeamController().getMijnTeam());
        }
    }

    private void initializeTableItems() {
        if (sortedList == null) {
            sortedList = new SortedList<>(observableTeams.getFilteredTeamList());
            sortedList.comparatorProperty().bind(teamTable.comparatorProperty());
            teamTable.setItems(sortedList);
        }
    }

    @FXML
    private void onAdd() {
        TeamFormController controller = FormLoader.showForm(
                formHost, context, View.TEAMS_FORM.fxml, c ->
                {
                    c.setBeheerType(beheerType);
                    c.loadForCreate();
                });

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }


    private void onEdit(TeamDTO team) {
        TeamFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.TEAMS_FORM.fxml,
                c -> {
                    c.setBeheerType(beheerType);
                    c.loadForEdit(team);
                }
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }

    private void closeForm() {
        FormLoader.hideForm(formHost);
        activeFormGuard = null;
    }

    private void delete(TeamDTO team) {

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