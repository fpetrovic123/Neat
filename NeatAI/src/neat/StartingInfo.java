package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class StartingInfo {
    
    
   
    
    public static final int POPULATION    = 50;
    public static final double DELTA_DISJOINT  = 2.0;
    public static final int STALE_GROUPS = 15;
    public static final double DELTA_WEIGHTS   = 0.4;
    public static final int NUMBER_OF_IMPUTS        = 4;
    public static final double DELTA_THRESHOLD = 1.0;
    public static final int NUMBER_OF_OUTPUTS       = 1;
    public static final int TIMEOUT       = 20;

    // VREDNOSTI POKUPLJENJE IZ XOR NEAT PAPIRA 
    public static final double CONN_MUTATION    = 0.25;
    public static final double LINK_MUTATION    = 2.0;
    public static final double BIAS_MUTATION    = 0.4;
    public static final double DISABLE_MUTATION = 0.4;
    public static final double STEP_SIZE        = 0.1;
    public static final double PERTURBATION     = 0.9;
    public static final double CROSSOVER        = 0.75;
    public static final double NODE_MUTATION    = 0.5;
    public static final double ENABLE_MUTATION  = 0.2;


    
    
    public static final Random random = new Random();

    public static final List<Group> groups    = new ArrayList<>();
    public static int                 generation = 0;
    public static int                 inovation = NUMBER_OF_OUTPUTS;
    public static double              maxFitness = 0.0;

    public static void addGenomToGroup(final Genom child) {
        for (final Group vrsta : StartingInfo.groups)
            if (child.sameGroup(vrsta.genoms.get(0))) {
                vrsta.genoms.add(child);
                return;
            }

        final Group childGroup = new Group();
        childGroup.genoms.add(child);
        groups.add(childGroup);
    }

    public static void cullGroups(final boolean cutToOne) {
        for (final Group group : StartingInfo.groups) {
            Collections.sort(group.genoms, new Comparator<Genom>() {

                @Override
                public int compare(final Genom o1, final Genom o2) {
                    final double cmp = o2.getFitness() - o1.getFitness();
                    return cmp == 0.0 ? 0 : cmp > 0.0 ? 1 : -1;
                }
            });

            double remaining = Math.ceil(group.genoms.size() / 2.0);
            if (cutToOne)
                remaining = 1.0;

            while (group.genoms.size() > remaining)
                group.genoms.remove(group.genoms.size() - 1);
        }
    }

    public static void initializePool() {
        for (int i = 0; i < POPULATION; ++i) {
            final Genom basic = new Genom();
            basic.setMaxN(NUMBER_OF_IMPUTS);
            basic.mutationOfGene();
            addGenomToGroup(basic);
        }
    }

    public static void newGeneration() {
        cullGroups(false);
        rankGlobally();
        deleteBadGroups();
        rankGlobally();
        for (final Group group : StartingInfo.groups)
            group.calculateAverageFitness();
        deleteWeak();
        final double sum = sumAverageFitness();
        final List<Genom> children = new ArrayList<Genom>();
        for (final Group group:  StartingInfo.groups) {
            final double breed = Math
                    .floor(group.getAverageFitnes() / sum * POPULATION) - 1.0;
            for (int i = 0; i < breed; ++i)
                children.add(group.Chiled());
        }
        cullGroups(true);
        while (children.size() + groups.size() < POPULATION) {
            final Group group = StartingInfo.groups
                    .get(random.nextInt(StartingInfo.groups.size()));
            children.add(group.Chiled());
        }
        for (final Genom child : children)
            addGenomToGroup(child);
        ++generation;
    }

    public static void rankGlobally() {
        final List<Genom> global = new ArrayList<Genom>();
        for (final Group group : StartingInfo.groups)
            for (final Genom genome : group.genoms)
                global.add(genome);

        Collections.sort(global, new Comparator<Genom>() {

            @Override
            public int compare(final Genom o1, final Genom o2) {
                final double cmp = o1.getFitness() - o2.getFitness();
                return cmp == 0 ? 0 : cmp > 0 ? 1 : -1;
            }
        });

        for (int i = 0; i < global.size(); ++i)
            global.get(i).setgRank(i);
    }

    public static void deleteBadGroups() {
        final List<Group> alive = new ArrayList<Group>();
        for (final Group group : StartingInfo.groups) {
            Collections.sort(group.genoms, new Comparator<Genom>() {

                @Override
                public int compare(final Genom o1, final Genom o2) {
                    final double comaration = o2.getFitness() - o1.getFitness();
                    return comaration == 0 ? 0 : comaration > 0 ? 1 : -1;
                }
            });

            if (group.genoms.get(0).getFitness() > group.getTopFitnests()) {
                group.setTopFitnests(group.genoms.get(0).getFitness());
                group.setStaleness(0);
            } else
                group.setStaleness(group.getStaleness()+1);

            if (group.getStaleness() < STALE_GROUPS
                    || group.getTopFitnests() >= maxFitness)
                alive.add(group);
        }

        groups.clear();
        groups.addAll(alive);
    }

    public static void deleteWeak() {
        final List<Group> alive = new ArrayList<Group>();

        final double sum = sumAverageFitness();
        for (final Group group : StartingInfo.groups) {
            final double linkings = Math
                    .floor(group.getAverageFitnes() / sum * POPULATION);
            if (linkings >= 1.0)
                alive.add(group);
        }

        groups.clear();
        groups.addAll(alive);
    }

    public static double sumAverageFitness() {
        double sum = 0;
        for (final Group group : StartingInfo.groups)
            sum += group.getAverageFitnes();
        return sum;
    }
}
