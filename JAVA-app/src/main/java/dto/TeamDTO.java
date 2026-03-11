package dto;

import java.util.List;

public record TeamDTO(
        long teamCode,
        SiteDTO site,
        GebruikerDTO verantwoordelijke, // zinvol om gebruikersDTO hier mee te geven?
        List<GebruikerDTO> teamleden
) {
}
