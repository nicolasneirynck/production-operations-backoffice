package dto;

import util.TaakType;

public record TaakDTO(
        long taakId,
        String taakType,
        String omschrijving,
        int duurtijd
) {}