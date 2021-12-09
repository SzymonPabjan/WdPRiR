package Zad3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Zad3MultiThread {

    public static class DownloadPNG implements Callable {
        String url;
        String fileName;

        public DownloadPNG(String urlOut, String fileNameOut){
            this.url = urlOut;
            this.fileName = fileNameOut;
        }
        @Override
        public Boolean call() throws Exception {
            Connection.Response resultImageResponse = Jsoup.connect(this.url).maxBodySize(0)
                    .ignoreContentType(true).execute();
            FileOutputStream out = (new FileOutputStream(new java.io.File(this.fileName)));
            out.write(resultImageResponse.bodyAsBytes());
            out.close();
            return true;
        }
    }

    public static class DownloadAndFilterPNG implements Callable {
        URL url;
        String fileName;
        double sigma;
        int kernelRadius;

        public DownloadAndFilterPNG(String urlOut, String fileNameOut, double sigmaOut, int kernelRadiusOut) throws MalformedURLException {
            this.url = new URL(urlOut);
            this.fileName = fileNameOut;
            this.sigma = sigmaOut;
            this.kernelRadius = kernelRadiusOut;
        }
        @Override
        public Boolean call() throws Exception {
            BufferedImage image = ImageIO.read(url);
            image = returnFilteredImage(image, sigma, kernelRadius);
            ImageIO.write(image, "png", new File(fileName));
            return true;
        }
    }
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
                            for(int c = 0; c<3; c++) {
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

    public static void downloadAndSavePNGs() throws IOException, InterruptedException {
        String url = "http://www.if.pw.edu.pl/~mrow/dyd/wdprir/";
        Document doc = Jsoup.connect(url).get();
        Elements pngs = doc.select("a[href*=.png]");
        ArrayList<Callable<Boolean>> callableList = new ArrayList<>();
        for(int i = 0; i<pngs.size(); i++ ){
            callableList.add(new DownloadPNG((url +"/"+pngs.get(i).attr("href")),pngs.get(i).attr("href")));
        }
        ExecutorService executor = Executors.newFixedThreadPool(8);
        System.out.println("Zaczynam wielowątkowe pobieranie obrazów z " + url);
        long start = System.nanoTime();
        List<Future<Boolean>> futures = executor.invokeAll(callableList);
        long end = System.nanoTime();
        executor.shutdown();
        System.out.println("Czas pobierania wielowątkowego w sekundach: " + ((end - start)/(Math.pow(10,9))));
        System.out.println("Done downloading");
    }

    public static void downloadAndFilterAndSavePNGs(double sigma, int kernelRadius) throws IOException, InterruptedException {
        String url = "http://www.if.pw.edu.pl/~mrow/dyd/wdprir/";
        Document doc = Jsoup.connect(url).get();
        Elements pngs = doc.select("a[href*=.png]");
        ArrayList<Callable<Boolean>> callableList = new ArrayList<>();
        for(int i = 0; i<pngs.size(); i++ ){
            callableList.add(new DownloadAndFilterPNG((url +"/"+pngs.get(i).attr("href")),pngs.get(i).attr("href"), sigma, kernelRadius));
        }
        ExecutorService executor = Executors.newFixedThreadPool(8);
        System.out.println("Zaczynam wielowątkowe pobieranie i filtrowanie obrazów z " + url);
        long start = System.nanoTime();
        List<Future<Boolean>> futures = executor.invokeAll(callableList);
        long end = System.nanoTime();
        executor.shutdown();
        System.out.println("Czas pobierania wielowątkowego i filtrowania w sekundach: " + ((end - start)/(Math.pow(10,9))));
        System.out.println("Done downloading");
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        //downloadAndSavePNGs();
        //downloadAndFilterAndSavePNGs(1,1);
        int[] size_arr = {1,2,3,4,5};
        for(int i = 0; i < 5; i++){
            System.out.println();
            System.out.println("Licze dla kernel = " + size_arr[i] + " :");
            downloadAndFilterAndSavePNGs(7,size_arr[i]);
            System.out.println();
        }
    }
}
