package dto;

import domein.Gebruiker;
import util.OperationeleStatus;
import util.ProductieStatus;

public record SiteDTO(
        long siteId,
        String naam,
        LocatieDTO locatie,
        int capaciteit,
        OperationeleStatus operationeleStatus,
        ProductieStatus productieStatus,
        GebruikerDTO verantwoordelijke
) {

}
