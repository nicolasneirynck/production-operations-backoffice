package main;

import domein.Gebruiker;
import jakarta.persistence.EntityManager;
import util.JPAUtil;

public class DBTest {
    public static void main(String args[]) {
        Gebruiker test = new Gebruiker("test","test");

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
