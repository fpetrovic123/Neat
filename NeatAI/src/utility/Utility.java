
package utility;

import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class Utility {
    
    public static  int SIRINA         = 600;
    public static  int VISINA         = 800;
    public static  int PTICA_SIRINA   = 36;
    public static  int PTICA_VISINA   = 26;
    public static  int POD_SIRINA     = 680;
    public static  int POD_DUZINA     = 224;
    public static  int POD_ODSTUPANJE = 96;
    public static  int POD_BRZINA     = 5;
    public static  int CEV_SIRINA     = 100;
    public static  int CEV_VISINA     = 640;
    public static  int CEVI_RAZMAK = 200;
    
    public static BufferedImage   POZADINA_IMAGE;
    public static BufferedImage[] PTICE_IMAGES;
    public static BufferedImage   ZEMLJA_IMAGE;
    public static BufferedImage   CEV1_IMAGE;
    public static BufferedImage   CEV2_IMAGE;
    public static Font            FONT;
    
    
        static {
        try {
            POZADINA_IMAGE = skaliraj(ImageIO.read(new File("pozadina.png")));
            ZEMLJA_IMAGE = skaliraj(ImageIO.read(new File("zemlja.png")));
            final BufferedImage birdImage = ImageIO.read(new File("ptice.png"));
            PTICE_IMAGES = new BufferedImage[] {
                    birdImage.getSubimage(0, 0, 36, 26),
                    birdImage.getSubimage(36, 0, 36, 26),
                    birdImage.getSubimage(72, 0, 36, 26) };
            CEV1_IMAGE = skaliraj(ImageIO.read(new File("cev1.png")));
            CEV2_IMAGE = skaliraj(ImageIO.read(new File("cev2.png")));
            FONT = Font.createFont(Font.TRUETYPE_FONT,
                    new File("timesbd.TTF"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        
    }
        
    private static BufferedImage toBufferedImage(final Image slika) {
        final BufferedImage buffered = new BufferedImage(slika.getWidth(null),
                slika.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        buffered.getGraphics().drawImage(slika, 0, 0, null);
        return buffered;
    }

    private static BufferedImage skaliraj(final Image slika) {
        return toBufferedImage(slika.getScaledInstance(slika.getWidth(null) * 2,
                slika.getHeight(null) * 2, Image.SCALE_FAST));
    }
        
}
