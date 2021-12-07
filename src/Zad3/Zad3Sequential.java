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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Zad3Sequential {
    public static void downloadAndSavePNGs() throws IOException {
        String url = "http://www.if.pw.edu.pl/~mrow/dyd/wdprir/";
        Document doc = Jsoup.connect(url).get();
        Elements pngs = doc.select("a[href*=.png]");
        System.out.println("Zaczynam pobieranie obrazów z " + url);
        long start = System.nanoTime();
        for(int i = 0; i<pngs.size(); i++ ){
            //System.out.println(url +"/"+pngs.get(i).attr("href"));
            Connection.Response resultImageResponse = Jsoup.connect(url +"/"+pngs.get(i).attr("href")).maxBodySize(0)
                    .ignoreContentType(true).execute();
            FileOutputStream out = (new FileOutputStream(new java.io.File(pngs.get(i).attr("href"))));
            out.write(resultImageResponse.bodyAsBytes());
            out.close();
        }
        long end = System.nanoTime();
        System.out.println("Czas pobierania sekwencyjnego w sekundach: " + ((end - start)/(Math.pow(10,9))));
        System.out.println("Done downloading");
    }


    public static void main(String[] args) throws IOException {
        downloadAndSavePNGs();
    }
}
