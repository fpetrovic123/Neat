package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class OsnovneInfomacije {
    
    
    //OSNOVNE INFORMACIJE
    
    public static final int POPULACIJA    = 50;
    public static final int STALE_SPECIES = 15;
    public static final int INPUTI        = 4;
    public static final int OUTPUT       = 1;
    public static final int TIMEOUT       = 20;

    //VREDNOSTI POKUPLJENJE IZ XOR   NEAT PAPIRA
    
    public static final double DELTA_DISJOINT  = 2.0;
    public static final double DELTA_WEIGHTS   = 0.4;
    public static final double DELTA_THRESHOLD = 1.0;

    
    // VREDNOSTI POKUPLJENJE IZ XOR NEAT PAPIRA 
    
    public static final double CONN_MUTATION    = 0.25;
    public static final double LINK_MUTATION    = 2.0;
    public static final double BIAS_MUTATION    = 0.4;
    public static final double NODE_MUTATION    = 0.5;
    public static final double ENABLE_MUTATION  = 0.2;
    public static final double DISABLE_MUTATION = 0.4;
    public static final double STEP_SIZE        = 0.1;
    public static final double PERTURBATION     = 0.9;
    public static final double CROSSOVER        = 0.75;

    
    
    public static final Random rnd = new Random();

    public static final List<Vrsta> vrste    = new ArrayList<>();
    public static int                 generacija = 0;
    public static int                 inovacija = OUTPUT;
    public static double              maksimalniFitnest = 0.0;

    public static void dodajGenVrsti(final Genom dete) {
        for (final Vrsta vrsta : OsnovneInfomacije.vrste)
            if (dete.istaVrsta(vrsta.genomi.get(0))) {
                vrsta.genomi.add(dete);
                return;
            }

        final Vrsta deteVrsta = new Vrsta();
        deteVrsta.genomi.add(dete);
        vrste.add(deteVrsta);
    }

    public static void cullSpecies(final boolean cutToOne) {
        for (final Vrsta species : OsnovneInfomacije.vrste) {
            Collections.sort(species.genomi, new Comparator<Genom>() {

                @Override
                public int compare(final Genom o1, final Genom o2) {
                    final double cmp = o2.fitnest - o1.fitnest;
                    return cmp == 0.0 ? 0 : cmp > 0.0 ? 1 : -1;
                }
            });

            double remaining = Math.ceil(species.genomi.size() / 2.0);
            if (cutToOne)
                remaining = 1.0;

            while (species.genomi.size() > remaining)
                species.genomi.remove(species.genomi.size() - 1);
        }
    }

    public static void initializePool() {
        for (int i = 0; i < POPULACIJA; ++i) {
            final Genom bazicni = new Genom();
            bazicni.maxNeurona = INPUTI;
            bazicni.mutacijaGena();
            dodajGenVrsti(bazicni);
        }
    }

    public static void newGeneration() {
        cullSpecies(false);
        rankGlobally();
        izbrisiVrsteKojeNevaljaju();
        rankGlobally();
        for (final Vrsta species : OsnovneInfomacije.vrste)
            species.calculateAverageFitness();
        izbrisiSlabeVrste();
        final double sum = ukupniProsecanFitnest();
        final List<Genom> children = new ArrayList<Genom>();
        for (final Vrsta species : OsnovneInfomacije.vrste) {
            final double breed = Math
                    .floor(species.prosecanFitnest / sum * POPULACIJA) - 1.0;
            for (int i = 0; i < breed; ++i)
                children.add(species.nastaloDete());
        }
        cullSpecies(true);
        while (children.size() + vrste.size() < POPULACIJA) {
            final Vrsta species = OsnovneInfomacije.vrste
                    .get(rnd.nextInt(OsnovneInfomacije.vrste.size()));
            children.add(species.nastaloDete());
        }
        for (final Genom child : children)
            dodajGenVrsti(child);
        ++generacija;
    }

    public static void rankGlobally() {
        final List<Genom> global = new ArrayList<Genom>();
        for (final Vrsta species : OsnovneInfomacije.vrste)
            for (final Genom genome : species.genomi)
                global.add(genome);

        Collections.sort(global, new Comparator<Genom>() {

            @Override
            public int compare(final Genom o1, final Genom o2) {
                final double cmp = o1.fitnest - o2.fitnest;
                return cmp == 0 ? 0 : cmp > 0 ? 1 : -1;
            }
        });

        for (int i = 0; i < global.size(); ++i)
            global.get(i).globalnaPozicija = i;
    }

    public static void izbrisiVrsteKojeNevaljaju() {
        final List<Vrsta> prezieli = new ArrayList<Vrsta>();
        for (final Vrsta species : OsnovneInfomacije.vrste) {
            Collections.sort(species.genomi, new Comparator<Genom>() {

                @Override
                public int compare(final Genom o1, final Genom o2) {
                    final double komparacija = o2.fitnest - o1.fitnest;
                    return komparacija == 0 ? 0 : komparacija > 0 ? 1 : -1;
                }
            });

            if (species.genomi.get(0).fitnest > species.topFitnest) {
                species.topFitnest = species.genomi.get(0).fitnest;
                species.ustajalePtice = 0;
            } else
                ++species.ustajalePtice;

            if (species.ustajalePtice < STALE_SPECIES
                    || species.topFitnest >= maksimalniFitnest)
                prezieli.add(species);
        }

        vrste.clear();
        vrste.addAll(prezieli);
    }

    public static void izbrisiSlabeVrste() {
        final List<Vrsta> preziveli = new ArrayList<Vrsta>();

        final double sum = ukupniProsecanFitnest();
        for (final Vrsta vrsta : OsnovneInfomacije.vrste) {
            final double umnozavanje = Math
                    .floor(vrsta.prosecanFitnest / sum * POPULACIJA);
            if (umnozavanje >= 1.0)
                preziveli.add(vrsta);
        }

        vrste.clear();
        vrste.addAll(preziveli);
    }

    public static double ukupniProsecanFitnest() {
        double ukupno = 0;
        for (final Vrsta vrsta : OsnovneInfomacije.vrste)
            ukupno += vrsta.prosecanFitnest;
        return ukupno;
    }
}
