package neat;

import static neat.StartingInfo.CROSSOVER;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static neat.StartingInfo.random;

public class Group {
    
    private double topFitnestss;
    private double averageFitness;
    private int staleness;

    public Group() {
    this.topFitnestss=0.0;
    this.averageFitness=0.0;
    this.staleness=0;
    }
    
    public double getTopFitnests() {
        return topFitnestss;
    }

    public void setTopFitnests(double topFitnests) {
        this.topFitnestss = topFitnests;
    }

    public double getAverageFitnes() {
        return averageFitness;
    }

    public void setAverageFitnes(double averageFitnes) {
        this.averageFitness = averageFitnes;
    }

    public int getStaleness() {
        return staleness;
    }

    public void setStaleness(int staleness) {
        this.staleness = staleness;
    }
    public final List<Genom> genoms= new LinkedList<>();
    
    public Genom Chiled() {
        final Genom child;
        if (random.nextDouble() < CROSSOVER) {
            final Genom g1 = genoms.get(random.nextInt(genoms.size()));
            final Genom g2 = genoms.get(random.nextInt(genoms.size()));
            child = Crossover(g1, g2);
        } else
            child = genoms.get(random.nextInt(genoms.size())).clone();
        child.mutationOfGene();
        return child;
    }

    public void calculateAverageFitness() {
        double total = 0.0;
        for (final Genom genome : genoms)
            total += genome.getgRank();
        averageFitness = total / genoms.size();
    }

    public Genom Crossover(Genom g1, Genom g2) {
        if (g2.getFitness() > g1.getFitness()) {
            final Genom tmp = g1;
            g1 = g2;
            g2 = tmp;
        }
        


        final Genom chiled = new Genom();
        outerloop: for (final Synapse genom1 : g1.genomLink) {
            for (final Synapse genom2 : g2.genomLink)
                if (genom1.inovation == genom2.inovation)
                    if (random.nextBoolean() && genom2.isEnabled) {
                        chiled.genomLink.add(genom2.clone());
                        continue outerloop;
                    } else
                        break;
            chiled.genomLink.add(genom1.clone());
        }

        chiled.setMaxN(Math.max(g1.getMaxN(), g2.getMaxN()));

        for (int i = 0; i < 7; ++i)
            chiled.arrayMutation[i] = g1.arrayMutation[i];
        return chiled;
    }
    
    
    
    
    
  
}
