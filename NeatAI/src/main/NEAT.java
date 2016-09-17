package main;

import fbird.Cell;
import fbird.Finch;
import fbird.Pipe;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import neat.Genom;
import neat.Neuron;
import neat.StartingInfo;
import neat.Group;
import neat.Synapse;
import static utility.Utility.*;
import static neat.StartingInfo.POPULATION;
import static neat.StartingInfo.NUMBER_OF_IMPUTS;
import static neat.StartingInfo.NUMBER_OF_OUTPUTS;

public class NEAT extends JPanel implements Runnable {

    public static int globalScore = 0;
  
    
    public static void main(final String[] args) {
        final JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setTitle("Simulation");
        frame.setPreferredSize(new Dimension(utility.Utility.WIDTH1, utility.Utility.HEIGHT1));
        NEAT neat = new NEAT();
        frame.add(neat);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        neat.run();
    }
    
    public static final Random randomize = new Random();
    private final List<Finch> birds = new LinkedList<>();
    private final List<Pipe> pipes = new ArrayList<>();

    private int speedOfSettingPipes;
    private int tick;
    private int tickOfPipes;


    private Finch bestBird;
    private int  localScore;

    public void evolution() {

        Pipe nextPipe = null;

        for (final Pipe pipe : pipes)
            if (pipe.getPosition() + PIPE_W > WIDTH1 / 3 - B_W/  2
                    && (nextPipe == null || pipe.getPosition() < nextPipe.getPosition()))
                nextPipe = pipe;

for (final Finch bird : birds) {
            if (bird.deadBird)
                continue;

            final double[] input = new double[4];
            input[0] = bird.hightBird / HEIGHT1;
            if (nextPipe == null) {
                input[1] = 0.5;
                input[2] = 1.0;
            } else {
                input[1] = nextPipe.getHeightPipe() / HEIGHT1;
                input[2] = nextPipe.getPosition() / WIDTH1;
            }
            

            input[3] = 1.0;
            
// NEAT OPALJIVANJE
//TODO: NAJZNACAJNIJE DVE LINIJE KODA!!!!!!!!!!!

            final double[] output = bird.genom.evolutionOfNetwork(input);
            if (output[0] > 0.5)
                bird.flaying = true;
        }
    }

