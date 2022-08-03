package fcp;

public class ILS implements Solver{
    private int k, ite;
    private FCP fcp;
    private Sol bestSol;
    
    @Override
    public String toString() {
        return "ILS{" +
                "ite=" + ite +
                "k=" + k +
                '}';
    }
    
    public ILS(int ite, int k){
        this.ite = ite;
        this.k = k;
    }
    
    @Override
    public void setFCP(FCP fcp) {
        this.fcp = fcp;
        this.bestSol = new Sol(fcp);
    }
    /**
     * Realoca os clientes que estão sem facilidade em facilidades aleatórias
     * @param current 
     */
    private void fit(Sol current) {
        int solucao[] = current.facOf; // solucao atual
        int n = fcp.M; // numero de clientes
        
        int idxC[];
        int idxF[];
        idxC = new int[fcp.M];
        idxF = new int[fcp.N];
        for (int i = 0; i < idxC.length; i++)
            idxC[i] = i;
        for (int i = 0; i < idxF.length; i++)
            idxF[i] = i;
        
        Utils.shuffler(idxC);
        Utils.shuffler(idxF);
        
        for (int a = 0; a < n; a++) {
            int i = idxC[a];
            if (solucao[i] == -1) {
                for (int b = 0; b < fcp.N; b++) {
                    int j = idxF[b];
                    if (fcp.clienteDem[i] < fcp.facCap[j] - current.consumoAtual[j]) {
                        solucao[i] = j;
                        current.consumoAtual[j] += fcp.clienteDem[j];
                        current.facOpened[j] = 1;
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param k numero de facilidades desacoladas
     * @param current saida com solucao perturbada
     * @param bestSol solucao de entrada
     */
    private void pertub(int k, Sol current, Sol bestSol) {
        current.copy(bestSol);
        int n = fcp.N; //quantidade de facilidades
        for (int i = 0; i < k; i++) {
            int x = Utils.rd.nextInt(n);
            current.facOpened[x] = 0;
            for (int j = 0; j < fcp.M; j++) {
                if(current.facOf[j] == x){
                    current.facOf[j] = -1;
                    current.consumoAtual[x] -= fcp.clienteDem[j];
                }
            }
        }
        fit(current);
    }
    
    @Override
    public Sol getSol() {
        return bestSol;
    }
    
    @Override
    public double run(){
        Sol current = new Sol(fcp);
        VND vnd = new VND(fcp, current);
        
        current.hungrySol();
        double best = vnd.run();
        bestSol.copy(current);
        
        for (int i = 1; i < ite; i++) {
            //pertubacao
            pertub(k, current, bestSol); 
            //busca do otimo local
            double x = vnd.run();
            //teste para o novo otimo local
            if (x < best) {
                best = x;
                bestSol.copy(current);
                System.out.println(i + " ILS: " + x);
            }
        }
//        System.out.println(best);
        return best;
        
    }
    
}
