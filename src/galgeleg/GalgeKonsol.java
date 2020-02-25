package galgeleg;

import java.rmi.Naming;
import java.util.Scanner;

public class GalgeKonsol {

    boolean erSpilVundet = false;


    public void spil() throws Exception{

        BrugerautorisationI brugeraut = (BrugerautorisationI) Naming.lookup("rmi://localhost:1099/brugeraut");
        brugeraut.autoriserBruger("Jeppe", "Mads");

        GalgeI spil =(GalgeI) Naming.lookup("rmi://localhost:1099/galgeleg");

        // setup
        spil.nulstil();

//        try {
//            spil.hentOrdFraDr();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println("Hej. Galgeleg er sjovt. Indtast brugernavn efterfulgt af kodeord.");

        // brugerautorisation
        // TODO: brugerautorisation

        // kør spil
        Scanner input = new Scanner(System.in);
        String gæt;

        while (!erSpilVundet) {
            System.out.println("Ok, PLACEHOLDER. Gæt ordet eller dø:");
            System.out.println(spil.getSynligtOrd());
            System.out.println("CHEATZ: " + spil.getOrdet());
            System.out.println("Gæt: ");
            System.out.println();

            gæt = input.next();

            while (!gæt.matches("[A-Åa-å]{1}")) {
                System.out.println("Ét bogstav...");
                gæt = input.next();
            }

            spil.gætBogstav(gæt);

            if (spil.erSpilletSlut() == true) {
                erSpilVundet = true;

                System.out.println("Spillet er slut.");

                input.close();
            }
        }

    }

}