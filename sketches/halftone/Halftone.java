package sketches.halftone;

import config.Config;
import util.ui.CanvasHolder;
import util.image.ImageLoader;

import java.awt.Color;
import java.lang.Runnable;

import javax.imageio.ImageIO;

import java.io.File;

public class Halftone {
   public static void main(final String[] args) {
      final var config = new Config("sketches/halftone/config.ini");
      final var il = new ImageLoader(config);
      final var img = il.load();
      final var iw = img.getWidth();
      final var ih = img.getHeight();
      final var window = new CanvasHolder("Pendejo");
      final var canvas = new HalftoneCanvas();
      window.add(canvas, 0);
      window.setSize(iw, ih);
      // canvas.setSize(img.getWidth(), img.getHeight());
      // window.pack();
      window.setResizable(false);
      window.setVisible(true);
      canvas.getGraphics().drawImage(img, 0, 0, null);
      Color[][] pixels = new Color[iw][ih];
      for (int x = 0; x < iw; x++) {
         for (int y = 0; y < ih; y++) {
            pixels[x][y] = new Color(img.getRGB(x, y));
         }
      }

      Thread drawThread = new Thread(new Runnable() {
         final int SSSA = 40;
         final int xstep = img.getWidth() / SSSA;
         final int ystep = img.getHeight() / SSSA;
         int x = 0;
         int y = 0;
         private void nap(int n) {
            try {
               Thread.sleep(n);
            } catch (Throwable e) {
               System.err.println("bruh");
            }
         }
         @Override
         public void run() {
            while (true) {
               var ctx = canvas.getGraphics();
               ctx.drawImage(img, 0, 0, null);
               ctx.dispose();
               var img_ctx = img.getGraphics();
               int cx;
               int cy;
               if (y < SSSA) {
                  if (x < SSSA) {
                     x++;
                  } else {
                     x = 0;
                     y++;
                  }
                  cx = x * xstep;
                  cy = y * ystep;
                  img_ctx.setColor(pixels[cx + xstep][cy + ystep]);
                  img_ctx.fillRect(cx, cy, xstep, ystep);
                  img_ctx.setColor(pixels[cx][cy]);
                  img_ctx.fillOval(cx, cy, xstep, ystep);
                  img_ctx.dispose();
                  nap(4);
               } else {
                  try {
                     ImageIO.write(img, "png", new File("out.png"));
                  } catch (Throwable e) {
                     
                  }
                  System.exit(0);
               }
            }
         }
      });
      drawThread.run();
      canvas.setIgnoreRepaint(true);
   }
}
