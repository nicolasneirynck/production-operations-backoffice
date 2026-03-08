package security;

import util.Rollen;

import java.util.Set;

public record UserPrincipal(
        // TODO: add andere dingen zoals naam, email, etc.
        Rollen rol,
        Set<Permission> permissions
) {}