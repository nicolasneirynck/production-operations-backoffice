package gui;

import main.AppContext;

public interface FormController {
    void setContext(AppContext context);
    void setOnClose(Runnable onClose);
    default void loadData(){};
}
