package main;

import domein.GebruikerController;
import domein.controllers.TeamController;
import domein.controllers.SiteController;
import domein.controllers.TaakController;
import exception.ValidationException;
import security.Permission;
import security.SecurityContext;
import security.UserPrincipal;
import util.GebruikerStatus;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.util.EnumSet;
import java.util.List;

public class MockdataSeeder {

    private MockdataSeeder() {
    }

    private static void seedSites(AppContext context) throws ValidationException {
        SiteController sc = context.getSiteController();

        sc.addSite(
                "Gent Plant",
                3L,   // verantwoordelijke
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
                null,   // geen verantwoordelijke
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
                3L,   // zelfde verantwoordelijke kan meerdere sites hebben
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

    private static void seedTaken(AppContext context) throws ValidationException {
        TaakController tc = context.getTaakController();

        tc.addTaak("Onderhoud", "Maandelijks onderhoud van productiemachine", 60);
        tc.addTaak("Inspectie", "Visuele controle van assemblagelijn", 15);
        tc.addTaak("Herstel", "Vervangen van aandrijfriem", 120);
        tc.addTaak("Schoonmaak", "Reiniging van werkstation", 30);
        tc.addTaak("Kalibratie", "Kalibreren van momentsleutel", 45);
        tc.addTaak("Software update", "Firmware update van productiemachine", 30);
        tc.addTaak("Testprocedure", "Functionele test van nieuwe fietsmodellen", 90);
        tc.addTaak("Kwaliteitscontrole", "Controle van afgewerkte fietsen", 15);
    }

    private static void seedGebruikers(AppContext context) {
        GebruikerController gc = context.getGebruikerController();

        gc.addGebruiker(
                1,
                "Admin",
                "System",
                "2000-10-02",
                "Hoofdzetel België",
                "admin@test.com",
                "",
                Rollen.ADMINISTRATOR,
                GebruikerStatus.ACTIEF,
                "admin"
        );

        gc.addGebruiker(
                2,
                "Manager",
                "Plant",
                "2000-10-02",
                "Gent, België",
                "manager@test.com",
                "",
                Rollen.MANAGER,
                GebruikerStatus.ACTIEF,
                "manager"
        );

        gc.addGebruiker(
                3,
                "Verantwoordelijke",
                "Team",
                "2000-10-02",
                "Antwerpen, België",
                "verantwoordelijke@test.com",
                "",
                Rollen.VERANTWOORDELIJKE,
                GebruikerStatus.ACTIEF,
                "verantwoordelijke"
        );

        gc.addGebruiker(
                1001,
                "Jaap",
                "Jan",
                "1988-03-14",
                "Stationsstraat 12, 9000 Gent",
                "jan.jaap@koga.com",
                "0470123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1002,
                "Stroeykens",
                "Mario",
                "1992-07-21",
                "Kortrijksesteenweg 85, 9000 Gent",
                "mario@koga.com",
                "0471123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1003,
                "Verschaeren",
                "Yari",
                "1999-01-17",
                "Brusselsesteenweg 201, 9090 Melle",
                "yari@koga.com",
                "0472123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1004,
                "De Cat",
                "Nathan",
                "1991-11-05",
                "Dendermondsesteenweg 44, 9040 Sint-Amandsberg",
                "nathan.decat@koga.com",
                "0473123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1005,
                "Saliba",
                "Nathan",
                "1990-04-09",
                "Antwerpsesteenweg 310, 9040 Sint-Amandsberg",
                "nathan.saliba@koga.com",
                "0474123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1006,
                "Mourinho",
                "Jose",
                "1978-02-26",
                "Kasteellaan 18, 9000 Gent",
                "jose@koga.com",
                "0475123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1007,
                "Vanaken",
                "Hans",
                "1987-08-12",
                "Ledebergstraat 9, 9050 Ledeberg",
                "hans@koga.com",
                "0476123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1008,
                "Lammens",
                "Senne",
                "1996-06-03",
                "Schoolstraat 27, 9820 Merelbeke",
                "senne@koga.com",
                "0477123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1009,
                "Mignolet",
                "Simon",
                "1989-10-22",
                "Oude Brusselseweg 61, 9050 Gentbrugge",
                "simon@koga.com",
                "0478123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1010,
                "Ancelotti",
                "Carlo",
                "1980-09-01",
                "Keizer Karelstraat 103, 9000 Gent",
                "carlo@koga.com",
                "0479123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1011,
                "Garcia",
                "Pablo",
                "1993-12-11",
                "Hundelgemsesteenweg 55, 9050 Ledeberg",
                "pablo@koga.com",
                "0480123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1012,
                "Boon",
                "Pieter",
                "1994-05-19",
                "Meulestedekaai 8, 9000 Gent",
                "pieter@koga.com",
                "0481123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        gc.addGebruiker(
                1013,
                "Pieters",
                "Jan",
                "1986-01-30",
                "Nieuwstraat 77, 9030 Mariakerke",
                "jan.pieters@koga.com",
                "0482123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );
    }

    private static void seedTeams(AppContext context) throws ValidationException {
        TeamController teamController = context.getTeamController();

        teamController.addTeam(1L, List.of(5L, 6L, 7L, 8L, 9L));
        teamController.addTeam(2L, List.of(10L, 11L, 12L));
        teamController.addTeam(3L, List.of(13L, 14L, 15L));
    }

    public static void seed(AppContext context) throws ValidationException {
        SecurityContext.login(
                new UserPrincipal(
                        "temp",
                        "temp",
                        Rollen.ADMINISTRATOR,
                        EnumSet.allOf(Permission.class)
                )
        );

        try {
            seedGebruikers(context);
            seedSites(context);
            seedTaken(context);
            seedTeams(context);
        } finally {
            SecurityContext.logout();
        }
    }
}