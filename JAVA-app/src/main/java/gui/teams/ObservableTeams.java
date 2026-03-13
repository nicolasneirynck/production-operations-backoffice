package gui.teams;

import domein.controllers.TeamController;
import dto.TeamDTO;
import exception.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import java.util.List;
import java.util.Optional;

public class ObservableTeams {

    private final TeamController controller;
    private final ObservableList<TeamDTO> observableTeamList;
    @Getter private final FilteredList<TeamDTO> filteredTeamList;

    public ObservableTeams(TeamController controller) {
        this.controller = controller;
        this.observableTeamList = FXCollections.observableArrayList();
        this.filteredTeamList = new FilteredList<>(observableTeamList, t -> true);
    }

    public void addTeam(long siteId, List<Long> werknemerIds) throws ValidationException {
        controller.addTeam(siteId, werknemerIds);
        reload();
    }

    public void updateTeam(String teamCode, List<Long> medewerkerIds) throws ValidationException {
        controller.updateTeam(teamCode, medewerkerIds);
        reload();
    }

    public void deleteTeam(String teamCode) {
        controller.deleteTeam(teamCode);
        observableTeamList.removeIf(t -> t.teamCode().equals(teamCode));
    }

    public void replaceWithSingleTeam(Optional<TeamDTO> team) {
        if (team.isEmpty()) {
            observableTeamList.clear();
        } else {
            observableTeamList.setAll(team.get());
        }
    }

    public void reload() {
        observableTeamList.setAll(controller.getAllTeams());
    }

}
