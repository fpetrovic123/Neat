package neat;
// OVA KLASA PRESTAVLJA KONKECIJU IZMEDJU NEURONA TACNIJE NJIHOVU KONEKCIJU  TEZINE BLIZU TRANSFER FUNKCIJE!


public class Sinapsa {
    public int     input      = 0;
    public int     output     = 0;
    public double  tezina     = 0.0;
    public boolean omogucen    = true;
    public int     inovacija = 0;

    @Override
    public Sinapsa clone() {
        final Sinapsa sinapsa = new Sinapsa();
        sinapsa.input = input;
        sinapsa.output = output;
        sinapsa.tezina = tezina;
        sinapsa.omogucen = omogucen;
        sinapsa.inovacija = inovacija;
        return sinapsa;
    }
}
