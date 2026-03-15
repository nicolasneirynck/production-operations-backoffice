package gui.teams.controllers;

import domein.services.MijnTeamService;
import dto.SiteDTO;
import exception.ValidationException;
import gui.teams.ObservableTeam;

import java.util.List;

public class MijnTeamFormController extends TeamFormController {
    private MijnTeamService mijnTeamService;
    private ObservableTeam observableTeam;

    @Override
    public void setContext(main.AppContext ctx) {
        super.setContext(ctx);
        this.mijnTeamService = ctx.getMijnTeamService();
        this.observableTeam = ctx.getObservableTeam();
    }

    @Override
    protected boolean isManagerMode() {
        return false;
    }

    @Override
    protected void saveTeam(SiteDTO site, List<Long> medewerkerIds) throws ValidationException {
        if (editingTeamId == null) {
            throw new IllegalStateException("Als verantwoordelijke kan je geen team aanmaken.");
        }

        mijnTeamService.updateMijnTeam(editingTeamId, medewerkerIds);
        observableTeam.setTeam(mijnTeamService.getMijnTeam());
    }
}
