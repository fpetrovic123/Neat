/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fb;

import static utility.Utility.SIRINA;



/**
 *
 * @author PVC
 */
public class Cev {
        public final double visinaCevi ;
        public double       pozicija;
        public boolean      proslaCev;

    public double getVisina() {
        return visinaCevi;
    }

        public Cev(final int height) {
            this.visinaCevi = height;
            pozicija = SIRINA;
            proslaCev = false;
        }

    public double getPozicija() {
        return pozicija;
    }

    public void setPozicija(double position) {
        this.pozicija = position;
    }

    public boolean isProslaCev() {
        return proslaCev;
    }

    public void setProslaCev(boolean passed) {
        this.proslaCev = passed;
    }
        
}
