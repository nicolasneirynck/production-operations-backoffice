package gui.teams;

import gui.factories.ActionColumnFactory;
import javafx.fxml.FXML;

public class AllTeamsOverviewController extends AbstractTeamOverviewController {

    @Override
    protected void configureScreen() {
        titleLabel.setText("Teams beheren");
        addBtn.setVisible(true);
        addBtn.setManaged(true);
        configureActiesColumn();
    }

    @Override
    protected void loadTeams() {
        observableTeams.reload();
    }

    @Override
    protected void configureActiesColumn() {
        ActionColumnFactory.configureEditDeleteColumn(actiesCol, this::onEdit, this::delete);
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
