package galgeleg.logik;

import galgeleg.server.GalgeI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Galgelogik extends UnicastRemoteObject implements GalgeI {
    /**
     * AHT afprøvning er muligeOrd synlig på pakkeniveau
     */
    ArrayList<String> muligeOrd = new ArrayList<String>();
    private String ordet;
    private ArrayList<String> brugteBogstaver = new ArrayList<String>();
    private String synligtOrd;
    private int antalForkerteBogstaver;
    private boolean sidsteBogstavVarKorrekt;
    private boolean spilletErVundet;
    private boolean spilletErTabt;

    public Galgelogik() throws java.rmi.RemoteException {
        muligeOrd.add("bil");
        muligeOrd.add("computer");
        muligeOrd.add("programmering");
        muligeOrd.add("motorvej");
        muligeOrd.add("busrute");
        muligeOrd.add("gangsti");
        muligeOrd.add("skovsnegl");
        muligeOrd.add("solsort");
        muligeOrd.add("nitten");
        nulstil();
    }


    @Override
    public ArrayList<String> getBrugteBogstaver() {
        return brugteBogstaver;
    }

    @Override
    public String getSynligtOrd() {
        return synligtOrd;
    }

    @Override
    public String getOrdet() {
        return ordet;
    }

    @Override
    public int getAntalForkerteBogstaver() {
        return antalForkerteBogstaver;
    }

    @Override
    public boolean erSidsteBogstavKorrekt() {
        return sidsteBogstavVarKorrekt;
    }

    @Override
    public boolean erSpilletVundet() {
        return spilletErVundet;
    }

    @Override
    public boolean erSpilletTabt() {
        return spilletErTabt;
    }

    @Override
    public boolean erSpilletSlut() {
        return spilletErTabt || spilletErVundet;
    }


    @Override
    public void nulstil() throws java.rmi.RemoteException {
        brugteBogstaver.clear();
        antalForkerteBogstaver = 0;
        spilletErVundet = false;
        spilletErTabt = false;
        if (muligeOrd.isEmpty()) throw new IllegalStateException("Listen over ord er tom!");
        ordet = muligeOrd.get(new Random().nextInt(muligeOrd.size()));
        opdaterSynligtOrd();
    }
    private void opdaterSynligtOrd() {
        synligtOrd = "";
        spilletErVundet = true;
        for (int n = 0; n < ordet.length(); n++) {
            String bogstav = ordet.substring(n, n + 1);
            if (brugteBogstaver.contains(bogstav)) {
                synligtOrd = synligtOrd + bogstav;
            } else {
                synligtOrd = synligtOrd + "*";
                spilletErVundet = false;
            }
        }
    }

    @Override
    public void gætBogstav(String bogstav) throws java.rmi.RemoteException {
        if (bogstav.length() != 1) return;
        System.out.println("Der gættes på bogstavet: " + bogstav);
        if (brugteBogstaver.contains(bogstav)) return;
        if (spilletErVundet || spilletErTabt) return;

        brugteBogstaver.add(bogstav);

        if (ordet.contains(bogstav)) {
            sidsteBogstavVarKorrekt = true;
            System.out.println("Bogstavet var korrekt: " + bogstav);
        } else {
            // Vi gættede på et bogstav der ikke var i ordet.
            sidsteBogstavVarKorrekt = false;
            System.out.println("Bogstavet var IKKE korrekt: " + bogstav);
            antalForkerteBogstaver = antalForkerteBogstaver + 1;
            if (antalForkerteBogstaver > 6) {
                spilletErTabt = true;
            }
        }
        opdaterSynligtOrd();
    }

    @Override
    public void logStatus() {
        System.out.println("---------- ");
        System.out.println("- ordet (skult) = " + ordet);
        System.out.println("- synligtOrd = " + synligtOrd);
        System.out.println("- forkerteBogstaver = " + antalForkerteBogstaver);
        System.out.println("- brugeBogstaver = " + brugteBogstaver);
        if (spilletErTabt) System.out.println("- SPILLET ER TABT");
        if (spilletErVundet) System.out.println("- SPILLET ER VUNDET");
        System.out.println("---------- ");
    }
  public static String hentUrl(String url) throws IOException {
    System.out.println("Henter data fra " + url);
    BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
    StringBuilder sb = new StringBuilder();
    String linje = br.readLine();
    while (linje != null) {
      sb.append(linje + "\n");
      linje = br.readLine();
    }
    return sb.toString();
  }


    /**
     * Hent ord fra DRs forside (https://dr.dk)
     */
    @Override
    public void hentOrdFraDr() throws java.rmi.RemoteException {

      String data = null;
      try {
        data = hentUrl("https://dr.dk");
      } catch (IOException e) {
        e.printStackTrace();
      }
      //System.out.println("data = " + data);

        data = data.substring(data.indexOf("<body")). // fjern headere
                replaceAll("<.+?>", " ").toLowerCase(). // fjern tags
                replaceAll("&#198;", "æ"). // erstat HTML-tegn
                replaceAll("&#230;", "æ"). // erstat HTML-tegn
                replaceAll("&#216;", "ø"). // erstat HTML-tegn
                replaceAll("&#248;", "ø"). // erstat HTML-tegn
                replaceAll("&oslash;", "ø"). // erstat HTML-tegn
                replaceAll("&#229;", "å"). // erstat HTML-tegn
                replaceAll("[^a-zæøå]", " "). // fjern tegn der ikke er bogstaver
                replaceAll(" [a-zæøå] ", " "). // fjern 1-bogstavsord
                replaceAll(" [a-zæøå][a-zæøå] ", " "); // fjern 2-bogstavsord

        System.out.println("data = " + data);
        System.out.println("data = " + Arrays.asList(data.split("\\s+")));
        muligeOrd.clear();
        muligeOrd.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

        System.out.println("muligeOrd = " + muligeOrd);
        nulstil();
    }


    /**
     * Hent ord og sværhedsgrad fra et online regneark. Du kan redigere i regnearket, på adressen
     * https://docs.google.com/spreadsheets/d/1RnwU9KATJB94Rhr7nurvjxfg09wAHMZPYB3uySBPO6M/edit?usp=sharing
     *
     * @param sværhedsgrader en streng med de tilladte sværhedsgrader - f.eks "3" for at medtage kun svære ord, eller "12" for alle nemme og halvsvære ord
     * @throws Exception
     */

    @Override
    public void hentOrdFraRegneark(String sværhedsgrader) throws java.rmi.RemoteException {
        String id = "1RnwU9KATJB94Rhr7nurvjxfg09wAHMZPYB3uySBPO6M";

        System.out.println("Henter data som kommasepareret CSV fra regnearket https://docs.google.com/spreadsheets/d/" + id + "/edit?usp=sharing");

      String data = null;
      try {
        data = hentUrl("https://docs.google.com/spreadsheets/d/" + id + "/export?format=csv&id=" + id);
      } catch (IOException e) {
        e.printStackTrace();
      }
      int linjeNr = 0;

        muligeOrd.clear();
        for (String linje : data.split("\n")) {
            if (linjeNr < 20) System.out.println("Læst linje = " + linje); // udskriv de første 20 linjer
            if (linjeNr++ < 1) continue; // Spring første linje med kolonnenavnene over
            String[] felter = linje.split(",", -1);// -1 er for at beholde tomme indgange, f.eks. bliver ",,," splittet i et array med 4 tomme strenge
            String sværhedsgrad = felter[0].trim();
            String ordet = felter[1].trim().toLowerCase();
            if (sværhedsgrad.isEmpty() || ordet.isEmpty()) continue; // spring over linjer med tomme ord
            if (!sværhedsgrader.contains(sværhedsgrad)) continue; // filtrér på sværhedsgrader
            System.out.println("Tilføjer " + ordet + ", der har sværhedsgrad " + sværhedsgrad);
            muligeOrd.add(ordet);
        }

        System.out.println("muligeOrd = " + muligeOrd);
        nulstil();
    }
}
