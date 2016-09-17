package fbird;

import neat.Genom;
import neat.Group;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.WeakHashMap;

import static neat.StartingInfo.random;
import utility.Utility;
import static utility.Utility.*;

public class Finch {

    public final Genom genom;
    public double hightBird;
    public double directionBird;
    public double angleOfBird;
    public boolean flaying;
    public int numberBH;
    public boolean deadBird;

    public BufferedImage[] imagesBird;

    public Finch(final Group group, final Genom genome) {
        if (cache.containsKey(group)) {
            imagesBird = cache.get(group);
        } else {
            final Color color = new Color(random.nextInt(0x1000000));
            imagesBird = new BufferedImage[3];
            for (int i = 0; i < 3; ++i) {
                imagesBird[i] = colorBird(BIRDS[i], color);
            }
            cache.put(group, imagesBird);
        }

        this.genom = genome;
        hightBird = WIDTH1 / 2.0;
    }

    public static Map<Group, BufferedImage[]> cache = new WeakHashMap<Group, BufferedImage[]>();

    public static BufferedImage colorBird(final BufferedImage refImage,
            final Color color) {
        final BufferedImage image = new BufferedImage(B_W,
                B_H, BufferedImage.TYPE_INT_ARGB);
        final Color bright = color.brighter().brighter();
        final Color dark = color.darker().darker();
        for (int y = 0; y < B_H; ++y) {
            for (int x = 0; x < B_W; ++x) {
                int argb = refImage.getRGB(x, y);
                if (argb == 0xffe0802c) {
                    argb = dark.getRGB();
                } else if (argb == 0xfffad78c) {
                    argb = bright.getRGB();
                } else if (argb == 0xfff8b733) {
                    argb = color.getRGB();
                }
                image.setRGB(x, y, argb);
            }
        }

        return image;
    }
}