    public void initializeGame() {
        
        // Brzina stvaranja cevi sto manji broj to se brze stvaraju tubes.
        // Tik igre
        // Tik tube brizna stvaranja
        //Scor
        speedOfSettingPipes = 75;
        tick = 0;
        tickOfPipes = 0;

        bestBird = null;
        
        localScore = 0;

        birds.clear();
        for (final Group group : StartingInfo.groups)
            for (final Genom genom : group.genoms) {
                genom.generateNetwork();
                birds.add(new Finch(group, genom));
            }
        pipes.clear();
    }

    
    public void learn() {
        
        bestBird = birds.get(0);
        
        boolean allDead = true;
        
        for (final Finch bird : birds) {
            if (bird.deadBird)
                continue;
            allDead = false;

            double fitnessLearn = tick - bird.numberBH * 1.5;
            fitnessLearn = fitnessLearn == 0.0 ? -1.0 : fitnessLearn;

            bird.genom.setFitness(fitnessLearn);
            if (fitnessLearn > StartingInfo.maxFitness)
                StartingInfo.maxFitness = fitnessLearn;

            if (fitnessLearn > bestBird.genom.getFitness())
                bestBird = bird;
        }

        if (allDead) {
            StartingInfo.newGeneration();
            initializeGame();
        }
    }
///UPDATE IGRE SAME!
    public void update() {
        ++tick;
        ++tickOfPipes;

        if (tickOfPipes == speedOfSettingPipes) {
            final int visina = F_OFFSET + 100
                    + randomize.nextInt(HEIGHT1 - 200 - EMPTY_SPACE - F_OFFSET);
            pipes.add(new Pipe(visina));
            tickOfPipes = 0;
        }

        final Iterator<Pipe> it = pipes.iterator();
        while (it.hasNext()) {
            
            final Pipe pipe = it.next();
            pipe.setPosition(pipe.getPosition()-F_SPEED);
            if (pipe.getPosition() + PIPE_W < 0.0)
                it.remove();
            if (!pipe.isPass() && pipe.getPosition() + PIPE_W < WIDTH1 / 3
                    - B_W / 2) {
                ++localScore;
                if (localScore % 10 == 0) {
                    speedOfSettingPipes -= 5;
                    speedOfSettingPipes = Math.max(speedOfSettingPipes, 20);
                }
                if(localScore>globalScore){
                    globalScore=localScore;
                }
                pipe.setPass(true);
            }
        }

        for (final Finch bird : birds) {
            if (bird.deadBird)
                continue;

            if (bird.flaying) {
                bird.directionBird = 10;
                bird.flaying = false;
                ++bird.numberBH;
            }

            bird.hightBird += bird.directionBird;
            bird.directionBird -= 0.98;
            bird.angleOfBird = 3.0 * bird.directionBird;
            bird.angleOfBird = Math.max(-90.0, Math.min(90.0, bird.angleOfBird));

            if (bird.hightBird > HEIGHT1) {
              bird.deadBird= true;
            }
            if (bird.hightBird < F_OFFSET + B_H / 2)
                bird.deadBird = true;

            final AffineTransform at = new AffineTransform();
            at.translate(WIDTH1 / 3 - B_H / 2, HEIGHT1 - bird.hightBird);
            at.rotate(-bird.angleOfBird / 180.0 * Math.PI, B_W / 2,
                    B_H / 2);
            at.translate(0, 36);
            
            
            final Shape borders = new GeneralPath(GRANICE)
                    .createTransformedShape(at);
            
            for (final Pipe tube : pipes) {
                final Rectangle2D rectanglePipe = new Rectangle2D.Double(
                        tube.getPosition(),
                        HEIGHT1 - tube.getHeightPipe() - EMPTY_SPACE - PIPE_H,
                        PIPE_W, PIPE_H);
                final Rectangle2D floorTube = new Rectangle2D.Double(
                        tube.getPosition(), HEIGHT1 - tube.getHeightPipe(), PIPE_W,
                        PIPE_H);
                
//NIJE NEKA VALIDACIJA !
                if (borders.intersects(rectanglePipe)
                        || borders.intersects(floorTube)) {
                    bird.deadBird = true;
                    break;
                }
            }
        }
    }

@Override
    public void run() {
        StartingInfo.initializePool();
        
        initializeGame();
        
        while (true) {
           
    //EValuacija  proverena 
           
            evolution();
//PROVERENA
            update();
            
            learn();

            repaint();
            try {
                Thread.sleep(25L);
            } catch (final InterruptedException e) {
            }
        }
    }

    
 



