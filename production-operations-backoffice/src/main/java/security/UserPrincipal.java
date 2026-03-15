package security;

import util.Rollen;

import java.util.Set;

public record UserPrincipal(
        long gebruikerId, // nodig voor teambeheer van verantwoordelijke
        String naam,
        String voornaam,
        Rollen rol,
        Set<Permission> permissions
) {}