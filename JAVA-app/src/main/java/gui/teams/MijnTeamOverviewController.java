package gui.teams;

import gui.factories.ActionColumnFactory;

public class MijnTeamOverviewController extends AbstractTeamOverviewController {

    @Override
    protected void configureScreen() {
        titleLabel.setText("Mijn team");
        addBtn.setVisible(false);
        addBtn.setManaged(false);
        configureActiesColumn();
    }

    @Override
    protected void loadTeams() {
        observableTeams.setSingleTeam(context.getTeamController().getMijnTeam());
    }

    @Override
    protected void configureActiesColumn() {
        ActionColumnFactory.configureEditOnlyColumn(actiesCol, this::onEdit);
    }

    @Override
    protected boolean isManagerMode() {
        return false;
    }
}
