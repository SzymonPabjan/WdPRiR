package Zad3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Zad3Sequential {

    public static double[][] returnKernel(double sigma, int radius) {
        double[][] kernel = new double[2*radius + 1][2*radius + 1];
        double sum = 0;
        for(int x = 0; x<2*radius + 1; x++) {
            for(int y = 0; y<2*radius + 1; y++) {
                double temporaryValue = Math.exp(- (Math.pow(x-radius, 2) + Math.pow(y-radius, 2)) / (2 * sigma * sigma));
                kernel[x][y] = temporaryValue;
                sum += temporaryValue;
            }
        }
        for(int x = 0; x<2*radius + 1; x++) {
            for (int y = 0; y < 2 * radius + 1; y++) {
                kernel[x][y] /= sum;
            }
        }

        return kernel;
    }

    private static BufferedImage returnFilteredImage(BufferedImage imageToProcess, double sigma, int kernelRadius) {
        int imageToProcessWidth = imageToProcess.getWidth();
        int imageToProcessHeight = imageToProcess.getHeight();
        BufferedImage processedImage = new BufferedImage(imageToProcessWidth, imageToProcessHeight, imageToProcess.getType());
        double[][] kernel = returnKernel(sigma, kernelRadius);
        float[] tempColors = {0, 0, 0};

        for(int x = 0; x<imageToProcessWidth; x++) {
            for(int y = 0; y<imageToProcessHeight; y++) {
                if(x-kernelRadius < 0 || x+kernelRadius >= imageToProcessWidth ||
                        y-kernelRadius < 0 || y+kernelRadius >= imageToProcessHeight)
                    processedImage.setRGB(x, y, imageToProcess.getRGB(x, y));
                else {
                    float[] newColors = {0, 0, 0};
                    for(int xx = x-kernelRadius; xx<=x+kernelRadius; xx++) {
                        for(int yy = y-kernelRadius; yy<=y+kernelRadius; yy++) {
                            for(int c = 0; c<3; ++c) {
                                new Color(imageToProcess.getRGB(xx, yy)).getRGBColorComponents(tempColors);
                                newColors[c] += tempColors[c] * kernel[xx-x+kernelRadius][yy-y+kernelRadius];
                            }
                        }
                    }
                    processedImage.setRGB(x, y, new Color(newColors[0], newColors[1], newColors[2]).getRGB());
                }
            }
        }

        return processedImage;
    }


    public static void downloadAndSavePNGs() throws IOException {
        String url = "http://www.if.pw.edu.pl/~mrow/dyd/wdprir/";
        Document doc = Jsoup.connect(url).get();
        Elements pngs = doc.select("a[href*=.png]");
        System.out.println("Zaczynam pobieranie obrazów z " + url);
        long start = System.nanoTime();
        for(int i = 0; i<pngs.size(); i++ ){
            URL pngURL = new URL(url +"/"+pngs.get(i).attr("href"));
            ImageIO.write(ImageIO.read(pngURL), "png", new File(String.valueOf(pngs.get(i).attr("href"))));
        }
        long end = System.nanoTime();
        System.out.println("Czas pobierania sekwencyjnego w sekundach: " + ((end - start)/(Math.pow(10,9))));
        System.out.println("Done downloading");
    }

    public static void downloadAndFilterAndSavePNGs(double sigma, int kernelRadius) throws IOException {
        String url = "http://www.if.pw.edu.pl/~mrow/dyd/wdprir/";
        Document doc = Jsoup.connect(url).get();
        Elements pngs = doc.select("a[href*=.png]");
        System.out.println("Zaczynam pobieranie i filtrowanie obrazów z " + url);
        long start = System.nanoTime();
        for(int i = 0; i<pngs.size(); i++ ){
            URL pngURL = new URL(url +"/"+pngs.get(i).attr("href"));
            BufferedImage image = ImageIO.read(pngURL);
            image = returnFilteredImage(image, sigma, kernelRadius);
            ImageIO.write(image, "png", new File(pngs.get(i).attr("href")));
        }
        long end = System.nanoTime();
        System.out.println("Czas pobierania sekwencyjnego i filtrowania w sekundach: " + ((end - start)/(Math.pow(10,9))));
        System.out.println("Done downloading");
    }

    //funkcja do testowania filtra
    public static void downloadAndFilterAndSaveOnePNG(double sigma, int kernelRadius) throws IOException {
        String url = "http://www.if.pw.edu.pl/~mrow/dyd/wdprir/";
        Document doc = Jsoup.connect(url).get();
        Elements pngs = doc.select("a[href*=.png]");
        System.out.println("Zaczynam pobieranie i filtrowanie obrazów z " + url);
        long start = System.nanoTime();
        URL pngURL = new URL(url +"/"+pngs.get(1).attr("href"));
        BufferedImage image = ImageIO.read(pngURL);
        image = returnFilteredImage(image, sigma, kernelRadius);
        ImageIO.write(image, "png", new File(pngs.get(1).attr("href")));

        long end = System.nanoTime();
        System.out.println("Czas pobierania sekwencyjnego w sekundach: " + ((end - start)/(Math.pow(10,9))));
        System.out.println("Done downloading");
    }


    public static void main(String[] args) throws IOException {
        //downloadAndSavePNGs();
        //downloadAndFilterAndSavePNGs(1,3);
        downloadAndFilterAndSaveOnePNG(7,2);
    }
}
