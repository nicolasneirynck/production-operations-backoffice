package gui.teams;

import domein.TeamController;
import dto.TeamDTO;
import exception.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import java.util.List;

public class ObservableTeams {

    private final TeamController controller;
    private final ObservableList<TeamDTO> observableTeamList;
    @Getter
    private final FilteredList<TeamDTO> filteredTeamList;

    public ObservableTeams(TeamController controller) {
        this.controller = controller;

        this.observableTeamList = FXCollections.observableArrayList();
        observableTeamList.addAll(controller.getAllTeams());

        this.filteredTeamList = new FilteredList<>(observableTeamList, t -> true);
    }

    public void addTeam(long siteId, List<Long> werknemerIds) throws ValidationException {
        controller.addTeam(siteId, werknemerIds);
        reload();
    }

    public void editTeam(long teamCode, List<Long> werknemerIds) throws ValidationException {
        controller.updateTeam(teamCode, werknemerIds);
        reload();
    }

    public void deleteTeam(long teamCode) {
        controller.deleteTeam(teamCode);
        observableTeamList.removeIf(t -> t.teamCode() == teamCode);
    }

    public void reload() {
        observableTeamList.setAll(controller.getAllTeams());
    }

    private int indexOf(long teamCode) {
        for (int i = 0; i < observableTeamList.size(); i++) {
            if (observableTeamList.get(i).teamCode() == teamCode)
                return i;
        }
        return -1;
    }
}
