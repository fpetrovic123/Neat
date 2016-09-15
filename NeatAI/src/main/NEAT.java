package main;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import neat.Genom;
import neat.Neuron;
import neat.OsnovneInfomacije;
import neat.Vrsta;
import neat.Sinapsa;
import fb.Celija;
import fb.Cev;
import fb.Ptica;

import static utility.Utility.FONT;
import static utility.Utility.SIRINA;
import static utility.Utility.VISINA;

import static utility.Utility.PTICA_SIRINA;
import static utility.Utility.PTICA_VISINA;
import static utility.Utility.POD_SIRINA;
import static utility.Utility.POD_DUZINA;
import static utility.Utility.POD_ODSTUPANJE;
import static utility.Utility.POD_BRZINA;
import static utility.Utility.CEV_SIRINA;
import static utility.Utility.CEV_VISINA;
import static utility.Utility.CEVI_RAZMAK;
import static neat.OsnovneInfomacije.POPULACIJA;
import static neat.OsnovneInfomacije.INPUTI;
import static neat.OsnovneInfomacije.OUTPUT;
import static utility.Utility.POZADINA_IMAGE;
import static utility.Utility.ZEMLJA_IMAGE;
import static utility.Utility.CEV1_IMAGE;
import static utility.Utility.CEV2_IMAGE;

public class NEAT extends JPanel implements Runnable {

    
public static int globalScore = 0;

public static final Random randomize = new Random();

 



    public static void main(final String[] args) {
        final JFrame frame = new JFrame();
        frame.setResizable(true);
        frame.setTitle("Simulation");
        frame.setPreferredSize(new Dimension(SIRINA, VISINA));
        
         NEAT neat = new NEAT();
        frame.add(neat);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        neat.run();
        
        
    }


    private int brzinaPostavnjanjaCevi;
    private int takt;
    private int taktCevi;


    private Ptica najboljaPtica;
    private int  skorLokalni;

    public void evaluacija() {

        Cev sledecaCev = null;

        for (final Cev cev : cevi)
            if (cev.pozicija + CEV_SIRINA > SIRINA / 3 - PTICA_SIRINA / 2
                    && (sledecaCev == null || cev.pozicija < sledecaCev.pozicija))
                sledecaCev = cev;
//PTICE

for (final Ptica ptica : ptice) {
            if (ptica.mrtva)
                continue;
// INPUTI U   MREZU 
            final double[] input = new double[4];
            input[0] = ptica.visina / VISINA;
            if (sledecaCev == null) {
                input[1] = 0.5;
                input[2] = 1.0;
            } else {
                input[1] = sledecaCev.visinaCevi / VISINA;
                input[2] = sledecaCev.pozicija / SIRINA;
            }
            
// INPUT TESIRANJE ZBOG IMPLEMENTACIJE

            input[3] = 1.0;
            
// NEAT OPALJIVANJE
//TODO: NAJZNACAJNIJE DVE LINIJE KODA!!!!!!!!!!!

            final double[] output = ptica.gen.evaulacijaMrezeGen(input);
            if (output[0] > 0.5)
                ptica.leti = true;
        }
    }

