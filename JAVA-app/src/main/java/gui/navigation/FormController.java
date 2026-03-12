package gui.navigation;

import main.AppContext;

public interface FormController {
    void setContext(AppContext context);
    default void loadData(){};
    void setOnClose(Runnable onClose);

}
