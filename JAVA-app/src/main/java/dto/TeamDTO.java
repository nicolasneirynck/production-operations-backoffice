package dto;

import java.util.List;

public record TeamDTO(
        long teamCode,
        SiteDTO site,
        List<GebruikerDTO> teamleden
) {
}
