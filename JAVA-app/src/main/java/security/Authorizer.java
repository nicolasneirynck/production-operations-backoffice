package security;

import exception.AccessDeniedException;
import util.View;

public class Authorizer {
    public static UserPrincipal require(Permission p) {
        UserPrincipal user = SecurityContext.getUser();
        if (user == null || !user.permissions().contains(p)) {
            throw new AccessDeniedException("Je hebt niet de nodige permission: " + p);
        }

        return user;
    }

    public static void require(View view) {
        if (!hasAccess(view)) {
            throw new AccessDeniedException();
        }
    }

    public static boolean hasAccess(View view) {
        if (view.requiredPermission == null) {
            return true;
        }
        return has(view.requiredPermission);
    }

    public static boolean has(Permission p) {
        UserPrincipal u = SecurityContext.getUser();
        return u != null && u.permissions().contains(p);
    }




}
