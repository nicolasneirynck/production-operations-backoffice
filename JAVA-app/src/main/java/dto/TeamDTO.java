package dto;

import java.util.List;

public record TeamDTO(
        String teamCode,
        SiteDTO site,
        List<GebruikerDTO> teamleden
) {
}
