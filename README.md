# Production Operations Backoffice

JavaFX backoffice-applicatie voor het beheren van productie-operaties. De applicatie laat toe om gebruikers, sites, taken en teams te beheren binnen een desktopomgeving met rolgebaseerde toegang.

## Functionaliteit

- aanmelden met verschillende gebruikersrollen
- beheer van gebruikers
- beheer van productiesites
- beheer van taken
- beheer van teams en teamleden
- rolgebaseerde rechten via de securitylaag
- automatische mockdata bij het opstarten

## Technologie

- Java 21
- Maven
- JavaFX 21
- EclipseLink JPA
- MySQL
- JUnit 5
- Mockito

## Projectstructuur

- `src/main/java/domein`: domeinlogica, entiteiten en services
- `src/main/java/gui`: JavaFX controllers, navigatie en observable viewmodellen
- `src/main/java/repository`: data access laag
- `src/main/java/security`: authenticatie, autorisatie en permissies
- `src/main/resources/gui`: FXML views
- `src/test/java`: unit tests

## Vereisten

Voor je de applicatie opstart, heb je lokaal nodig:

- JDK 21
- Maven 3.9+
- MySQL server

## Databaseconfiguratie

De JPA-config staat in [src/main/resources/META-INF/persistence.xml](/Users/nicolas/Documents/WorkspaceIntelliJ/SDPII/production-operations-backoffice/src/main/resources/META-INF/persistence.xml).

Standaard gebruikt het project:

- database: `koga`
- host: `localhost:3306`
- gebruiker: `root`
- wachtwoord: `my-secret-pw`

Belangrijk: de property `jakarta.persistence.schema-generation.database.action` staat momenteel op `drop-and-create`. Bij het opstarten wordt het schema dus opnieuw aangemaakt.

Maak eerst lokaal de database aan:

```sql
CREATE DATABASE koga;
```

Pas daarna eventueel de credentials in `persistence.xml` aan naar jouw lokale MySQL-config.

## Applicatie starten

Start de applicatie met Maven:

```bash
mvn clean javafx:run
```

De applicatie seedt bij het opstarten automatisch voorbeelddata.

## Testen

Voer de tests uit met:

```bash
mvn test
```

## Demo logins

De loginview bevat quick-login knoppen voor deze seeded accounts:

- admin: `admin@test.com` / `admin`
- manager: `manager@test.com` / `manager`
- verantwoordelijke: `verantwoordelijke@test.com` / `verantwoordelijke`

Daarnaast worden er ook extra werknemers en verantwoordelijken aangemaakt als mockdata.

## Opmerkingen

- Build-output in `target/` en IntelliJ-bestanden in `.idea/` worden genegeerd via `.gitignore`.
- De applicatie is bedoeld voor lokaal development en demo/testing.

## Auteur

Persoonlijk GitHub-profiel: [nicolasneirynck](https://github.com/nicolasneirynck)
