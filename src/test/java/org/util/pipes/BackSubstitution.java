package org.util.pipes;

import org.util.concurrent.pipes.*;
import org.util.concurrent.pipes.Process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * @author ahmad
 */
public class BackSubstitution {

    private static int n;
    private static long a[][];
    private static long b[];
    private static double x[];

    public static void main(String[] args) {
        // Initialize arrays
        init();

        // Solves equation #i to compute x[i]
        Process equationSolver = c -> {
            int i = c.pipe().index();

            if (i == 0) {
                c.dataBus().set("start", System.nanoTime());
            }

            int sum = 0;
            double xValue;

            for (int j = 0; j < i; j++) {
                xValue = (double) c.channel().readBlocking();
                if (i < n - 1) {
                    c.writeToChannel(i + 1, xValue);
                }
                sum += a[i][j] * xValue;
            }
            x[i] = (double) (b[i] - sum) / a[i][i];

            if (i < n - 1) {
                c.writeToChannel(i + 1, x[i]);
            } else {
                c.dataBus().set("end", System.nanoTime());
            }
        };

        // Build pipeline
        Pipeline.Builder pb = Pipeline.builder();
        IntStream.range(0, n).forEach(i -> pb.nextAsync(equationSolver));
        Pipeline pipeline = pb.build();

        System.out.print("Computing \'x\' values... ");

        // Start calculation...
        PipelineEngine engine = new PipelineEngine();
        engine.start(pipeline, true).join();
        engine.shutdown();

        System.out.println("done.\n");

        printResults();

        DataBus dataBus = PipelineContext.shared(pipeline).dataBus();
        long start = (long) dataBus.get("start");
        long end = (long) dataBus.get("end");

        System.out.printf("\nCalculation time = %.3f ms", (end - start) / 1000000.0);
    }

    private static void init() {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter number of equations : ");
            n = scanner.nextInt();

            if (n < 1) {
                throw new AssertionError("You must enter a positive integer.");
            }

            a = new long[n][];
            b = new long[n];
            x = new double[n];

            System.out.println("----------------------------------------");

            for (int i = 0; i < n; i++) {
                System.out.println("Initializing equation #" + (i + 1) + " =>");

                a[i] = new long[i + 1];

                for (int j = 0; j <= i; j++) {
                    System.out.print("a[" + (j + 1) + "] = ");
                    a[i][j] = scanner.nextInt();
                }

                System.out.print("b = ");
                b[i] = scanner.nextInt();

                System.out.println("\n" + equationToString(i) + "\n----------------------------------------");
            }

        }
    }

    private static String equationToString(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = a[n].length; i < len; i++) {
            sb.append('(').append(a[n][i]).append(')').append(" x[").append(i + 1).append("] + ");
        }
        sb.setCharAt(sb.length() - 2, '=');
        sb.append(b[n]);
        return sb.toString();
    }

    private static void printResults() {
        List<String> xValues = new ArrayList<>();

        int maxWidth = 0;
        for (double xValue : x) {
            String xString;
            if (xValue == Double.POSITIVE_INFINITY) {
                xString = "Infinity";
            } else if (xValue == Double.NEGATIVE_INFINITY) {
                xString = "-Infinity";
            } else if (xValue != xValue) {
                xString = "NaN";
            } else {
                xString = BigDecimal.valueOf(xValue).stripTrailingZeros().toPlainString();
            }
            xValues.add(xString);
            maxWidth = Math.max(maxWidth, xString.length());
        }

        int cellWidth = Math.max(maxWidth + 2, 10);

        System.out.println(String.format("+%-" + cellWidth + "s+%-" + cellWidth + "s+", "", "").replace(' ', '-'));
        System.out.printf("| %-" + (cellWidth - 1) + "s| %-" + (cellWidth - 1) + "s|\n", "x", "value");
        System.out.println(String.format("+%-" + cellWidth + "s+%-" + cellWidth + "s+", "", "").replace(' ', '-'));

        for (int i = 0; i < n; i++) {
            System.out.printf("| %-" + (cellWidth - 1) + "d| %-" + (cellWidth - 1) + "s|\n", i + 1, xValues.get(i));
            System.out.println(String.format("+%-" + cellWidth + "s+%-" + cellWidth + "s+", "", "").replace(' ', '-'));
        }
    }

}
