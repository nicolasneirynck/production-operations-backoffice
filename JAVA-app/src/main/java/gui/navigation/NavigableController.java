package gui.navigation;

import main.AppContext;

public interface NavigableController {
    void setNavigator(Navigator navigator);
    void setContext(AppContext context);
}