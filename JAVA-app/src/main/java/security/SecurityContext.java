package security;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SecurityContext {
    private static final ObjectProperty<UserPrincipal> currentUser = new SimpleObjectProperty<>(null);

    public static UserPrincipal getUser() { return currentUser.get(); }
    public static ReadOnlyObjectProperty<UserPrincipal> userProperty() { return currentUser; }

    public static void login(UserPrincipal user) { currentUser.set(user); }
    public static void logout() { currentUser.set(null); }
}
