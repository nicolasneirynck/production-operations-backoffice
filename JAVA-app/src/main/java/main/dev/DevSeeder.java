package main.dev;

import domein.Gebruiker;
import repository.GebruikerDao;
import util.GebruikerStatus;
import util.Rollen;

public final class DevSeeder {
    private final GebruikerDao gebruikerRepo;

    public DevSeeder(GebruikerDao gebruikerRepo) {
        this.gebruikerRepo = gebruikerRepo;
    }

    public void seed() {
        // To prevent seeding inside of production
        if (!Boolean.getBoolean("seed.devUser")) return;

        try {
            gebruikerRepo.startTransaction();

            String email = "test@test.com";

            // If we ever turn off drop-and-create we would need this:
            boolean isPresent = gebruikerRepo.findByEmail(email).isPresent();

            if (!isPresent) {
                Gebruiker gebruiker = Gebruiker.builder().email(email).gebruikersnaam("TestUser").wachtwoord("pass123").status(GebruikerStatus.ACTIEF).rol(Rollen.WERKNEMER).build();
                gebruikerRepo.insert(gebruiker);
            }

            gebruikerRepo.commitTransaction();
        } catch (RuntimeException ex) {
            gebruikerRepo.rollbackTransaction();
            throw ex;
        }
    }
}