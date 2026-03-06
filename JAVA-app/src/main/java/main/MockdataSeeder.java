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
                "Kortrijksesteenweg",
                "80",
                "9000",
                "Gent",
                "België",
                120,
                OperationeleStatus.ACTIEF,
                ProductieStatus.GEZOND
        );

        sc.addSite(
                "Antwerp Hub",
                "Noorderlaan",
                "101",
                "2030",
                "Antwerpen",
                "België",
                80,
                OperationeleStatus.ACTIEF,
                ProductieStatus.PROBLEMEN
        );

        sc.addSite(
                "Brussels Factory",
                "Industrielaan",
                "12",
                "1000",
                "Brussel",
                "België",
                200,
                OperationeleStatus.NON_ACTIEF,
                ProductieStatus.OFFLINE
        );
    }
}
