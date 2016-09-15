package neat;

import static neat.OsnovneInfomacije.BIAS_MUTATION;
import static neat.OsnovneInfomacije.CONN_MUTATION;
import static neat.OsnovneInfomacije.DELTA_DISJOINT;
import static neat.OsnovneInfomacije.DELTA_THRESHOLD;
import static neat.OsnovneInfomacije.DELTA_WEIGHTS;
import static neat.OsnovneInfomacije.DISABLE_MUTATION;
import static neat.OsnovneInfomacije.ENABLE_MUTATION;
import static neat.OsnovneInfomacije.LINK_MUTATION;
import static neat.OsnovneInfomacije.NODE_MUTATION;
import static neat.OsnovneInfomacije.PERTURBATION;
import static neat.OsnovneInfomacije.STEP_SIZE;
import static neat.OsnovneInfomacije.rnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static neat.OsnovneInfomacije.INPUTI;
import static neat.OsnovneInfomacije.OUTPUT;

public class Genom {
   

    public final List<Sinapsa>  genLinkVeza         = new ArrayList<Sinapsa>();
    public double               fitnest       = 0.0;
    public int                  maxNeurona     = 0;
    public int                  globalnaPozicija    = 0;
    
    
    
    
    @Override
    public Genom clone() {
        final Genom genom = new Genom();
        for (final Sinapsa gene : genLinkVeza)
            genom.genLinkVeza.add(gene.clone());
        genom.maxNeurona = maxNeurona;
        for (int i = 0; i < 7; ++i)
            genom.mutacija[i] = mutacija[i];
        return genom;
    }
    
    

    public boolean sadrziLink(final Sinapsa link) {
        for (final Sinapsa gene : this.genLinkVeza)
            if (gene.input == link.input && gene.output == link.output)
                return true;
        return false;
    }

    public double disjoint(final Genom genom) {
        double disjoinGen = 0.0;
        search: for (final Sinapsa gene : genLinkVeza) {
            for (final Sinapsa drugiGen : genom.genLinkVeza)
                if (gene.inovacija == drugiGen.inovacija)
                    continue search;
            ++disjoinGen;
        }
        return disjoinGen / Math.max(genLinkVeza.size(), genom.genLinkVeza.size());
    }
    
    
    public final double[]       mutacija = new double[] { CONN_MUTATION,
            LINK_MUTATION, BIAS_MUTATION, NODE_MUTATION, ENABLE_MUTATION,
            DISABLE_MUTATION, STEP_SIZE };
    
    public Map<Integer, Neuron> mreza       = null;

    public double[] evaulacijaMrezeGen(final double[] input) {
        for (int i = 0; i < INPUTI; ++i)
            mreza.get(i).Value = input[i];
        

        for (final Entry<Integer, Neuron> entry : mreza.entrySet()) {
            if (entry.getKey() < INPUTI + OUTPUT)
                continue;
            final Neuron neuron = entry.getValue();
            double sum = 0.0;
            for (final Sinapsa dolazecaSin : neuron.Inputs) {
                final Neuron drugiNeuron = mreza.get(dolazecaSin.input);
                sum += dolazecaSin.tezina * drugiNeuron.Value;
            }
//Jer ako nema veze predstavlja output

            if (!neuron.Inputs.isEmpty())
                neuron.Value = Neuron.sigmoidFunkcija(sum);
        }
//OUTP funkcije 
        for (final Entry<Integer, Neuron> entry : mreza.entrySet()) {
            if (entry.getKey() < INPUTI || entry.getKey() >= INPUTI + OUTPUT)
                continue;
            final Neuron neuron = entry.getValue();
            double sum = 0.0;
            for (final Sinapsa dolazecaSin:  neuron.Inputs) {
                final Neuron drugiNeuron = mreza.get(dolazecaSin.input);
                sum += dolazecaSin.tezina * drugiNeuron.Value;
            }

            if (!neuron.Inputs.isEmpty())
                neuron.Value = Neuron.sigmoidFunkcija(sum);
        }

        final double[] output = new double[OUTPUT];
        for (int i = 0; i < OUTPUT; ++i)
            output[i] = mreza.get(INPUTI + i).Value;
        return output;
    }

    public void generisiMrezu() {
        mreza = new HashMap<Integer, Neuron>();
        for (int i = 0; i < INPUTI; ++i)
            mreza.put(i, new Neuron());
        for (int i = 0; i < OUTPUT; ++i)
            mreza.put(INPUTI + i, new Neuron());

        
        
        Collections.sort(genLinkVeza, new Comparator<Sinapsa>() {

            @Override
            public int compare(final Sinapsa o1, final Sinapsa o2) {
                return o1.output - o2.output;
            }
        });
        for (final Sinapsa gen : genLinkVeza)
            if (gen.omogucen) {
                if (!mreza.containsKey(gen.output))
                    mreza.put(gen.output, new Neuron());
                final Neuron neuron = mreza.get(gen.output);
                neuron.Inputs.add(gen);
                if (!mreza.containsKey(gen.input))
                    mreza.put(gen.input, new Neuron());
            }
    }

