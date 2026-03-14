package gui.teams.controllers;

import domein.controllers.AlleTeamsService;
import dto.SiteDTO;
import exception.ValidationException;
import gui.teams.ObservableTeams;

import java.util.List;

public class ManagerTeamFormController extends TeamFormController {
    private AlleTeamsService alleTeamsService;
    private ObservableTeams observableTeams;

    @Override
    public void setContext(main.AppContext ctx) {
        super.setContext(ctx);
        this.alleTeamsService = ctx.getAlleTeamsService();
        this.observableTeams = ctx.getObservableTeams();
    }

    @Override
    protected boolean isManagerMode() {
        return true;
    }

    @Override
    protected void saveTeam(SiteDTO site, List<Long> medewerkerIds) throws ValidationException {
        if (editingTeamId == null) {
            alleTeamsService.addTeam(site.siteId(), medewerkerIds);
        } else {
            alleTeamsService.updateTeam(editingTeamId, medewerkerIds);
        }

        observableTeams.setTeams(alleTeamsService.getAllTeams());
    }
}
