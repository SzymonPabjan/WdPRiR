package Zad4;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Zad4 {
    public static class QuickSortMutliThreading extends RecursiveTask<Integer> {
        //zrodla
        //ogolnie o algorytmie https://www.baeldung.com/java-quicksort
        //troche sciagania jak cos nie dzialalo: https://www.geeksforgeeks.org/quick-sort-using-multi-threading/
        int start, end;
        int[] arr;

        public QuickSortMutliThreading(int start, int end, int[] arr) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }


        public int partition(int start, int end, int[] arr) {

            int i = start, j = end;
            int pivote = new Random().nextInt(j - i) + i;

            int t = arr[j];
            arr[j] = arr[pivote];
            arr[pivote] = t;
            j--;

            while (i <= j) {
                if (arr[i] <= arr[end]) {
                    i++;
                    continue;
                }
                if (arr[j] >= arr[end]) {
                    j--;
                    continue;
                }
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
                j--;
                i++;
            }


            t = arr[j + 1];
            arr[j + 1] = arr[end];
            arr[end] = t;
            return j + 1;
        }


        @Override
        public Integer compute() {
            if (start >= end) {
                return null;
            }

            int p = partition(start, end, arr);

            QuickSortMutliThreading left = new QuickSortMutliThreading(start, p - 1, arr);
            QuickSortMutliThreading right = new QuickSortMutliThreading(p + 1, end, arr);

            left.fork();
            right.compute();

            left.join();

            return null;
        }
    }
    public static void main(String args[])
    {
        int[] sizeArray = {100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000};
        ArrayList<Double> myAlgTimeArray = new ArrayList<Double>();
        ArrayList<Double> parallelSortTimeArray = new ArrayList<Double>();
        Random random = new Random();

        System.out.println("Czasy dla mojego algotymu");
        for(int i = 0; i < sizeArray.length; i++) {
            double[] tempTimeArray = new double[10];
            for (int k = 0; k < 10; k++) {
                int[] tempArray = new int[sizeArray[i]];
                for (int j = 0; j < tempArray.length; j++) {
                    tempArray[j] = random.ints(1, 1000).findFirst().getAsInt();
                }
                long start = System.nanoTime();

                ForkJoinPool pool = ForkJoinPool.commonPool();
                pool.invoke(new QuickSortMutliThreading(0, sizeArray[i] - 1, tempArray));
                long end = System.nanoTime();
                tempTimeArray[k] = (end - start) / (Math.pow(10, 9));
            }
            System.out.println(Arrays.stream(tempTimeArray).sum());
        }

        System.out.println();
        System.out.println("Czasy dla parallelSort");
        for(int i = 0; i < sizeArray.length; i++){
            double[] tempTimeArray = new double[10];
            for (int k = 0; k < 10; k++) {
                int[] tempArray = new int[sizeArray[i]];
                for (int j = 0; j < tempArray.length; j++) {
                    tempArray[j] = random.ints(1, 1000).findFirst().getAsInt();
                }
                long start = System.nanoTime();
                Arrays.parallelSort(tempArray);
                long end = System.nanoTime();
                tempTimeArray[k] = (end - start) / (Math.pow(10, 9));
            }
            System.out.println(Arrays.stream(tempTimeArray).sum());
        }

    }
}

