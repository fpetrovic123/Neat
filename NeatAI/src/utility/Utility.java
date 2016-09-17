
package utility;

import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class Utility {
    

    public static BufferedImage   BIRD_IMAGE;
    public static BufferedImage[] BIRDS;
    public static BufferedImage   EARTH;
    public static BufferedImage   PIPE1_IMAGE;
    public static BufferedImage   PIPE2_IMAGE;
    public static Font            FONT;
    
    public static  int WIDTH1         = 600;
    public static  int HEIGHT1         = 800;
    public static  int B_W   = 36;
    public static  int B_H   = 26;
    public static  int F_W     = 680;
    public static  int F_H     = 224;
    public static  int F_OFFSET = 96;
    public static  int F_SPEED     = 5;
    public static  int PIPE_W     = 100;
    public static  int PIPE_H     = 640;
    public static  int EMPTY_SPACE = 200;
    
    
    
        static {
        try {
            BIRD_IMAGE = ScaleImage(ImageIO.read(new File("pozadina.png")));
            EARTH = ScaleImage(ImageIO.read(new File("zemlja.png")));
            final BufferedImage birdImage = ImageIO.read(new File("ptice.png"));
            BIRDS = new BufferedImage[] {
                    birdImage.getSubimage(0, 0, 36, 26),
                    birdImage.getSubimage(36, 0, 36, 26),
                    birdImage.getSubimage(72, 0, 36, 26) };
            PIPE1_IMAGE = ScaleImage(ImageIO.read(new File("cev1.png")));
            PIPE2_IMAGE = ScaleImage(ImageIO.read(new File("cev2.png")));
            FONT = Font.createFont(Font.TRUETYPE_FONT,
                    new File("timesbd.TTF"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        
    }
        
    private static BufferedImage toBufferedImage(final Image image) {
        final BufferedImage buffered = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        buffered.getGraphics().drawImage(image, 0, 0, null);
        return buffered;
    }

    private static BufferedImage ScaleImage(final Image image) {
        return toBufferedImage(image.getScaledInstance(image.getWidth(null) * 2,
                image.getHeight(null) * 2, Image.SCALE_FAST));
    }
        
}
