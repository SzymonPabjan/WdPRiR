package Zad2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zad2Watki {

    public static class subValidityArrayRunnable implements Runnable{
        int width;
        int height;
        int offset;
        double xMin;
        double xMax;
        double yMin;
        double yMax;
        int iterations;
        Boolean[][] validityArray;

        public subValidityArrayRunnable(int widthOut, int heightOut, int offsetOut, double xMinOut, double xMaxOut,
                                        double yMinOut, double yMaxOut, int iterationsOut, Boolean[][] validityArrayOut){
            this.width = widthOut;
            this.height = heightOut;
            this.offset = offsetOut;
            this.xMin = xMinOut;
            this.xMax = xMaxOut;
            this.yMin = yMinOut;
            this.yMax = yMaxOut;
            this.iterations = iterationsOut;
            this.validityArray = validityArrayOut;
        }
        public void run(){

            for(int x = this.offset; x < this.width; ++x){
                double x_temp = xMin + (x* ( (xMax - xMin) / (height - 1) ));
                for (int y = 0; y < height; ++y){
                    double y_temp = yMin + (y* ( (yMax - yMin) / (height - 1) ));
                    if(checkPoint(iterations, x_temp, y_temp)){
                        validityArray[x][y] = true;
                    }
                    else {
                        validityArray[x][y] = false;
                    }
                }
            }
        }
    }


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

    public static Boolean[][] validityArray(int width, int height, double xMin, double xMax, double yMin, double yMax, int iterations){
        Boolean[][] validityArray = new Boolean[width][height];
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        int[] offsetArray = new int[numberOfThreads];
        int basicOffset = width/numberOfThreads;
        for(int i = 0; i <numberOfThreads; ++i){
            offsetArray[i] = basicOffset*i;
        }

        Runnable runnable0 = new subValidityArrayRunnable((basicOffset+offsetArray[0]), height, offsetArray[0], xMin, xMax, yMin, yMax, iterations, validityArray);
        Runnable runnable1 = new subValidityArrayRunnable((basicOffset+offsetArray[1]), height, offsetArray[1], xMin, xMax, yMin, yMax, iterations, validityArray);
        Runnable runnable2 = new subValidityArrayRunnable((basicOffset+offsetArray[2]), height, offsetArray[2], xMin, xMax, yMin, yMax, iterations, validityArray);
        Runnable runnable3 = new subValidityArrayRunnable((basicOffset+offsetArray[3]), height, offsetArray[3], xMin, xMax, yMin, yMax, iterations, validityArray);
        Runnable runnable4 = new subValidityArrayRunnable((basicOffset+offsetArray[4]), height, offsetArray[4], xMin, xMax, yMin, yMax, iterations, validityArray);
        Runnable runnable5 = new subValidityArrayRunnable((basicOffset+offsetArray[5]), height, offsetArray[5], xMin, xMax, yMin, yMax, iterations, validityArray);
        Runnable runnable6 = new subValidityArrayRunnable((basicOffset+offsetArray[6]), height, offsetArray[6], xMin, xMax, yMin, yMax, iterations, validityArray);
        Runnable runnable7 = new subValidityArrayRunnable((basicOffset+offsetArray[7]), height, offsetArray[7], xMin, xMax, yMin, yMax, iterations, validityArray);

        Thread thread0 = new Thread(runnable0);
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        Thread thread3 = new Thread(runnable3);
        Thread thread4 = new Thread(runnable4);
        Thread thread5 = new Thread(runnable5);
        Thread thread6 = new Thread(runnable6);
        Thread thread7 = new Thread(runnable7);

        thread0.start();
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        try {
            thread0.join();
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
            thread7.join();
        } catch (InterruptedException e) {
        }

        return  validityArray;
    }


    public static BufferedImage MandelbrotImg(int width, int height, Boolean [][] validityArray){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                if(validityArray[x][y] == true){
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
            System.out.println(i);
            long timeSum = 0;
            for(int j = 0; j < iterations; j++){
                long start = System.nanoTime();
                Boolean[][] validityArray = validityArray(sizes.get(i), sizes.get(i), -2.1, 0.6, -1.2, 1.2, 200);
                MandelbrotImg(sizes.get(i), sizes.get(i), validityArray);
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
        List<Integer> sizes= Arrays.asList(32, 64, 128, 256, 512, 1024, 2048, 4096, 8192);
        getAverageGenerationTime(10, sizes);

        System.out.println("Done");
    }
}
