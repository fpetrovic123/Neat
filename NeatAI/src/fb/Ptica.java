package fb;

import neat.Genom;
import static neat.OsnovneInfomacije.rnd;
import neat.Vrsta;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.WeakHashMap;
import static utility.Utility.VISINA;
import static utility.Utility.PTICA_SIRINA;
import static utility.Utility.PTICA_VISINA;
import static utility.Utility.PTICE_IMAGES;

public class Ptica {
    ///TODO GETERI I SETERI !
        public final Genom gen;
        public double       visina;
        public double       kurs;
        public double       ugao;
        public boolean      leti;
        public int          brojMahanjaKrilima;
        public boolean      mrtva;
        
        
        
        public BufferedImage[] images;

        
        
        public Ptica(final Vrsta species, final Genom genome) {
            if (cache.containsKey(species))
                images = cache.get(species);
            else {
                final Color color = new Color(rnd.nextInt(0x1000000));
                images = new BufferedImage[3];
                for (int i = 0; i < 3; ++i)
                    images[i] = colorBird(PTICE_IMAGES[i], color);
                cache.put(species, images);
            }

            this.gen = genome;
            visina = VISINA / 2.0;
        }
        
        
        
        
        
        
           public static Map<Vrsta, BufferedImage[]> cache = new WeakHashMap<Vrsta, BufferedImage[]>();

        public static BufferedImage colorBird(final BufferedImage refImage,
                final Color color) {
            final BufferedImage slika = new BufferedImage(PTICA_SIRINA,
                    PTICA_VISINA, BufferedImage.TYPE_INT_ARGB);
            final Color bright = color.brighter().brighter();
            final Color dark = color.darker().darker();
            for (int y = 0; y < PTICA_VISINA; ++y)
                for (int x = 0; x < PTICA_SIRINA; ++x) {
                    int argb = refImage.getRGB(x, y);
                    if (argb == 0xffe0802c)
                        argb = dark.getRGB();
                    else if (argb == 0xfffad78c)
                        argb = bright.getRGB();
                    else if (argb == 0xfff8b733)
                        argb = color.getRGB();
                  /*TODO OVDE TESTIRANJE ZA SAMO PRAVOGUAONIK ZBOG KOLIZIJE!
                  int  argb=0xffcccccc; */
                    slika.setRGB(x, y, argb);
                }
            
            return slika;
        }
    }