    public void inicijalizujIgru() {
        
        // Brzina stvaranja cevi sto manji broj to se brze stvaraju tubes.
        // Tik igre
        // Tik tube brizna stvaranja
        //Scor
        brzinaPostavnjanjaCevi = 75;
        takt = 0;
        taktCevi = 0;

        najboljaPtica = null;
        
        skorLokalni = 0;

        ptice.clear();
        for (final Vrsta vrsta : OsnovneInfomacije.vrste)
            for (final Genom genom : vrsta.genomi) {
                genom.generisiMrezu();
                ptice.add(new Ptica(vrsta, genom));
            }
        cevi.clear();
    }

    
    public void nauci() {
        
        najboljaPtica = ptice.get(0);
        
        boolean sveMrtve = true;
        
        for (final Ptica ptica : ptice) {
            if (ptica.mrtva)
                continue;
            sveMrtve = false;

            double fitnestNauci = takt - ptica.brojMahanjaKrilima * 1.5;
            fitnestNauci = fitnestNauci == 0.0 ? -1.0 : fitnestNauci;

            ptica.gen.fitnest = fitnestNauci;
            if (fitnestNauci > OsnovneInfomacije.maksimalniFitnest)
                OsnovneInfomacije.maksimalniFitnest = fitnestNauci;

            if (fitnestNauci > najboljaPtica.gen.fitnest)
                najboljaPtica = ptica;
        }

        if (sveMrtve) {
            OsnovneInfomacije.newGeneration();
            inicijalizujIgru();
        }
    }
///UPDATE IGRE SAME!
    public void update() {
        ++takt;
        ++taktCevi;

        if (taktCevi == brzinaPostavnjanjaCevi) {
            final int visina = POD_ODSTUPANJE + 100
                    + randomize.nextInt(VISINA - 200 - CEVI_RAZMAK - POD_ODSTUPANJE);
            cevi.add(new Cev(visina));
            taktCevi = 0;
        }

        final Iterator<Cev> it = cevi.iterator();
        while (it.hasNext()) {
            
            final Cev cev = it.next();
            cev.pozicija -= POD_BRZINA;
            if (cev.pozicija + CEV_SIRINA < 0.0)
                it.remove();
            if (!cev.proslaCev && cev.pozicija + CEV_SIRINA < SIRINA / 3
                    - PTICA_SIRINA / 2) {
                ++skorLokalni;
                if (skorLokalni % 10 == 0) {
                    brzinaPostavnjanjaCevi -= 5;
                    brzinaPostavnjanjaCevi = Math.max(brzinaPostavnjanjaCevi, 20);
                }
                if(skorLokalni>globalScore){
                    globalScore=skorLokalni;
                }
                cev.proslaCev = true;
            }
        }

        for (final Ptica ptica : ptice) {
            if (ptica.mrtva)
                continue;

            if (ptica.leti) {
                ptica.kurs = 10;
                ptica.leti = false;
                ++ptica.brojMahanjaKrilima;
            }

            ptica.visina += ptica.kurs;
            ptica.kurs -= 0.98;
            ptica.ugao = 3.0 * ptica.kurs;
            ptica.ugao = Math.max(-90.0, Math.min(90.0, ptica.ugao));

            if (ptica.visina > VISINA) {
              ptica.mrtva= true;
            }
            if (ptica.visina < POD_ODSTUPANJE + PTICA_VISINA / 2)
                ptica.mrtva = true;

            final AffineTransform at = new AffineTransform();
            at.translate(SIRINA / 3 - PTICA_VISINA / 2, VISINA - ptica.visina);
            at.rotate(-ptica.ugao / 180.0 * Math.PI, PTICA_SIRINA / 2,
                    PTICA_VISINA / 2);
            at.translate(0, 36);
            
            
            final Shape granice = new GeneralPath(GRANICE)
                    .createTransformedShape(at);
            
            
            
            for (final Cev tube : cevi) {
                final Rectangle2D pravugaonikCevi = new Rectangle2D.Double(
                        tube.pozicija,
                        VISINA - tube.visinaCevi - CEVI_RAZMAK - CEV_VISINA,
                        CEV_SIRINA, CEV_VISINA);
                final Rectangle2D floorTube = new Rectangle2D.Double(
                        tube.pozicija, VISINA - tube.visinaCevi, CEV_SIRINA,
                        CEV_VISINA);
                
//NIJE NEKA VALIDACIJA !
                if (granice.intersects(pravugaonikCevi)
                        || granice.intersects(floorTube)) {
                    ptica.mrtva = true;
                    break;
                }
            }
        }
    }

@Override
    public void run() {
        OsnovneInfomacije.initializePool();

        inicijalizujIgru();
        while (true) {
           
    //EValuacija  proverena 
           
            evaluacija();
//PROVERENA
            update();
            
            nauci();

            repaint();
            try {
                Thread.sleep(25L);
            } catch (final InterruptedException e) {
            }
        }
    }

    
    private final List<Ptica> ptice = new ArrayList<>();
    private final List<Cev> cevi = new ArrayList<>();



