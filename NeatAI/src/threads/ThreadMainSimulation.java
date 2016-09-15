package threads;
import main.NEAT;

public class ThreadMainSimulation extends Thread{
String args1[];

    public ThreadMainSimulation(String args1[]) {
        this.args1=args1;
    }
    @Override
    public void run() {
        NEAT n = new NEAT();
        n.main(args1);
        n.run();
    }
    
    
    
}
