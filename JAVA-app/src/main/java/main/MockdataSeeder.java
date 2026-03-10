package main;

import domein.GebruikerController;
import domein.controllers.SiteController;
import domein.controllers.TaakController;
import exception.SiteException;
import exception.TaakException;
import security.Permission;
import security.SecurityContext;
import security.UserPrincipal;
import util.GebruikerStatus;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.util.EnumSet;

public class MockdataSeeder {
    private static void seedSites(AppContext context) throws SiteException {
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

    private static void seedTaken(AppContext context) throws TaakException {
        TaakController tc = context.getTaakController();

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

    private static void seedGebruikers(AppContext context) {
        GebruikerController gc = context.getGebruikerController();

        gc.addGebruiker(1, "admin", "admin", "02/10/2000", "België", "admin@test.com", "", Rollen.ADMINISTRATOR, GebruikerStatus.ACTIEF, "admin");
        gc.addGebruiker(2, "manager", "manager", "02/10/2000", "België", "manager@test.com", "", Rollen.MANAGER, GebruikerStatus.ACTIEF, "manager");
        gc.addGebruiker(3, "verantwoordelijke", "verantwoordelijke", "02/10/2000", "België", "verantwoordelijke@test.com", "", Rollen.VERANTWOORDELIJKE, GebruikerStatus.ACTIEF, "verantwoordelijke");
        gc.addGebruiker(4, "Bakker", "Jan", "02/10/2000", "België", "jan.bakker@gmail.com", "", Rollen.MANAGER, GebruikerStatus.ACTIEF, "pass123");
    }

    public static void seed(AppContext context) throws SiteException, TaakException {
        // prevent seeding inside of production
       // if (!Boolean.getBoolean("seed.devUser")) return;

        SecurityContext.login(new UserPrincipal("temp", "temp", Rollen.ADMINISTRATOR, EnumSet.allOf(Permission.class)));

        seedSites(context);
        seedTaken(context);
        seedGebruikers(context);

        SecurityContext.logout();
    }
}
