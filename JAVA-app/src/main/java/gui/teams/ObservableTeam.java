package gui.teams;

import dto.TeamDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.Optional;

public class ObservableTeam {

    private final ObservableList<TeamDTO> observableTeamList;
    @Getter private final FilteredList<TeamDTO> filteredTeamList;

    public ObservableTeam() {
        this.observableTeamList = FXCollections.observableArrayList();
        this.filteredTeamList = new FilteredList<>(observableTeamList, t -> true);
    }

    public void setTeam(Optional<TeamDTO> team) {
        if (team.isEmpty()) {
            observableTeamList.clear();
        } else {
            observableTeamList.setAll(team.get());
        }
    }
}
