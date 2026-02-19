package main;

import domein.Gebruiker;
import jakarta.persistence.EntityManager;
import util.GebruikerStatus;
import util.JPAUtil;
import util.Rollen;

public class DBTest {
    public static void main(String args[]) {
        Gebruiker test = Gebruiker.builder().email("test").gebruikersnaam("test").wachtwoord("test").status(GebruikerStatus.ACTIEF).rol(Rollen.WERKNEMER).build();

        try( EntityManager em
                     = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager())
        {
            em.getTransaction().begin();

            em.persist(test);

            em.getTransaction().commit();
        }

        JPAUtil.getENTITY_MANAGER_FACTORY().close();
    }
}
