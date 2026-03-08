package main;

import domein.SiteController;
import domein.TaakController;
import exception.SiteException;
import exception.TaakException;
import security.Authorizer;
import security.Permission;
import util.OperationeleStatus;
import util.ProductieStatus;

public class MockdataSeeder {
    public static void seed(AppContext context) throws SiteException, TaakException {
        if (!Authorizer.has(Permission.SITES_BEHEREN)) {
            return;
        }

        SiteController sc = context.getSiteController();
        TaakController tc = context.getTaakController();

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

        tc.addTaak(
                "Onderhoud",
                "Maandelijks onderhoud van productiemachine",
                60
        );

        tc.addTaak(
                "Inspectie",
                "Visuele controle van assemblagelijn",
                15
        );

        tc.addTaak(
                "Herstel",
                "Vervangen van aandrijfriem",
                120
        );

        tc.addTaak(
                "Schoonmaak",
                "Reiniging van werkstation",
                30
        );

        tc.addTaak(
                "Kalibratie",
                "Kalibreren van momentsleutel",
                45
        );

        tc.addTaak(
                "Software update",
                "Firmware update van productiemachine",
                30
        );

        tc.addTaak(
                "Testprocedure",
                "Functionele test van nieuwe fietsmodellen",
                90
        );

        tc.addTaak(
                "Kwaliteitscontrole",
                "Controle van afgewerkte fietsen",
                15
        );
    }
}
