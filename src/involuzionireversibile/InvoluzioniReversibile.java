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
        //generation of the data structuresgeneriamo n nodi, e poi 1 in piú per sistemare le rate uscenti
        ret.chain = new double[n][n];
        ret.pi = new double[n];
        ret.rho = new int[n];
        // s is the set that contains all the vertices of the generated connected component
        ArrayList<Integer> s = new ArrayList();
        // u is the set that contains the remaining nodes of the graph
        ArrayList<Integer> u = new ArrayList();
        Random gen = new Random();
        int a, b;
        double sommaPi = 0.0;
        //generation of the pi vector
        for (int i = 0; i < n; i++) {
            ret.rho[i] = i;//each vertex is renamed into himself
            if (ret.pi[i] == 0) {
                ret.pi[i] = gen.nextDouble();
            }
            ret.pi[ret.rho[i]] = ret.pi[i];
            u.add(i);
            sommaPi += ret.pi[i];
        }
        // all pi sum to unity
        for (int i = 0; i < ret.pi.length; i++) {
            ret.pi[i] /= sommaPi;
        }
        //add the first node to s
        s.add(u.remove(gen.nextInt(u.size())));
        //edges generation: while u is not empty we connect the extracted edge to the connected component
        while (!u.isEmpty()) {
            a = s.get(gen.nextInt(s.size()));
            b = u.remove(gen.nextInt(u.size()));
            //Non devo controllare che non ci sia giá un altro arco da un nodo di s che va verso
            //u perché nel momento in cui lo metto, per definizione del mio programma, connetto tutto il nodo nella componente connessa
            //if (ret.chain[a][b] == 0) {
            balanceEquation(a, b, ret.pi, ret.chain/*, ret.ro*/);
            s.add(b);
        }
        //alla fine, dopo aver creato tutti gli archi, sistemo le rate uscenti usando il nodo aggiuntivo
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
        BufferedWriter out = new BufferedWriter(new FileWriter("inputReversible.txt"));
        System.out.print("Insert the number of verteces: ");
        n = keyboard.nextInt();
        System.out.print("Insert the number of chains to generate: ");
        l = keyboard.nextInt();
        System.out.println("Generations of " + l + " chains each composed of " + (n + 1) + " vertices.");
        startTime = System.currentTimeMillis();
        //scrive sul file il numero di catene e il numero di nodi per le catene generate
        out.write(l + "");
        out.newLine();
        out.write(n + "");
        out.newLine();
        //per adesso supporta soltanto la generazione di catene con lo stesso numero di nodi
        for (int k = 0; k < l; k++) {
            Triple chain = getChain(n);//tutte le catene hanno lo stesso numero di nodi
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
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
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
            if (loop > 0) {
                chain[i][i] = loop;//cappio con peso uguale a ciò che manca per avere la somma dei nodi uscenti pari a uno
            }
        }
        return chain;
    }

}
