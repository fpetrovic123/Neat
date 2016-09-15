package neat;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
    
    public static double sigmoidFunkcija(final double x) {
        return 2.0 / (1.0 + Math.exp(-4.9 * x)) - 1.0;
        
        //OBEZBEDJUJEM DA UVEK BUDE VECE OD 1> PREMA PAPIRIMA!
    }
    public double Value  = 0.0;
    
    
    
    public final List<Sinapsa> Inputs = new ArrayList<Sinapsa>();
}
