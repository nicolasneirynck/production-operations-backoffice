package dto;

import util.OperationeleStatus;
import util.ProductieStatus;

public record SiteDTO(
        long siteId,
        String naam,
        String locatie,
        int capaciteit,
        OperationeleStatus operationeleStatus,
        ProductieStatus productieStatus
) {}
