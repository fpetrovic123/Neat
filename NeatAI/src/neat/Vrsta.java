package neat;

import static neat.OsnovneInfomacije.CROSSOVER;
import static neat.OsnovneInfomacije.rnd;

import java.util.ArrayList;
import java.util.List;

public class Vrsta {
    public final List<Genom> genomi        = new ArrayList<Genom>();
    public double             topFitnest     = 0.0;
    public double             prosecanFitnest = 0.0;
    public int                ustajalePtice      = 0;

    public Genom nastaloDete() {
        final Genom dete;
        if (rnd.nextDouble() < CROSSOVER) {
            final Genom g1 = genomi.get(rnd.nextInt(genomi.size()));
            final Genom g2 = genomi.get(rnd.nextInt(genomi.size()));
            dete = poredjenjeCrossovera(g1, g2);
        } else
            dete = genomi.get(rnd.nextInt(genomi.size())).clone();
        dete.mutacijaGena();
        return dete;
    }

    public void calculateAverageFitness() {
        double total = 0.0;
        for (final Genom genome : genomi)
            total += genome.globalnaPozicija;
        prosecanFitnest = total / genomi.size();
    }

    public Genom poredjenjeCrossovera(Genom g1, Genom g2) {
        if (g2.fitnest > g1.fitnest) {
            final Genom tmp = g1;
            g1 = g2;
            g2 = tmp;
        }

        final Genom dete = new Genom();
        outerloop: for (final Sinapsa gene1 : g1.genLinkVeza) {
            for (final Sinapsa gene2 : g2.genLinkVeza)
                if (gene1.inovacija == gene2.inovacija)
                    if (rnd.nextBoolean() && gene2.omogucen) {
                        dete.genLinkVeza.add(gene2.clone());
                        continue outerloop;
                    } else
                        break;
            dete.genLinkVeza.add(gene1.clone());
        }

        dete.maxNeurona = Math.max(g1.maxNeurona, g2.maxNeurona);

        for (int i = 0; i < 7; ++i)
            dete.mutacija[i] = g1.mutacija[i];
        return dete;
    }
}
