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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Zad2PulaWatkow {

    public static class subValidityArrayCallable implements Callable {
        int width;
        int height;
        int xOffset;
        int yOffset;
        double xMin;
        double xMax;
        double yMin;
        double yMax;
        int iterations;
        int size;
        Boolean[][] validityArray;

        public subValidityArrayCallable(int widthOut, int heightOut, int sizeOut, int xOffsetOut, int yOffsetOut, double xMinOut, double xMaxOut,
                                        double yMinOut, double yMaxOut, int iterationsOut, Boolean[][] validityArrayOut){
            this.width = widthOut;
            this.height = heightOut;
            this.size = sizeOut;
            this.xOffset = xOffsetOut;
            this.yOffset = yOffsetOut;
            this.xMin = xMinOut;
            this.xMax = xMaxOut;
            this.yMin = yMinOut;
            this.yMax = yMaxOut;
            this.iterations = iterationsOut;
            this.validityArray = validityArrayOut;
        }
        public Boolean call(){

            for(int x = this.xOffset; x < this.width; ++x){
                double x_temp = xMin + (x* ( (xMax - xMin) / (size - 1) ));
                for (int y = this.yOffset; y < this.height; ++y){
                    double y_temp = yMin + (y* ( (yMax - yMin) / (size - 1) ));
                    if(checkPoint(iterations, x_temp, y_temp)){
                        validityArray[x][y] = true;
                    }
                    else {
                        validityArray[x][y] = false;
                    }
                }
            }
            return true;
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

    public static Boolean[][] validityArray(int width, int height, int subSquareSize, double xMin, double xMax, double yMin, double yMax, int iterations, ArrayList<Long> averageTimeList) throws InterruptedException {
        Boolean[][] validityArray = new Boolean[width][height];
        ArrayList<Callable<Boolean>> callableList = new ArrayList<>();
        int numberOfChunks = width/subSquareSize;
        int[] offsetArray = new int[width/subSquareSize];
        int basicOffset = subSquareSize;
        for(int i = 0; i <numberOfChunks; ++i){
            offsetArray[i] = basicOffset*i;
        }

        for (int i = 0; i < numberOfChunks; i++){
            for (int j = 0; j < numberOfChunks; j++){
                callableList.add(new subValidityArrayCallable((basicOffset+offsetArray[i]), (basicOffset+offsetArray[j]), height, offsetArray[i], offsetArray[j], xMin, xMax, yMin, yMax, iterations, validityArray));
            }
        }
        ExecutorService executor = Executors.newFixedThreadPool(8);
        long start = System.nanoTime();
        List<Future<Boolean>> futures = executor.invokeAll(callableList);
        long end = System.nanoTime();
        averageTimeList.add(end - start);
        executor.shutdown();


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

    public static void getAverageGenerationTime(int iterations, List<Integer> subSquareSizes, int size) throws InterruptedException {
        ArrayList<Long> averageTimeList = new ArrayList<Long>();
        for(int i = 0; i < subSquareSizes.size(); i ++){
            long timeSum = 0;
            ArrayList<Long> temporaryAverageTimeList = new ArrayList<Long>();
            for(int j = 0; j < iterations; j++){

                Boolean[][] validityArray = validityArray(size, size, subSquareSizes.get(i),-2.1, 0.6, -1.2, 1.2, 200, temporaryAverageTimeList);
                MandelbrotImg(size, size, validityArray);

            }
            long sum = 0;
            for(Long d : temporaryAverageTimeList)
                sum += d;
            averageTimeList.add(sum/iterations);
        }
        for(int i = 0; i < subSquareSizes.size(); i ++) {
            System.out.println(subSquareSizes.get(i).toString() + "  " + averageTimeList.get(i).toString());
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        List<Integer> sizes = Arrays.asList(1024, 2048, 4096, 8192);
        List<Integer> subSquareSizes = Arrays.asList(4, 8, 16, 32, 64, 128);
        for(int i = 0; i < sizes.size(); i++){
            System.out.println("Licze dla "+sizes.get(i));
            getAverageGenerationTime(10, subSquareSizes,sizes.get(i) );
        }

       System.out.println("Done");
    }
}