 @Override
    public void paint(final Graphics g_) {
        final Graphics2D g2d = (Graphics2D) g_;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(BIRD_IMAGE, 0, 0, WIDTH1, HEIGHT1, null);

//CRTANJE CEVI 1  i 2 
        for (final Pipe pipe : pipes) {
            g2d.drawImage(PIPE1_IMAGE, (int) pipe.getPosition(),
                    HEIGHT1 - (int) pipe.getHeightPipe() - EMPTY_SPACE - PIPE_H,
                    PIPE_W, PIPE_H, null);
            g2d.drawImage(PIPE2_IMAGE, (int) pipe.getPosition(),
                    HEIGHT1 - (int) pipe.getHeightPipe(), PIPE_W, PIPE_H, null);
        }
        
//CRTANJE PODA         

        g2d.drawImage(EARTH,
                -(F_SPEED * tick % (WIDTH1 - F_W)),
                HEIGHT1 - F_OFFSET, F_W, F_H, null);

        
//CRTANJE PTICA        
        
        int alive = 0;
        
//SAMO GORE I DOLE ! PTICA

        final int animation = tick / 3 % 3;
    try{
        for (final Finch bird : birds) {
            
            if (bird.deadBird)
                continue;
            ++alive;
            final AffineTransform at = new AffineTransform();
            at.translate(WIDTH1 / 3 - B_H / 3, HEIGHT1 - bird.hightBird);
            at.rotate(-bird.angleOfBird / 180.0 * Math.PI, B_W / 2,
                    B_H / 2);
            g2d.drawImage(bird.imagesBird[animation], at, null);
            
        }
    }
    catch(Exception ex){}
//POZICIONIRANJE SCORA KOLIKO JE PROSAO CEVI! 

        final Font scoreFont = FONT.deriveFont(50f);
        g2d.setFont(scoreFont);
        
        final String scoreText = Integer.toString(localScore);
        final GlyphVector glyphsVector = scoreFont
                .createGlyphVector(g2d.getFontRenderContext(), scoreText);
        

       
        
        final Rectangle2D scoreBorder = glyphsVector.getVisualBounds();
        
        final int scoreX = WIDTH1 / 2 - (int) scoreBorder.getWidth() / 2;
        final int scoreY = HEIGHT1 / 6 + (int) scoreBorder.getHeight() / 2;
        final Shape outline = glyphsVector.getOutline(scoreX, scoreY);
        
        
        
        g2d.setStroke(new BasicStroke(8f));
        g2d.setColor(Color.BLACK);
        g2d.draw(outline);
        g2d.setColor(Color.WHITE);
        g2d.drawString(scoreText, scoreX, scoreY);
        g2d.setStroke(new BasicStroke(1f));
        
        //POZICIONIRANJE GLOBALNOG SOCRA KOLIKO JE DOSADA NAJVISE USPELO
            
        final Font globalScoreFont =FONT.deriveFont(20f);
        g2d.setFont(globalScoreFont);
        
        final String globalScoreText=Integer.toString(globalScore);
        final GlyphVector glV=scoreFont.createGlyphVector(g2d.getFontRenderContext(), globalScoreText);
        
        final int gSkorX=420;
        final int gSkorY= 150;
        final Shape outline1= glV.getOutline(gSkorX,gSkorY);
        g2d.drawString("Najbolji skor:"+globalScoreText, gSkorX, gSkorY);
        
        //POZICIONIRANJE GRAFA  JAKO BITNO

        final int minX = 30;
        final int maxX =  200;

        final Map<Integer, Cell> network = new HashMap<Integer, Cell>();
        if(bestBird!=null){
        for (final Entry<Integer, Neuron> entry : bestBird.genom.network
                .entrySet()) {
            
            final int i = entry.getKey();
            final Neuron neuron = entry.getValue();
            final int x;
            final int y;
            if (i < StartingInfo.NUMBER_OF_IMPUTS) {
                x = 15;
                y = 15 + 20 * i;
            } else if (entry.getKey() < NUMBER_OF_IMPUTS + NUMBER_OF_OUTPUTS) {
                x = 200 - 47;
                y = 80;
                int opacity = 0x80000000;
                if (neuron.Value < 0.5)
                    opacity = 0x30000000;
                g2d.setColor(new Color(opacity, true));
            } else {
                x = (minX + maxX) / 2;
                y = 80;
            }
            network.put(i, new Cell(x, y, neuron.Value));
        }

        for (int n = 0; n < 4; ++n)
            for (final Synapse genom : bestBird.genom.genomLink)
                if (genom.isEnabled) {
                    final Cell c1 = network.get(genom.input);
                    final Cell c2 = network.get(genom.output);
                    if (genom.input >= NUMBER_OF_IMPUTS + NUMBER_OF_OUTPUTS) {
                        c1.setX((int)(0.75*c1.getX()+0.25*c2.getX()));
                        
                        if (c1.getX() >= c2.getX())
                            c1.setX(c1.getX() - 60);
                        if (c1.getX() < minX)
                            c1.setX(minX);
                        if (c1.getX() > maxX)
                            c1.setX(maxX);
                        c1.setY((int) (0.75 * c1.getY() + 0.25 * c2.getY()));
                    }
                    if (genom.output >= NUMBER_OF_IMPUTS + NUMBER_OF_OUTPUTS) {
                        c2.setX( (int) (0.25 * c1.getX() + 0.75 * c2.getX())); 
                        if (c1.getX() >= c2.getX())
                            c2.setX(c2.getX() + 60);
                        if (c2.getX() < minX)
                            c2.setX(minX);
                        if (c2.getY() > maxX)
                            c2.setX(maxX);
                        c2.setY((int) (0.25 * c1.getY() + 0.75 * c2.getY()));
                    }
                }

        for (final Synapse gene : bestBird.genom.genomLink)
            if (gene.isEnabled) {
                final Cell c1 = network.get(gene.input);
                final Cell c2 = network.get(gene.output);
                final float vrednost = (float) Math
                        .abs(Neuron.sigmoidFunction(gene.weight));
                final Color color;
                if (Neuron.sigmoidFunction(gene.weight) > 0.0)
                    color = Color.getHSBColor(2f / 3f, 1f, vrednost);
                else
                    color = Color.getHSBColor(0f, 1f, vrednost);
                g2d.setColor(new Color(color.getRed(), color.getGreen(),
                        color.getBlue(), 0x80));
                g2d.drawLine(c1.getX() + 8, c1.getY() + 5, c2.getX() + 2, c2.getY() + 5);
            }
       

        for (final Cell cell : network.values())
            printCell(g2d, cell);

        g2d.setColor(new Color(0x80000000, true));
        g2d.setFont(FONT.deriveFont(14f));
        
        g2d.drawString("Generacija " + StartingInfo.generation, 20, 120);
        Dimension d = getBounds(g2d, g2d.getFont(),
                "Žive " + alive + "/" + POPULATION);
        g2d.drawString("Žive " + alive + "/" + POPULATION, 20,
                140);
        d = getBounds(g2d, g2d.getFont(),
                "Ukupan fitnest" + bestBird.genom.getFitness() + "/" + StartingInfo.maxFitness);
        g2d.drawString("Ukupan fitnest " + bestBird.genom.getFitness() + "/" + StartingInfo.maxFitness,
                20, 160);
         }
    }
    
    
    public void printCell(final Graphics2D g2d, final Cell cell) {
        
        final float vrednost = (float) Math.abs(cell.getValueOfCell());
        final Color color;
        if (cell.getValueOfCell() > 0.0)
            color = Color.getHSBColor(2f / 3f, 1f, vrednost);
        else
            color = Color.getHSBColor(0f, 1f, vrednost);
        g2d.setColor(new Color(color.getRed(), color.getGreen(),
                color.getBlue(), 0x80));
        g2d.fillRect(cell.getX(), cell.getY(), 10, 10);
        g2d.setColor(g2d.getColor().darker().darker());
        g2d.drawRect(cell.getX(), cell.getY(), 10, 10);
    }
    
    
        private static final int[]   XS     = new int[] { 2, 6, 14, 18, 26, 50, 54,
            58, 62, 66, 70, 70, 66, 62, 42, 22, 14, 10, 6, 2 };
    private static final int[]   YS     = new int[] { -34, -38, -42, -46, -50,
            -50, -46, -42, -38, -26, -22, -18, -10, -6, -2, -2, -6, -10, -18,
            -22 };
    private static final Polygon GRANICE = new Polygon(XS, YS, XS.length);



    public static Dimension getBounds(final Graphics2D g, final Font font,
            final String text) {
        final int width = (int) font
                .getStringBounds(text, g.getFontRenderContext()).getWidth();
        final int height = (int) font
                .createGlyphVector(g.getFontRenderContext(), text)
                .getVisualBounds().getHeight();
        return new Dimension(width, height);
    }
}
