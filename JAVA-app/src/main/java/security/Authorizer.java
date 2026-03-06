package security;

import exception.AccessDeniedException;

public class Authorizer {
    public static void require(Permission p) {
        UserPrincipal u = SecurityContext.getUser();
        if (u == null || !u.permissions().contains(p)) {
            throw new AccessDeniedException("Je hebt niet de nodige permission: " + p);
        }
    }

    public static boolean has(Permission p) {
        UserPrincipal u = SecurityContext.getUser();
        return u != null && u.permissions().contains(p);
    }
}
