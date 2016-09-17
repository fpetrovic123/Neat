package neat;

public class Synapse {
    public int     input      = 0;
    public int     output     = 0;
    public double  weight     = 0.0;
    public boolean isEnabled    = true;
    public int     inovation = 0;
    
    
    
    @Override
    public Synapse clone() {
        final Synapse synapse = new Synapse();
        synapse.input = input;
        synapse.output = output;
        synapse.weight = weight;
        synapse.isEnabled = isEnabled;
        synapse.inovation = inovation;
        return synapse;
    }
}
