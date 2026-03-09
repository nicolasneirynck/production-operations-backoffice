package security;

import util.Rollen;

import java.util.Set;

public record UserPrincipal(
        String naam,
        String voornaam,
        Rollen rol,
        Set<Permission> permissions
) {}