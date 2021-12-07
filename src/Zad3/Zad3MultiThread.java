package Zad3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        System.out.println("Czas pobierania wielowątkowego w sekundach: " + ((end - start)/(Math.pow(10,9))));
        System.out.println("Done downloading");
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        downloadAndSavePNGs();
    }
}
