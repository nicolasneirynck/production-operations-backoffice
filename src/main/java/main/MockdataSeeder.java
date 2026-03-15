package main;

import domein.services.TeamService;
import domein.services.GebruikerService;
import domein.services.SiteService;
import domein.services.TaakService;
import exception.ValidationException;
import security.Permission;
import security.SecurityContext;
import security.UserPrincipal;
import util.GebruikerStatus;
import util.OperationeleStatus;
import util.ProductieStatus;
import util.Rollen;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

public class MockdataSeeder {

    private MockdataSeeder() {
    }

    private static void seedSites(AppContext context) throws ValidationException {
        SiteService sc = context.getSiteService();

        sc.addSite(
                "Gent Plant",
                3L,
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
                4L,
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
                5L,
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
        TaakService tc = context.getTaakService();

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
        GebruikerService gc = context.getGebruikerService();

        addGebruiker(
                gc,
                "Admin",
                "System",
                LocalDate.parse("2000-10-02"),
                "Hoofdzetellaan", "1", "1000", "Brussel", "België",
                "admin@test.com",
                "",
                Rollen.ADMINISTRATOR,
                GebruikerStatus.ACTIEF,
                "admin"
        );

        addGebruiker(
                gc,
                "Manager",
                "Plant",
                LocalDate.parse("2000-10-02"),
                "Plantstraat", "2", "9000", "Gent", "België",
                "manager@test.com",
                "",
                Rollen.MANAGER,
                GebruikerStatus.ACTIEF,
                "manager"
        );

        addGebruiker(
                gc,
                "Verantwoordelijke",
                "Team",
                LocalDate.parse("2000-10-02"),
                "Teamstraat", "3", "2000", "Antwerpen", "België",
                "verantwoordelijke@test.com",
                "",
                Rollen.VERANTWOORDELIJKE,
                GebruikerStatus.ACTIEF,
                "verantwoordelijke"
        );

        addGebruiker(
                gc,
                "Verantwoordelijke",
                "Site2",
                LocalDate.parse("1995-06-10"),
                "Sitestraat", "4", "1000", "Brussel", "België",
                "verantwoordelijke2@test.com",
                "",
                Rollen.VERANTWOORDELIJKE,
                GebruikerStatus.ACTIEF,
                "verantwoordelijke2"
        );

        addGebruiker(
                gc,
                "Verantwoordelijke",
                "Site3",
                LocalDate.parse("1992-03-18"),
                "Fabriekstraat", "5", "9000", "Gent", "België",
                "verantwoordelijke3@test.com",
                "",
                Rollen.VERANTWOORDELIJKE,
                GebruikerStatus.ACTIEF,
                "verantwoordelijke3"
        );

        addGebruiker(
                gc,
                "Jaap",
                "Jan",
                LocalDate.parse("1988-03-14"),
                "Stationsstraat", "12", "9000", "Gent", "België",
                "jan.jaap@koga.com",
                "0470123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Stroeykens",
                "Mario",
                LocalDate.parse("1992-07-21"),
                "Kortrijksesteenweg", "85", "9000", "Gent", "België",
                "mario@koga.com",
                "0471123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Verschaeren",
                "Yari",
                LocalDate.parse("1999-01-17"),
                "Brusselsesteenweg", "201", "9090", "Melle", "België",
                "yari@koga.com",
                "0472123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "De Cat",
                "Nathan",
                LocalDate.parse("1991-11-05"),
                "Dendermondsesteenweg", "44", "9040", "Sint-Amandsberg", "België",
                "nathan.decat@koga.com",
                "0473123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Saliba",
                "Nathan",
                LocalDate.parse("1990-04-09"),
                "Antwerpsesteenweg", "310", "9040", "Sint-Amandsberg", "België",
                "nathan.saliba@koga.com",
                "0474123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Mourinho",
                "Jose",
                LocalDate.parse("1978-02-26"),
                "Kasteellaan", "18", "9000", "Gent", "België",
                "jose@koga.com",
                "0475123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Vanaken",
                "Hans",
                LocalDate.parse("1987-08-12"),
                "Ledebergstraat", "9", "9050", "Ledeberg", "België",
                "hans@koga.com",
                "0476123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Lammens",
                "Senne",
                LocalDate.parse("1996-06-03"),
                "Schoolstraat", "27", "9820", "Merelbeke", "België",
                "senne@koga.com",
                "0477123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Mignolet",
                "Simon",
                LocalDate.parse("1989-10-22"),
                "Oude Brusselseweg", "61", "9050", "Gentbrugge", "België",
                "simon@koga.com",
                "0478123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Ancelotti",
                "Carlo",
                LocalDate.parse("1980-09-01"),
                "Keizer Karelstraat", "103", "9000", "Gent", "België",
                "carlo@koga.com",
                "0479123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Garcia",
                "Pablo",
                LocalDate.parse("1993-12-11"),
                "Hundelgemsesteenweg", "55", "9050", "Ledeberg", "België",
                "pablo@koga.com",
                "0480123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Boon",
                "Pieter",
                LocalDate.parse("1994-05-19"),
                "Meulestedekaai", "8", "9000", "Gent", "België",
                "pieter@koga.com",
                "0481123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );

        addGebruiker(
                gc,
                "Pieters",
                "Jan",
                LocalDate.parse("1986-01-30"),
                "Nieuwstraat", "77", "9030", "Mariakerke", "België",
                "jan.pieters@koga.com",
                "0482123456",
                Rollen.WERKNEMER,
                GebruikerStatus.ACTIEF,
                "test123"
        );
    }

    private static void addGebruiker(GebruikerService gc, String naam, String voornaam, LocalDate geboortedatum,
                                     String straat, String nummer, String postcode, String gemeente, String land,
                                     String email, String gsm, Rollen rol, GebruikerStatus status, String wachtwoord) {
        gc.addGebruiker(
                naam, voornaam, geboortedatum, straat, nummer, postcode, gemeente, land,
                email, gsm, rol, status, wachtwoord
        );
    }

    private static void seedTeams(AppContext context) throws ValidationException {
        TeamService teamService = context.getTeamService();

        teamService.addTeam(1L, List.of(6L, 7L, 8L, 9L, 10L));
        teamService.addTeam(2L, List.of(11L, 12L, 13L));
        teamService.addTeam(3L, List.of(14L, 15L, 16L));
    }

    public static void seed(AppContext context) throws ValidationException {
        SecurityContext.login(
                new UserPrincipal(
                        0L,
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
