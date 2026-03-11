package gui.teams;

import dto.GebruikerDTO;
import dto.SiteDTO;
import dto.TeamDTO;
import gui.LayoutController;
import gui.factories.ActionColumnFactory;
import gui.navigation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;
import util.View;

public class TeamOverviewController implements NavigableController, NavigationGuard {

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
    private ObservableTeams observableTeams;

    private SortedList<TeamDTO> sortedList;

    private ClosableFormGuard activeFormGuard;


    @Override
    public void setContext(AppContext ctx) {
        this.context = ctx;
        this.observableTeams = ctx.getObservableTeams();
    }

    @FXML
    private void initialize() {
        siteCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().site().naam()));
        verantwoordelijkeCol.setCellValueFactory(cellData -> {
            var site = cellData.getValue().site();
            var verantwoordelijke = site == null ? null : site.verantwoordelijke();
            return new SimpleStringProperty(
                    verantwoordelijke == null ? "-" : verantwoordelijke.volledigeNaam()
            );
        });

        medewerkersCol.setCellValueFactory(cellData -> Bindings.createObjectBinding(cellData::getValue));
        medewerkersCol.setCellFactory(col -> new TableCell<>() {
            private final FlowPane badgesPane = new FlowPane();

            {
                badgesPane.setHgap(8);
                badgesPane.setVgap(8);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(TeamDTO team, boolean empty) {
                super.updateItem(team, empty);

                if (empty || team == null || team.teamleden() == null || team.teamleden().isEmpty()) {
                    setGraphic(null);
                    return;
                }

                badgesPane.getChildren().clear();

                badgesPane.setPrefWrapLength(getTableColumn().getWidth() - 30);

                var leden = team.teamleden();

                int maxVisible = 6;

                int visible = Math.min(maxVisible, leden.size());

                for (int i = 0; i < visible; i++) {
                    GebruikerDTO medewerker = leden.get(i);

                    Label badge = new Label(medewerker.volledigeNaam());
                    badge.getStyleClass().add("employee-badge");

                    badgesPane.getChildren().add(badge);
                }

                if (leden.size() > maxVisible) {
                    int remaining = leden.size() - maxVisible;

                    Label moreBadge = new Label("+" + remaining);
                    moreBadge.getStyleClass().add("employee-badge");
                    moreBadge.getStyleClass().add("employee-badge-more");

                    badgesPane.getChildren().add(moreBadge);
                }

                setGraphic(badgesPane);
            }
        });

        ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::edit, this::delete);

        siteCol.setStyle("-fx-alignment: CENTER-LEFT;");
        verantwoordelijkeCol.setStyle("-fx-alignment: CENTER;");
        medewerkersCol.setStyle("-fx-alignment: CENTER-LEFT;");
        actiesCol.setStyle("-fx-alignment: CENTER;");

        medewerkersCol.setSortable(false);
        actiesCol.setSortable(false);

        teamTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        teamTable.setFixedCellSize(84);    }

    @Override
    public void loadData() {
        observableTeams.reload();

        if (sortedList == null) {
            sortedList = new SortedList<>(observableTeams.getFilteredTeamList());
            sortedList.comparatorProperty().bind(teamTable.comparatorProperty());
            teamTable.setItems(sortedList);
        }
    }

    @FXML
    private void onAdd() {
        TeamFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.TEAMS_FORM.fxml,
                TeamFormController::loadForCreate
        );

        controller.setOnClose(this::closeForm);
        activeFormGuard = controller;
    }


    private void edit(TeamDTO team) {
        TeamFormController controller = FormLoader.showForm(
                formHost,
                context,
                View.TEAMS_FORM.fxml,
                c -> c.loadForEdit(team)
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

                    observableTeams.reload();

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