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
     Questa funzione serve per creare la tripla ret contenente la matrice degli atchi, le pi e le ro.
     */
    public static Tripla getChain(int n) {
        Tripla ret = new Tripla();
        //generiamo n nodi, e poi 1 in piú per sistemare le rate uscenti
        ret.chain = new double[n][n];
        ret.pi = new double[n];
        ret.ro = new int[n];
        // s é l'insieme che contiene tutti i nodi che formano tra loro una componente connessa
        ArrayList<Integer> s = new ArrayList();
        //u é l'insieme dei nodi che non si trovano nella componenete connessa
        ArrayList<Integer> u = new ArrayList();
        //ArrayList<Integer> appoggio = new ArrayList();
        Random gen = new Random();
        int a, b;
        double sommaPi = 0.0;
        //rinomino il nodo aggiunto inse stesso
        //ret.ro[n] = n;
        //generazione pi greco per tutti i nodi e li inserisco in u (tranne n+1)
        for (int i = 0; i < n; i++) {
            ret.ro[i] = i;//aggiunta per renderla reversibile
            if (ret.pi[i] == 0) {
                ret.pi[i] = gen.nextDouble();
            }
            ret.pi[ret.ro[i]] = ret.pi[i];
            u.add(i);
            sommaPi += ret.pi[i];
        }
        //genero anche la pi del nodo fittizio
        /*ret.pi[n] = gen.nextDouble();
         sommaPi += ret.pi[n];*/
        /*
         * La somma dei pi deve essere uguale a uno
         */
        for (int i = 0; i < ret.pi.length; i++) {
            ret.pi[i] /= sommaPi;
        }
        sommaPi = 0;
        for (int i = 0; i < ret.pi.length; i++) {
            sommaPi += ret.pi[i];
        }
        //aggiungo un nodo all'insieme iniziale
        s.add(u.remove(gen.nextInt(u.size())));
        //generazione degli archi: finché ci sono nodi in u prendo un nodo da s, ne rimuovo uno da u e faccio i controlli per vedere se
        //fa giá parte o no della cc
        while (!u.isEmpty()) {
            a = s.get(gen.nextInt(s.size()));
            b = u.remove(gen.nextInt(u.size()));
            //se non c'é l'arco da a fino a b lo creo. Non devo controllare che non ci sia giá un altro arco da un nodo di s che va verso
            //u perché nel momento in cui lo metto, per definizione del mio programma, connetto tutto il nodo nella componente connessa
            if (ret.chain[a][b] == 0) {
                rinomine(a, b, ret.pi, ret.chain, ret.ro);
            }
            rinomine(b, a, ret.pi, ret.chain, ret.ro);
            //poi posso aggiungere il nodo su cui ho lavorato all'insieme s
            s.add(b);
        }
        //alla fine, dopo aver creato tutti gli archi, sistemo le rate uscenti usando il nodo aggiuntivo
        //sistemaRate(ret);
        return ret;
    }

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        int n;//numero di nodi
        int l;//numero di catene da generare
        long startTime;
        long stopTime;
        long elapsedTime;
        Scanner tastiera = new Scanner(System.in);
        NumberFormat formatter = new DecimalFormat("#0.0000000000000000");
        BufferedWriter out = new BufferedWriter(new FileWriter("cateneReversibili.txt"));
        System.out.print("Inserire il numero di nodi: ");
        n = tastiera.nextInt();
        System.out.print("Inserire il numero di catene da generare: ");
        l = tastiera.nextInt();
        System.out.println("Generazione di " + l + " catena/e ogniuna composta da " + (n + 1) + " nodi.");
        startTime = System.currentTimeMillis();
        //scrive sul file il numero di catene e il numero di nodi per le catene generate
        out.write(l + "");
        out.newLine();
        out.write(n + "");
        out.newLine();
        //per adesso supporta soltanto la generazione di catene con lo stesso numero di nodi
        for (int k = 0; k < l; k++) {
            Tripla chain = getChain(n);//tutte le catene hanno lo stesso numero di nodi
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("Elapsed time: " + elapsedTime + "ms");
            double archi[][] = chain.chain;
            double nodi[] = chain.pi;
            for (int i = 0; i < n; i++) {
                System.out.print(nodi[i] + " ");
            }
            System.out.println("");
            for (int i = 0; i < n; i++) {
                System.out.print(i + "->" + chain.ro[i] + "/");
            }
            System.out.println("");
            System.out.println("");
            for (int i = 0; i < n; i++) {
                System.out.print(formatter.format(archi[i][0]) + " | ");
                out.write(archi[i][0] + "");
                for (int j = 1; j < n; j++) {
                    System.out.print(formatter.format(archi[i][j]) + " | ");
                    out.write("," + archi[i][j]);
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

    private static double rinomine(int a, int b, double[] pi, double[][] chain, int[] ro) {
        Random gen = new Random();
        int aR = ro[b];
        int bR = ro[a];
        //creo arco a-b
        chain[a][b] = gen.nextDouble();
        //creo arco dalla rinomina di b alla rinomina di a tramite la formula
        chain[aR][bR] = pi[a] * chain[a][b] / pi[b];
        return chain[a][b];
    }

    //calcola la somma delle rate uscenti
    private static double trovaSommaUscenti(double[][] chain, Integer x) {
        double ret = 0;
        for (int j = 0; j < chain.length; j++) {
            ret += chain[x][j];
        }
        return ret;
    }
}
