package dto;

import util.TaakType;

public record TaakDTO(
        long taakId,
        TaakType taakType,
        String omschrijving,
        int duurtijd
) {}