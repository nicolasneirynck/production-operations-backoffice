package main;

import domein.SiteController;
import exception.SiteException;
import util.OperationeleStatus;
import util.ProductieStatus;

public class MockdataSeeder {
    public static void seed(AppContext context) throws SiteException {

        SiteController sc = context.getSiteController();

        sc.addSite(
                "Gent Plant",
                "Gent",
                120,
            OperationeleStatus.ACTIEF,
                ProductieStatus.GEZOND
        );

        sc.addSite(
                "Antwerp Hub",
                "Antwerpen",
                80,
                OperationeleStatus.ACTIEF,
                ProductieStatus.PROBLEMEN
        );

        sc.addSite(
                "Brussels Factory",
                "Brussel",
                200,
                OperationeleStatus.NON_ACTIEF,
                ProductieStatus.OFFLINE
        );
    }
}
