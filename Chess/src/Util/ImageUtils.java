package Util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class ImageUtils {
    
    private static final String IMAGES_FOLDER = "/Images/";
    public static final String GIF = ".gif";
    public static final String JPG = ".jpg";
    public static final String PNG = ".png";
    
    private ImageUtils() {
        
    }
    
    public static BufferedImage readImage(String imageName, String imageType) {
        try {
            return ImageIO.read(ImageUtils.class.getResourceAsStream(IMAGES_FOLDER + imageName + imageType));
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    public static Image readGIFImage(String imageName) {
        return new ImageIcon(ImageUtils.class.getResource(IMAGES_FOLDER + imageName + GIF)).getImage();
    }
    
    //code from https://stackoverflow.com/questions/14225518/tinting-image-in-java-improvement
    
    public static BufferedImage tintImage(BufferedImage image, Color tint, float alpha) {
        return tintWithMask(image, generateMask(image, tint, alpha));
    }

    private static BufferedImage createCompatibleImage(int width, int height, int transparency) {
        BufferedImage image = Constants.GRAPHICS_CONFIGURATION.createCompatibleImage(width, height, transparency);
        image.coerceData(true);
        return image;
    }
    
    private static BufferedImage generateMask(BufferedImage source, Color color, float alpha) {
        int width = source.getWidth();
        int height = source.getHeight();
        
        BufferedImage mask = createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D g2D = mask.createGraphics();
        applyQualityRenderingHints(g2D);

        g2D.drawImage(source, 0, 0, null);
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
        g2D.setColor(color);

        g2D.fillRect(0, 0, width, height);
        g2D.dispose();

        return mask;
    }

    private static BufferedImage tintWithMask(BufferedImage master, BufferedImage tint) {
        
        BufferedImage tinted = createCompatibleImage(master.getWidth(), master.getHeight(), Transparency.TRANSLUCENT);
        Graphics2D g2D = tinted.createGraphics();
        applyQualityRenderingHints(g2D);
        g2D.drawImage(master, 0, 0, null);
        g2D.drawImage(tint, 0, 0, null);
        g2D.dispose();

        return tinted;
    }

    private static void applyQualityRenderingHints(Graphics2D g2D) {
        g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}