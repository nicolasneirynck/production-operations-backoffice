package security;

import util.Rollen;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class RolePermissions {
    private static final Map<Rollen, Set<Permission>> MAP = Map.of(
            Rollen.ADMINISTRATOR, EnumSet.of(Permission.GEBRUIKERS_BEHEREN),
            Rollen.MANAGER, EnumSet.of(Permission.TEAMS_BEHEREN, Permission.SITES_BEHEREN),
            Rollen.VERANTWOORDELIJKE, EnumSet.of(Permission.TEAMS_BEHEREN),
            Rollen.WERKNEMER, Set.of()
    );

    public static Set<Permission> getPermissions(Rollen rol) {
        EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);
        permissions.addAll(MAP.getOrDefault(rol, Set.of()));

        return permissions;
    }
}
