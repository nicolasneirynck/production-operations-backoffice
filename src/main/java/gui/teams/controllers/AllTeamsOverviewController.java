package gui.teams.controllers;

import domein.services.AlleTeamsService;
import gui.factories.ActionColumnFactory;
import gui.teams.ObservableTeams;
import javafx.fxml.FXML;
import javafx.collections.transformation.FilteredList;
import dto.TeamDTO;
import main.AppContext;

public class AllTeamsOverviewController extends TeamOverviewController {
    private AlleTeamsService alleTeamsService;
    private ObservableTeams observableTeams;

    @Override
    public void setContext(AppContext ctx) {
        super.setContext(ctx);
        this.alleTeamsService = ctx.getAlleTeamsService();
        this.observableTeams = ctx.getObservableTeams();
    }

    @Override
    protected void configureScreen() {
        titleLabel.setText("Teams beheren");
        addBtn.setVisible(true);
        addBtn.setManaged(true);
        configureActiesColumn();
    }

    @Override
    protected void loadTeams() {
        observableTeams.setTeams(alleTeamsService.getAllTeams());
    }

    @Override
    protected void configureActiesColumn() {
        ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::onEdit, this::delete);
    }

    @Override
    protected FilteredList<TeamDTO> getFilteredTeams() {
        return observableTeams.getFilteredTeamList();
    }

    @Override
    protected void deleteTeam(String teamCode) {
        alleTeamsService.deleteTeam(teamCode);
        observableTeams.removeTeam(teamCode);
    }

    @Override
    protected boolean isManagerMode() {
        return true;
    }

    @FXML
    private void onAdd() {
        openCreateForm();
    }
}
