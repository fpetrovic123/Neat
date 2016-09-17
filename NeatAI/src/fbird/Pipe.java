package fbird;

import static utility.Utility.HEIGHT1;

public class Pipe {
    
    private double heightPipe;
    private boolean pass;
    private double position;

    public double getHeightPipe() {
        return heightPipe;
    }

    public void setHeightPipe(double heightPipe) {
        this.heightPipe = heightPipe;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }
        public Pipe(final int height) {
            this.heightPipe = height;
            position = HEIGHT1;
            pass = false;
        }
        
}
