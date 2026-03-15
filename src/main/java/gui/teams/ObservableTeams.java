package gui.teams;

import dto.TeamDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import java.util.List;

public class ObservableTeams {

    private final ObservableList<TeamDTO> observableTeamList;
    @Getter private final FilteredList<TeamDTO> filteredTeamList;

    public ObservableTeams() {
        this.observableTeamList = FXCollections.observableArrayList();
        this.filteredTeamList = new FilteredList<>(observableTeamList, t -> true);
    }

    public void setTeams(List<TeamDTO> teams) {
        observableTeamList.setAll(teams);
    }

    public void removeTeam(String teamCode) {
        observableTeamList.removeIf(t -> t.teamCode().equals(teamCode));
    }
}