 @Override
    public void paint(final Graphics g_) {
        final Graphics2D g2d = (Graphics2D) g_;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(POZADINA_IMAGE, 0, 0, SIRINA, VISINA, null);

//CRTANJE CEVI 1  i 2 
        for (final Cev cev : cevi) {
            g2d.drawImage(CEV1_IMAGE, (int) cev.pozicija,
                    VISINA - (int) cev.visinaCevi - CEVI_RAZMAK - CEV_VISINA,
                    CEV_SIRINA, CEV_VISINA, null);
            g2d.drawImage(CEV2_IMAGE, (int) cev.pozicija,
                    VISINA - (int) cev.visinaCevi, CEV_SIRINA, CEV_VISINA, null);
        }
        
//CRTANJE PODA         

        g2d.drawImage(ZEMLJA_IMAGE,
                -(POD_BRZINA * takt % (SIRINA - POD_SIRINA)),
                VISINA - POD_ODSTUPANJE, POD_SIRINA, POD_DUZINA, null);

        
//CRTANJE PTICA        
        
        int zive = 0;
        
//SAMO GORE I DOLE ! PTICA

        final int animacija = takt / 3 % 3;
        for (final Ptica bird : ptice) {
            
            if (bird.mrtva)
                continue;
            ++zive;
            final AffineTransform at = new AffineTransform();
            at.translate(SIRINA / 3 - PTICA_VISINA / 3, VISINA - bird.visina);
            at.rotate(-bird.ugao / 180.0 * Math.PI, PTICA_SIRINA / 2,
                    PTICA_VISINA / 2);
            g2d.drawImage(bird.images[animacija], at, null);
            
        }
//POZICIONIRANJE SCORA KOLIKO JE PROSAO CEVI! 

        final Font skorFont = FONT.deriveFont(50f);
        g2d.setFont(skorFont);
        
        final String skorTekst = Integer.toString(skorLokalni);
        final GlyphVector glyphsVector = skorFont
                .createGlyphVector(g2d.getFontRenderContext(), skorTekst);
        

       
        
        final Rectangle2D skorOkvir = glyphsVector.getVisualBounds();
        
        final int skorX = SIRINA / 2 - (int) skorOkvir.getWidth() / 2;
        final int skorY = VISINA / 6 + (int) skorOkvir.getHeight() / 2;
        final Shape outline = glyphsVector.getOutline(skorX, skorY);
        
        
        
        g2d.setStroke(new BasicStroke(8f));
        g2d.setColor(Color.BLACK);
        g2d.draw(outline);
        g2d.setColor(Color.WHITE);
        g2d.drawString(skorTekst, skorX, skorY);
        g2d.setStroke(new BasicStroke(1f));
        
        //POZICIONIRANJE GLOBALNOG SOCRA KOLIKO JE DOSADA NAJVISE USPELO
            
        final Font globalniSkorFont =FONT.deriveFont(20f);
        g2d.setFont(globalniSkorFont);
        
        final String globalScoreText=Integer.toString(globalScore);
        final GlyphVector glV=skorFont.createGlyphVector(g2d.getFontRenderContext(), globalScoreText);
        
        final int gSkorX=420;
        final int gSkorY= 150;
        final Shape outline1= glV.getOutline(gSkorX,gSkorY);
        g2d.drawString("Najbolji skor:"+globalScoreText, gSkorX, gSkorY);
        
        //POZICIONIRANJE GRAFA  JAKO BITNO

        final int minX = 30;
        final int maxX =  200;

        final Map<Integer, Celija> mreza = new HashMap<Integer, Celija>();
        if(najboljaPtica!=null){
        for (final Entry<Integer, Neuron> entry : najboljaPtica.gen.mreza
                .entrySet()) {
            
            final int i = entry.getKey();
            final Neuron neuron = entry.getValue();
            final int x;
            final int y;
            if (i < OsnovneInfomacije.INPUTI) {
                x = 15;
                y = 15 + 20 * i;
            } else if (entry.getKey() < INPUTI + OUTPUT) {
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
            mreza.put(i, new Celija(x, y, neuron.Value));
        }

        for (int n = 0; n < 4; ++n)
            for (final Sinapsa gene : najboljaPtica.gen.genLinkVeza)
                if (gene.omogucen) {
                    final Celija c1 = mreza.get(gene.input);
                    final Celija c2 = mreza.get(gene.output);
                    if (gene.input >= INPUTI + OUTPUT) {
                        c1.x = (int) (0.75 * c1.x + 0.25 * c2.x);
                        if (c1.x >= c2.x)
                            c1.x = c1.x - 60;
                        if (c1.x < minX)
                            c1.x = minX;
                        if (c1.x > maxX)
                            c1.x = maxX;
                        c1.y = (int) (0.75 * c1.y + 0.25 * c2.y);
                    }
                    if (gene.output >= INPUTI + OUTPUT) {
                        c2.x = (int) (0.25 * c1.x + 0.75 * c2.x);
                        if (c1.x >= c2.x)
                            c2.x = c2.x + 60;
                        if (c2.x < minX)
                            c2.x = minX;
                        if (c2.x > maxX)
                            c2.x = maxX;
                        c2.y = (int) (0.25 * c1.y + 0.75 * c2.y);
                    }
                }

        for (final Sinapsa gene : najboljaPtica.gen.genLinkVeza)
            if (gene.omogucen) {
                final Celija c1 = mreza.get(gene.input);
                final Celija c2 = mreza.get(gene.output);
                final float vrednost = (float) Math
                        .abs(Neuron.sigmoidFunkcija(gene.tezina));
                final Color color;
                if (Neuron.sigmoidFunkcija(gene.tezina) > 0.0)
                    color = Color.getHSBColor(2f / 3f, 1f, vrednost);
                else
                    color = Color.getHSBColor(0f, 1f, vrednost);
                g2d.setColor(new Color(color.getRed(), color.getGreen(),
                        color.getBlue(), 0x80));
                g2d.drawLine(c1.x + 8, c1.y + 5, c2.x + 2, c2.y + 5);
            }
       

        for (final Celija celija : mreza.values())
            printCelije(g2d, celija);

        g2d.setColor(new Color(0x80000000, true));
        g2d.setFont(FONT.deriveFont(14f));
        
        g2d.drawString("Generacija " + OsnovneInfomacije.generacija, 20, 120);
        Dimension d = getBounds(g2d, g2d.getFont(),
                "Žive " + zive + "/" + POPULACIJA);
        g2d.drawString("Žive " + zive + "/" + POPULACIJA, 20,
                140);
        d = getBounds(g2d, g2d.getFont(),
                "Ukupan fitnest" + najboljaPtica.gen.fitnest + "/" + OsnovneInfomacije.maksimalniFitnest);
        g2d.drawString("Ukupan fitnest " + najboljaPtica.gen.fitnest + "/" + OsnovneInfomacije.maksimalniFitnest,
                20, 160);
         }
    }
    
    
    public void printCelije(final Graphics2D g2d, final Celija celija) {
        
        final float vrednost = (float) Math.abs(celija.vrednost);
        final Color color;
        if (celija.vrednost > 0.0)
            color = Color.getHSBColor(2f / 3f, 1f, vrednost);
        else
            color = Color.getHSBColor(0f, 1f, vrednost);
        g2d.setColor(new Color(color.getRed(), color.getGreen(),
                color.getBlue(), 0x80));
        g2d.fillRect(celija.x, celija.y, 10, 10);
        g2d.setColor(g2d.getColor().darker().darker());
        g2d.drawRect(celija.x, celija.y, 10, 10);
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
