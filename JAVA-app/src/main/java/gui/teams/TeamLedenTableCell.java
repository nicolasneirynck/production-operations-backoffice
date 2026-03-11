package gui.teams;

import dto.GebruikerDTO;
import dto.TeamDTO;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class TeamLedenTableCell extends TableCell<TeamDTO, TeamDTO> {

    private static final int MAX_VISIBLE_LEDEN = 6;

    private final FlowPane badgesPane = new FlowPane();

    public TeamLedenTableCell() {
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

        List<GebruikerDTO> leden = team.teamleden();
        int visible = Math.min(MAX_VISIBLE_LEDEN, leden.size());

        for (int i = 0; i < visible; i++) {
            badgesPane.getChildren().add(createBadge(leden.get(i).volledigeNaam()));
        }

        if (leden.size() > MAX_VISIBLE_LEDEN) {
            int remaining = leden.size() - MAX_VISIBLE_LEDEN;
            Label moreBadge = createBadge("+" + remaining);
            moreBadge.getStyleClass().add("employee-badge-more");
            badgesPane.getChildren().add(moreBadge);
        }

        setGraphic(badgesPane);
    }

    private Label createBadge(String text) {
        Label badge = new Label(text);
        badge.getStyleClass().add("employee-badge");
        return badge;
    }
}
