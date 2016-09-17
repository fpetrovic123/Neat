package neat;

import static neat.StartingInfo.BIAS_MUTATION;
import static neat.StartingInfo.CONN_MUTATION;
import static neat.StartingInfo.DELTA_DISJOINT;
import static neat.StartingInfo.DELTA_THRESHOLD;
import static neat.StartingInfo.DELTA_WEIGHTS;
import static neat.StartingInfo.DISABLE_MUTATION;
import static neat.StartingInfo.ENABLE_MUTATION;
import static neat.StartingInfo.LINK_MUTATION;
import static neat.StartingInfo.NODE_MUTATION;
import static neat.StartingInfo.PERTURBATION;
import static neat.StartingInfo.STEP_SIZE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static neat.StartingInfo.NUMBER_OF_IMPUTS;
import static neat.StartingInfo.NUMBER_OF_OUTPUTS;
import static neat.StartingInfo.random;

public class Genom {
   

    private double fitness=0.0;
    private int maxN=0;
    private int gRank=0;

    public Genom() {
            fitness = 0.0;
            maxN=0;
            gRank=0;
    }

    public double getFitness() {
        return fitness;
    }
    
    public final List<Synapse>  genomLink         = new ArrayList<>();

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getMaxN() {
        return maxN;
    }

    public void setMaxN(int maxN) {
        this.maxN = maxN;
    }

    public int getgRank() {
        return gRank;
    }

    public void setgRank(int gRank) {
        this.gRank = gRank;
    }
    
    
    @Override
    public Genom clone() {
        final Genom genom = new Genom();
        for (final Synapse gene : genomLink)
            genom.genomLink.add(gene.clone());
        genom.maxN = maxN;
        for (int i = 0; i < 7; ++i)
            genom.arrayMutation[i] = arrayMutation[i];
        return genom;
    }
    
    

    public boolean hasLink(final Synapse link) {
        for (final Synapse genom : this.genomLink)
            if (genom.input == link.input && genom.output == link.output)
                return true;
        return false;
    }

    public double disjoint(final Genom genom) {
        double disjoinGen = 0.0;
        search: for (final Synapse gene : genomLink) {
            for (final Synapse drugiGen : genom.genomLink)
                if (gene.inovation == drugiGen.inovation)
                    continue search;
            ++disjoinGen;
        }
        return disjoinGen / Math.max(genomLink.size(), genom.genomLink.size());
    }
    
    
    public final double[]       arrayMutation = new double[] { CONN_MUTATION,
            LINK_MUTATION, BIAS_MUTATION, NODE_MUTATION, ENABLE_MUTATION,
            DISABLE_MUTATION, STEP_SIZE };
    
    public Map<Integer, Neuron> network       = null;

    public double[] evolutionOfNetwork(final double[] input) {
        for (int i = 0; i < NUMBER_OF_IMPUTS; ++i)
            network.get(i).Value = input[i];
        

        for (final Entry<Integer, Neuron> entry : network.entrySet()) {
            if (entry.getKey() < NUMBER_OF_IMPUTS + NUMBER_OF_OUTPUTS)
                continue;
            final Neuron neuron = entry.getValue();
            double sum = 0.0;
            for (final Synapse inc : neuron.Inputs) {
                final Neuron anotherNeuron = network.get(inc.input);
                sum += inc.weight * anotherNeuron.Value;
            }
//Jer ako nema veze predstavlja output

            if (!neuron.Inputs.isEmpty())
                neuron.Value = Neuron.sigmoidFunction(sum);
        }
//OUTP funkcije 
        for (final Entry<Integer, Neuron> entry : network.entrySet()) {
            if (entry.getKey() < NUMBER_OF_IMPUTS || entry.getKey() >= NUMBER_OF_IMPUTS + NUMBER_OF_OUTPUTS)
                continue;
            final Neuron neuron = entry.getValue();
            double sum = 0.0;
            for (final Synapse incS:  neuron.Inputs) {
                final Neuron drugiNeuron = network.get(incS.input);
                sum += incS.weight * drugiNeuron.Value;
            }

            if (!neuron.Inputs.isEmpty())
                neuron.Value = Neuron.sigmoidFunction(sum);
        }

        final double[] output = new double[NUMBER_OF_OUTPUTS];
        for (int i = 0; i < NUMBER_OF_OUTPUTS; ++i)
            output[i] = network.get(NUMBER_OF_IMPUTS + i).Value;
        return output;
    }

    public void generateNetwork() {
        network = new HashMap<Integer, Neuron>();
        for (int i = 0; i < NUMBER_OF_IMPUTS; ++i)
            network.put(i, new Neuron());
        for (int i = 0; i < NUMBER_OF_OUTPUTS; ++i)
            network.put(NUMBER_OF_IMPUTS + i, new Neuron());

        
        
        Collections.sort(genomLink, new Comparator<Synapse>() {

            @Override
            public int compare(final Synapse o1, final Synapse o2) {
                return o1.output - o2.output;
            }
        });
        for (final Synapse gen : genomLink)
            if (gen.isEnabled) {
                if (!network.containsKey(gen.output))
                    network.put(gen.output, new Neuron());
                final Neuron neuron = network.get(gen.output);
                neuron.Inputs.add(gen);
                if (!network.containsKey(gen.input))
                    network.put(gen.input, new Neuron());
            }
    }

