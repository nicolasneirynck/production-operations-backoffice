package gui.teams.controllers;

import domein.controllers.MijnTeamService;
import dto.TeamDTO;
import gui.factories.ActionColumnFactory;
import gui.teams.ObservableTeam;
import javafx.collections.transformation.FilteredList;
import main.AppContext;

public class MijnTeamOverviewController extends TeamOverviewController {
    private MijnTeamService mijnTeamService;
    private ObservableTeam observableTeam;

    @Override
    public void setContext(AppContext ctx) {
        super.setContext(ctx);
        this.mijnTeamService = ctx.getMijnTeamService();
        this.observableTeam = ctx.getObservableTeam();
    }

    @Override
    protected void configureScreen() {
        titleLabel.setText("Mijn team");
        addBtn.setVisible(false);
        addBtn.setManaged(false);
        configureActiesColumn();
    }

    @Override
    protected void loadTeams() {
        observableTeam.setTeam(mijnTeamService.getMijnTeam());
    }

    @Override
    protected void configureActiesColumn() {
        ActionColumnFactory.configureEditOnlyColumn(actiesCol, this::onEdit);
    }

    @Override
    protected FilteredList<TeamDTO> getFilteredTeams() {
        return observableTeam.getFilteredTeamList();
    }

    @Override
    protected void removeTeamFromState(String teamCode) {
        throw new UnsupportedOperationException("Mijn team kan niet verwijderd worden.");
    }

    @Override
    protected boolean isManagerMode() {
        return false;
    }
}
