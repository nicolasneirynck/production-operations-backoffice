package gui.teams;

import dto.GebruikerDTO;
import dto.SiteDTO;
import dto.TeamDTO;
import gui.LayoutController;
import gui.navigation.NavigableController;
import gui.navigation.Navigator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import main.AppContext;

public class TeamOverviewController implements NavigableController {

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
        actiesCol.setCellValueFactory(cellData -> Bindings.createObjectBinding(cellData::getValue));

        medewerkersCol.setCellFactory(col -> new TableCell<>() {
            private final FlowPane chipsPane = new FlowPane();
            {
                chipsPane.setHgap(8);
                chipsPane.setVgap(8);
            }

            @Override
            protected void updateItem(TeamDTO team, boolean empty) {
                super.updateItem(team, empty);

                if (empty || team == null) {
                    setGraphic(null);
                    return;
                }

                chipsPane.getChildren().clear();

                for (GebruikerDTO medewerker : team.teamleden()) {
                    javafx.scene.control.Label chip = new javafx.scene.control.Label(medewerker.volledigeNaam());
                    chip.getStyleClass().add("chip");
                    chipsPane.getChildren().add(chip);
                }

                setGraphic(chipsPane);
            }
        });

        actiesCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");
            private final HBox box = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().addAll("icon-button", "danger-button");

                editBtn.setOnAction(e -> {
                    TeamDTO team = getTableRow().getItem();
                    if (team != null) {
                        onEdit(team);
                    }
                });

                deleteBtn.setOnAction(e -> {
                    TeamDTO team = getTableRow().getItem();
                    if (team != null) {
                        onDelete(team);
                    }
                });
            }

            @Override
            protected void updateItem(TeamDTO team, boolean empty) {
                super.updateItem(team, empty);
                setGraphic(empty || team == null ? null : box);
            }
        });
    }

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
        // navigator.navigateTo(...);
        System.out.println("Nieuw team toevoegen");
    }


    private void onEdit(TeamDTO team) {
        System.out.println("Team bewerken: " + team.teamCode());
    }

    private void onDelete(TeamDTO team) {
        System.out.println("Team verwijderen: " + team.teamCode());
    }
}