    public void mutationOfGene() {
        
       
        for (int i = 0; i < 7; ++i)
            arrayMutation[i] *= random.nextBoolean() ? 0.95 : 1.05263;

        if (random.nextDouble() < arrayMutation[0]) 
            mutationPoint();

        double prob = arrayMutation[1];
        while (prob > 0) {
            if (random.nextDouble() < prob)
                mutateLink(false);
            --prob;
        }

        prob = arrayMutation[2];
        while (prob > 0) {
            if (random.nextDouble() < prob)
                mutateLink(true);
            --prob;
        }

        prob = arrayMutation[3];
        while (prob > 0) {
            if (random.nextDouble() < prob)
                mutateNode();
            --prob;
        }

        prob = arrayMutation[4];
        while (prob > 0) {
            if (random.nextDouble() < prob)
                mutateEnableDisable(true);
            --prob;
        }

        prob = arrayMutation[5];
        while (prob > 0) {
            if (random.nextDouble() < prob)
                mutateEnableDisable(false);
            --prob;
        }
    }

    public void mutateEnableDisable(final boolean enable) {
        final List<Synapse> candidates = new ArrayList<Synapse>();
        for (final Synapse gene : genomLink)
            if (gene.isEnabled != enable)
                candidates.add(gene);

        if (candidates.isEmpty())
            return;

        final Synapse gene = candidates.get(random.nextInt(candidates.size()));
        gene.isEnabled = !gene.isEnabled;
    }

    public void mutateLink(final boolean forceBias) {
        final int neuron1 = randomNeuron(false, true);
        final int neuron2 = randomNeuron(true, false);

        final Synapse newLink = new Synapse();
        newLink.input = neuron1;
        newLink.output = neuron2;

        if (forceBias)
            newLink.input = NUMBER_OF_IMPUTS - 1;

        if (hasLink(newLink))
            return;

        newLink.inovation = ++StartingInfo.inovation;
        newLink.weight = random.nextDouble() * 4.0 - 2.0;

        genomLink.add(newLink);
    }

    public void mutateNode() {
        if (genomLink.isEmpty())
            return;

        final Synapse gene = genomLink.get(random.nextInt(genomLink.size()));
        if (!gene.isEnabled)
            return;
        gene.isEnabled = false;

        ++maxN;

        final Synapse genom1 = gene.clone();
        genom1.output = maxN;
        genom1.weight = 1.0;
        genom1.inovation = ++StartingInfo.inovation;
        genom1.isEnabled = true;
        genomLink.add(genom1);

        final Synapse genom2 = gene.clone();
        genom2.input = maxN;
        genom2.inovation = ++StartingInfo.inovation;
        genom2.isEnabled = true;
        genomLink.add(genom2);
    }

    public void mutationPoint() {
        for (final Synapse gene : genomLink)
            if (random.nextDouble() < PERTURBATION)
                gene.weight += random.nextDouble() * arrayMutation[6] * 2.0
                        - arrayMutation[6];
            else
                gene.weight = random.nextDouble() * 4.0 - 2.0;
    }

    public int randomNeuron(final boolean nonInput, final boolean nonOutput) {
        final List<Integer> neurons = new ArrayList<Integer>();

        if (!nonInput)
            for (int i = 0; i < NUMBER_OF_IMPUTS; ++i)
                neurons.add(i);

        if (!nonOutput)
            for (int i = 0; i < NUMBER_OF_OUTPUTS; ++i)
                neurons.add(NUMBER_OF_IMPUTS + i);

        for (final Synapse gene : genomLink) {
            if ((!nonInput || gene.input >= NUMBER_OF_IMPUTS)
                    && (!nonOutput || gene.input >= NUMBER_OF_IMPUTS + NUMBER_OF_OUTPUTS))
                neurons.add(gene.input);
            if ((!nonInput || gene.output >= NUMBER_OF_IMPUTS)
                    && (!nonOutput || gene.output >= NUMBER_OF_IMPUTS + NUMBER_OF_OUTPUTS))
                neurons.add(gene.output);
        }

        return neurons.get(random.nextInt(neurons.size()));
    }

    public boolean sameGroup(final Genom genom) {
        final double dd = DELTA_DISJOINT * disjoint(genom);
        final double dw = DELTA_WEIGHTS * weights(genom);
        return dd + dw < DELTA_THRESHOLD;
    }

    public double weights(final Genom genom) {
        double sum = 0.0;
        double coincidence = 0.0;
        search: for (final Synapse genom1 : genomLink)
            for (final Synapse antoherGenom : genom.genomLink)
                if (genom1.inovation == antoherGenom.inovation) {
                    sum += Math.abs(genom1.weight - antoherGenom.weight);
                    ++coincidence;
                    continue search;
                }
        return sum / coincidence;
    }
}
