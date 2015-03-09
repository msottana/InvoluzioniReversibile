/*
 * Questo programma genera catene che come rinomine hanno solo involuzioni o rinomine in se stessi
 */
package involuzionireversibile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Matteo and Giuly
 */
public class InvoluzioniReversibile {

    /*
     * This function generates a Triple ret which contains the probability
     * matrix, the pi vector and the rho vector.
     */
    public static Triple getChain(int n) {
        Triple ret = new Triple();
        //generation of the data structures
        ret.chain = new double[n][n];
        ret.pi = new double[n];
        ret.rho = new int[n];
        //s is the set that contains all the vertices of the generated connected component
        ArrayList<Integer> s = new ArrayList();
        //u is the set that contains the remaining nodes of the graph which are not in s
        ArrayList<Integer> u = new ArrayList();
        Random gen = new Random();
        int a, b;
        double sumPi = 0.0;
        //generation of the pi vector
        for (int i = 0; i < n; i++) {
            ret.rho[i] = i;//each vertex is renamed into himself
            if (ret.pi[i] == 0) {
                ret.pi[i] = gen.nextDouble();
            }
            u.add(i);
            sumPi += ret.pi[i];
        }
        // all pi sum to unity
        for (int i = 0; i < n; i++) {
            ret.pi[i] /= sumPi;
        }
        //Add the first node to s
        s.add(u.remove(gen.nextInt(u.size())));
        //edges generation: while u is not empty we connect the extracted vertex to the connected component
        while (!u.isEmpty()) {
            a = s.get(gen.nextInt(s.size()));
            b = u.remove(gen.nextInt(u.size()));
            balanceEquation(a, b, ret.pi, ret.chain);
            s.add(b);
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        int n;//number of vertices
        int l;//number of chains to generate
        long startTime;
        long stopTime;
        long elapsedTime;
        Scanner keyboard = new Scanner(System.in);
        NumberFormat formatter = new DecimalFormat("#0.0000000000000000");
        //This file will be the imput file for VerifiReversibility
        BufferedWriter out = new BufferedWriter(new FileWriter("inputReversible.txt"));
        System.out.print("Insert the number of vertices: ");
        n = keyboard.nextInt(); 
        System.out.print("Insert the number of chains to generate: ");
        l = keyboard.nextInt();
        System.out.println("Generations of " + l + " chains each composed of " + (n + 1) + " vertices.");
        startTime = System.currentTimeMillis();
        //Write the number of chains and vertices in the output file
        out.write(l + "");
        out.newLine();
        out.write(n + "");
        out.newLine();
        for (int k = 0; k < l; k++) {
            Triple chain = getChain(n);//all chains have the same number of vertices
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("Elapsed time: " + elapsedTime + "ms");
            double[][] edges = converter(chain.chain);
            double[] vertices = chain.pi;
            for (int i = 0; i < n; i++) {
                System.out.print(vertices[i] + " ");
            }
            System.out.println("");
            for (int i = 0; i < n; i++) {
                System.out.print(i + "->" + chain.rho[i] + "/");
            }
            System.out.println("");
            System.out.println("");
            for (int i = 0; i < n; i++) {
                System.out.print(formatter.format(edges[i][0]) + " | ");
                out.write(edges[i][0] + "");
                for (int j = 1; j < n; j++) {
                    System.out.print(formatter.format(edges[i][j]) + " | ");
                    out.write("," + edges[i][j]);
                }
                System.out.println("");
                out.newLine();
            }
            System.out.println("---------------------------------------------");
        }
        out.close();
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + "ms");
    }

    private static double balanceEquation(int a, int b, double[] pi, double[][] chain) {
        Random gen = new Random();
        chain[a][b] = gen.nextDouble();
        chain[b][a] = pi[a] * chain[a][b] / pi[b];
        return chain[a][b];
    }

    public static double[][] converter(double chain[][]) {
        int n = chain.length;
        double max = 0.0;
        double loop;
        double sum;
        for (int i = 0; i < n; i++) {
            sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += chain[i][j];
            }
            if (sum >= max) {
                max = sum;
            }
        }
        for (int i = 0; i < n; i++) {
            loop = 1.0;
            for (int j = 0; j < n; j++) {
                chain[i][j] /= max;
                loop -= chain[i][j];
            }
            if (loop > 0.0000000001) {
                chain[i][i] = loop;//loop used to make te outgoing rates of the current vertex sum to 1
            }
        }
        return chain;
    }

}
