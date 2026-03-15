package gui.navigation;

import main.AppContext;

public class ControllerInitializer {

    private final AppContext context;
    private final Navigator navigator;

    public ControllerInitializer(AppContext context, Navigator navigator) {
        this.context = context;
        this.navigator = navigator;
    }

    public void initialize(Object controller) {
        if (controller instanceof NavigableController navigableController) {
            navigableController.setNavigator(navigator);
            navigableController.setContext(context);
            navigableController.loadData();
        }
    }
}
