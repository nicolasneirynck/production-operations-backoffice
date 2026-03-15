package dto;

import util.Rollen;

public record AuthenticatedUserDTO(
        long gebruikerId,
        String naam,
        String voornaam,
        Rollen rol
) {
}
