/*
 * Questo programma genera catene che come rinomine hanno solo involuzioni o rinomine in se stessi
 */
package involuzionireversibile;

import java.util.ArrayList;
import java.util.Random;

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
        ret.chain = new double[n + 1][n + 1];
        ret.pi = new double[n + 1];
        ret.ro = new int[n + 1];
        // s é l'insieme che contiene tutti i nodi che formano tra loro una componente connessa
        ArrayList<Integer> s = new ArrayList();
        //u é l'insieme dei nodi che non si trovano nella componenete connessa
        ArrayList<Integer> u = new ArrayList();
        ArrayList<Integer> appoggio = new ArrayList();
        Random gen = new Random();
        int a, b;

        for (int i = 0; i < n; i++) {
            appoggio.add(i);
        }
        //qui creo le ro come involuzioni. O da 2 o da 1. Per tutti i nodi n del grafo ma non per l'n+1 che aggiungo per sistemare
        //le rate perché quello deve essere per forza rinominato in se stesso
        while (!appoggio.isEmpty()) {
            int temp = appoggio.get(gen.nextInt(appoggio.size()));
            ret.ro[temp] = appoggio.get(gen.nextInt(appoggio.size()));
            ret.ro[ret.ro[temp]] = temp;
            appoggio.remove((Integer) ret.ro[temp]);
            appoggio.remove((Integer) temp);
        }
        //rinomino il nodo aggiunto inse stesso
        ret.ro[n] = n;
        //generazione pi greco per tutti i nodi e li inserisco in u (tranne n+1)
        for (int i = 0; i < n; i++) {
            if (ret.pi[i] == 0) {
                ret.pi[i] = gen.nextDouble();
            }
            ret.pi[ret.ro[i]] = ret.pi[i];
            u.add(i);
        }
        //genero anche la pi del nodo fittizio
        ret.pi[n] = gen.nextDouble();
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
        sistemaRate(ret);
        return ret;
    }

    public static void main(String[] args) {
        // TODO code application logic here
        int n = 4;
        long startTime = System.currentTimeMillis();
        long stopTime;
        long elapsedTime;
        Tripla tests[] = new Tripla[1];//serve per generare più catene, non viene utilizzato per adesso
        for (int k = 0; k < 1; k++) {
            Tripla chain = getChain(n);
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("Elapsed time: " + elapsedTime + "ms");
            double archi[][] = chain.chain;
            double nodi[] = chain.pi;
            tests[k] = chain;
            for (int i = 0; i < n + 1; i++) {
                System.out.print(nodi[i] + " ");
            }
            System.out.println("");
            for (int i = 0; i < n + 1; i++) {
                System.out.print(i + "->" + chain.ro[i] + "/");
            }
            System.out.println("");
            System.out.println("");
            for (int i = 0; i < n + 1; i++) {
                for (int j = 0; j < n + 1; j++) {
                    System.out.print(archi[i][j] + " | ");
                }
                System.out.println("");
            }
            System.out.println("---------------------------------------------");
        }
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

    //per ogni nodo identifica il suo gruppo, il massimo di quel gruppo, e sistema la rate aggiungendo un arco verso il nodo
    //aggiuntivo e crendo poi l'arco in ingresso verso la rinomina (che é in ingresso quindi non va a modificare la rate)
    private static void sistemaRate(Tripla ret) {
        Random gen = new Random();
        ArrayList<Integer> nodi = new ArrayList<>();
        for (int i = 0; i < ret.chain.length; i++) {
            nodi.add(i);
        }
        while (!nodi.isEmpty()) {
            int nodo = nodi.remove((int) 0);
            if (ret.ro[nodo] != nodo) {
                nodi.remove((Integer)ret.ro[nodo]);
                double somma_1 = trovaSommaUscenti(ret.chain, nodo);
                double somma_2 = trovaSommaUscenti(ret.chain, ret.ro[nodo]);
                int x = nodo;
                int y = ret.ro[nodo];
                double valArco;
                double valAggiunto = gen.nextDouble();
                System.out.println("valore uscite di x: " + somma_1);
                System.out.println("valore uscite di y: " + somma_2);
                if (somma_1 > somma_2) {   
                    System.out.println("x é il piú grande");
                    System.out.println("aggiungo il valore: " + valAggiunto);
                    valArco = somma_1 - somma_2 + valAggiunto;
                    System.out.println("ottengo un valore" + valArco + "che faccio partire da y");
                    ret.chain[y][ret.chain.length - 1] = valArco;
                    ret.chain[ret.chain.length - 1][x] = ret.pi[y] * valArco / ret.pi[ret.chain.length - 1];
                    ret.chain[x][ret.chain.length - 1] = valAggiunto;
                    System.out.println("metto a partire da x solo" + valAggiunto);
                    ret.chain[ret.chain.length - 1][y] = ret.pi[x] * valAggiunto / ret.pi[ret.chain.length - 1];
                    
                    
                } else {
                    System.out.println("y é il piú grande");
                    System.out.println("aggiungo il valore: " + valAggiunto);
                    valArco = somma_2 - somma_1 + valAggiunto;
                    System.out.println("ottengo un valore" + valArco + "che faccio partire da x");
                    ret.chain[x][ret.chain.length - 1] = valArco;
                    ret.chain[ret.chain.length - 1][y] = ret.pi[x] * valArco / ret.pi[ret.chain.length - 1];
                    ret.chain[y][ret.chain.length - 1] = valAggiunto;
                    System.out.println("metto a partire da y solo" + valAggiunto);
                    ret.chain[ret.chain.length - 1][x] = ret.pi[y] * valAggiunto / ret.pi[ret.chain.length - 1];
                }
            }
        }
        int flag=0;
        for(int i = 0; (i<ret.chain.length && flag==0); i++){
            if(ret.chain[ret.chain.length-1][i] != 0)
                flag=1;
        }
        if(flag==0){
            int x = gen.nextInt(ret.chain.length -1);
            double val = rinomine(ret.chain.length-1, x , ret.pi, ret.chain, ret.ro);
            ret.chain[ret.chain.length-1][ret.ro[x]]=val;
            ret.chain[ret.ro[x]][ret.chain.length-1]= ret.chain[x][ret.chain.length-1]; 
            
        }
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

class Tripla {

    double pi[];
    double chain[][];
    int ro[];
}
