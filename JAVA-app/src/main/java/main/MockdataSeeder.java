package main;

import domein.GebruikerController;
import domein.SiteController;
import domein.TaakController;
import domein.TeamController;
import exception.SiteException;
import exception.TaakException;
import exception.TeamException;
import util.GebruikerStatus;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.util.List;

public class MockdataSeeder {
    public static void seed(AppContext context) throws SiteException, TaakException, TeamException {

        SiteController sc = context.getSiteController();
        TaakController tc = context.getTaakController();
        TeamController teamController = context.getTeamController();
        GebruikerController gebruikerController = context.getGebruikerController();


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

        gebruikerController.addGebruiker(
                "jan.jaap@koga.com",
                "Jan Jaap",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "mario@koga.com",
                "Mario Stroeykens",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "yari@koga.com",
                "Yari Verschaeren",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "nathan.decat@koga.com",
                "Nathan De Cat",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "nathan.saliba@koga.com",
                "Nathan Saliba",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "jose@koga.com",
                "Jose Mourinho",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "hans@koga.com",
                "Hans Vanaken",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "senne@koga.com",
                "Senne Lammens",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "simon@koga.com",
                "Simon Mignolet",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "carlo@koga.com",
                "Carlo Ancelotti",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "pablo@koga.com",
                "Pablo Garcia",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "pieter@koga.com",
                "Pieter Boon",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
        );

        gebruikerController.addGebruiker(
                "jan.pieters@koga.com",
                "Jan Pieters",
                "test123",
                GebruikerStatus.ACTIEF,
                Rollen.WERKNEMER
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

        teamController.addTeam(1L, List.of(1L, 2L, 3L, 4L, 5L));
        teamController.addTeam(2L, List.of(6L, 7L, 8L, 9L));
        teamController.addTeam(3L, List.of(10L, 11L, 12L, 13L));
    }
}
