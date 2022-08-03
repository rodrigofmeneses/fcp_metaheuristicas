package fcp;

import java.util.LinkedList;
import static java.util.Arrays.fill;

public class GRASP implements Solver{
    private FCP fcp;
    private Sol bestSol;
    int k, ite;
    LinkedList<Integer> candidatos = new LinkedList<>();
    
    @Override
    public String toString() {
        return "GRASP{" +
                "ite=" + ite +
                "k=" + k +
                '}';
    }
    
    public GRASP(int ite, int k) {
        this.k = k;
        this.ite = ite;
    }

    @Override
    public void setFCP(FCP fcp) {
        this.fcp = fcp;
        bestSol = new Sol(fcp);    }
    
    
    private double greedyRandom(Sol current){
        
        int solucao[];
        int install[];
        int consumoAtual[];
        solucao = current.facOf;
        install = current.facOpened;
        consumoAtual = current.consumoAtual;
        
        double min = Double.MAX_VALUE;
        int jmin = -1;
        
        fill(current.facOf, -1);   
        fill(current.facOpened, 0);
        fill(current.consumoAtual, 0);
//        System.arraycopy(fcp.capacidade, 0, consumoAtual, 0, fcp.capacidade.length);

        for (int i = 0; i < fcp.M; i++) {
            for (int j = 0; j < fcp.N; j++) {
               if(fcp.clienteDem[i] < fcp.facCap[j] - consumoAtual[j]){
                   double x = current.somaCustoLocal(i, j);
                   if(min > x){
                       candidatos.addFirst(j);
                       if(candidatos.size() > k)
                          candidatos.removeLast();
                       min = x;                       
                   }
               }
            }
            
            jmin = Utils.rd.nextInt(candidatos.size());
            install[jmin] = 1;
            solucao[i] = jmin;
            consumoAtual[jmin] += fcp.clienteDem[i];
            min = Double.MAX_VALUE;
            candidatos.clear();
        }
        return current.funcaoObjetiva();
    }

    @Override
    public double run() {
        Sol current = new Sol(fcp);
        VND vnd = new VND(fcp, current);

        greedyRandom(current);
        double best = vnd.run();

        bestSol.copy(current);

        for (int i = 1; i < ite; i++) {
            greedyRandom(current);
            double x = vnd.run();

            if (x < best) {
                best = x;
                bestSol.copy(current);
                System.out.println(i + " GRASP: " + x);
            }
        }
        return best;
    }
    
    @Override
    public Sol getSol() {
        return bestSol;
    }
    
}
