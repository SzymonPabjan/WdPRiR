package Zad2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Zad2Watki {


    public static boolean checkPoint(int iterations, double inX, double inY) {
        double x = 0;
        double y = 0;
        for (int i = 0; i < iterations; ++i){
            double oldX = x;
            double oldY = y;
            x = Math.pow(oldX,2) - Math.pow(oldY,2) +inX;
            y = 2*oldX*oldY +inY;

            if(Math.sqrt(x*x + y*y) >=2){
                return false;
            }
        }
        return true;
    }

    public static BufferedImage MandelbrotImg(int width, int height, double xMin, double xMax, double yMin, double yMax, int iterations){
        ArrayList<Double> widthArray = new ArrayList<Double>();
        ArrayList<Double> heightArray = new ArrayList<Double>();


        for(int x = 0; x < width; ++x){
            widthArray.add(xMin + (x* ( (xMax - xMin) / (width - 1) )));
        }

        for(int y = 0; y < height; ++y){
            heightArray.add(yMin + (y* ( (yMax - yMin) / (height - 1) )));
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                if(checkPoint(iterations, widthArray.get(x), heightArray.get(y))){
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
                else {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }

        }

        return image;
    }

    public static void saveImg(BufferedImage image) throws IOException {
        File outputFile = new File("Mendelbrot_" + new SimpleDateFormat("dd_MM_yyyy_HH_mm").format(new java.util.Date()) + ".png");
        ImageIO.write(image, "png", outputFile);
    }

    public static void getAverageGenerationTime(int iterations, List<Integer> sizes){
        ArrayList<Long> averageTimeList = new ArrayList<Long>();
        for(int i = 0; i < sizes.size(); i ++){
            long timeSum = 0;
            for(int j = 0; j < iterations; j++){
                long start = System.nanoTime();
                MandelbrotImg(sizes.get(i), sizes.get(i), -2.1, 0.6, -1.2, 1.2, 200);
                long end = System.nanoTime();
                timeSum += end - start;
            }
            averageTimeList.add((timeSum/iterations));
        }
        for(int i = 0; i < sizes.size(); i ++) {
            System.out.println(sizes.get(i).toString() + "  " + averageTimeList.get(i).toString());
        }

    }

    public static void main(String[] args) throws IOException {
        //List<Integer> sizes= Arrays.asList(32, 64, 128, 256, 512, 1024, 2048, 4096, 8192);
        //getAverageGenerationTime(10, sizes);
        //saveImg(MandelbrotImg(512, 512, -2.1, 0.6, -1.2, 1.2, 200));
        //saveImg(MandelbrotImg(4096, 4096, -0, 0.5, -0.6, 0.6, 200));
        System.out.println("Done");
    }
}