    public void mutacijaGena() {
        
       
        for (int i = 0; i < 7; ++i)
            mutacija[i] *= rnd.nextBoolean() ? 0.95 : 1.05263;

        if (rnd.nextDouble() < mutacija[0]) 
            mutacijaTacke();

        double prob = mutacija[1];
        while (prob > 0) {
            if (rnd.nextDouble() < prob)
                mutateLink(false);
            --prob;
        }

        prob = mutacija[2];
        while (prob > 0) {
            if (rnd.nextDouble() < prob)
                mutateLink(true);
            --prob;
        }

        prob = mutacija[3];
        while (prob > 0) {
            if (rnd.nextDouble() < prob)
                mutateNode();
            --prob;
        }

        prob = mutacija[4];
        while (prob > 0) {
            if (rnd.nextDouble() < prob)
                mutateEnableDisable(true);
            --prob;
        }

        prob = mutacija[5];
        while (prob > 0) {
            if (rnd.nextDouble() < prob)
                mutateEnableDisable(false);
            --prob;
        }
    }

    public void mutateEnableDisable(final boolean enable) {
        final List<Sinapsa> candidates = new ArrayList<Sinapsa>();
        for (final Sinapsa gene : genLinkVeza)
            if (gene.omogucen != enable)
                candidates.add(gene);

        if (candidates.isEmpty())
            return;

        final Sinapsa gene = candidates.get(rnd.nextInt(candidates.size()));
        gene.omogucen = !gene.omogucen;
    }

    public void mutateLink(final boolean forceBias) {
        final int neuron1 = randomNeuron(false, true);
        final int neuron2 = randomNeuron(true, false);

        final Sinapsa newLink = new Sinapsa();
        newLink.input = neuron1;
        newLink.output = neuron2;

        if (forceBias)
            newLink.input = INPUTI - 1;

        if (sadrziLink(newLink))
            return;

        newLink.inovacija = ++OsnovneInfomacije.inovacija;
        newLink.tezina = rnd.nextDouble() * 4.0 - 2.0;

        genLinkVeza.add(newLink);
    }

    public void mutateNode() {
        if (genLinkVeza.isEmpty())
            return;

        final Sinapsa gene = genLinkVeza.get(rnd.nextInt(genLinkVeza.size()));
        if (!gene.omogucen)
            return;
        gene.omogucen = false;

        ++maxNeurona;

        final Sinapsa gene1 = gene.clone();
        gene1.output = maxNeurona;
        gene1.tezina = 1.0;
        gene1.inovacija = ++OsnovneInfomacije.inovacija;
        gene1.omogucen = true;
        genLinkVeza.add(gene1);

        final Sinapsa gene2 = gene.clone();
        gene2.input = maxNeurona;
        gene2.inovacija = ++OsnovneInfomacije.inovacija;
        gene2.omogucen = true;
        genLinkVeza.add(gene2);
    }

    public void mutacijaTacke() {
        for (final Sinapsa gene : genLinkVeza)
            if (rnd.nextDouble() < PERTURBATION)
                gene.tezina += rnd.nextDouble() * mutacija[6] * 2.0
                        - mutacija[6];
            else
                gene.tezina = rnd.nextDouble() * 4.0 - 2.0;
    }

    public int randomNeuron(final boolean nonInput, final boolean nonOutput) {
        final List<Integer> neurons = new ArrayList<Integer>();

        if (!nonInput)
            for (int i = 0; i < INPUTI; ++i)
                neurons.add(i);

        if (!nonOutput)
            for (int i = 0; i < OUTPUT; ++i)
                neurons.add(INPUTI + i);

        for (final Sinapsa gene : genLinkVeza) {
            if ((!nonInput || gene.input >= INPUTI)
                    && (!nonOutput || gene.input >= INPUTI + OUTPUT))
                neurons.add(gene.input);
            if ((!nonInput || gene.output >= INPUTI)
                    && (!nonOutput || gene.output >= INPUTI + OUTPUT))
                neurons.add(gene.output);
        }

        return neurons.get(rnd.nextInt(neurons.size()));
    }

    public boolean istaVrsta(final Genom genom) {
        final double dd = DELTA_DISJOINT * disjoint(genom);
        final double dw = DELTA_WEIGHTS * tezine(genom);
        return dd + dw < DELTA_THRESHOLD;
    }

    public double tezine(final Genom genom) {
        double sum = 0.0;
        double slucajnost = 0.0;
        search: for (final Sinapsa gene : genLinkVeza)
            for (final Sinapsa drugiGen : genom.genLinkVeza)
                if (gene.inovacija == drugiGen.inovacija) {
                    sum += Math.abs(gene.tezina - drugiGen.tezina);
                    ++slucajnost;
                    continue search;
                }
        return sum / slucajnost;
    }
}
