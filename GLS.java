package fcp;

import static java.util.Arrays.fill;

public class GLS implements Solver{
    Sol bestSol;
    FCP fcp;
    int ite, p[][];

    @Override
    public String toString() {
        return "GLS{" +
                "ite=" + ite +
                '}';
    }
    
    public GLS(int ite) {
        this.ite = ite;
    }

    @Override
    public void setFCP(FCP fcp) {
        this.fcp = fcp;
        bestSol = new Sol(fcp);
        p = new int[fcp.M][];
        for (int i = 0; i < fcp.M; i++)
            p[i] = new int[i];
    }

    @Override
    public double run() {
        //constroi solução inicial
        Sol sol = new Sol(fcp);
        sol.hungrySol();
        bestSol.copy(sol);
        System.out.println(sol.funcaoObjetiva());
        
        for (int i = 0; i < ite; i++) {
            hc(sol);
//            System.out.println(sol.funcaoObjetiva());
            if(sol.funcaoObjetiva() < bestSol.funcaoObjetiva()){
                bestSol.copy(sol);
                System.out.println("GLS: "+bestSol.funcaoObjetiva());
                penaltyReset();
            }
            updateMatriz(sol);
        }
        return bestSol.funcaoObjetiva();
    }

    private void penaltyReset() {
        for (int i = 0; i < fcp.M; i++)
            fill(p[i],0);
    }


    private void updateMatriz(Sol sol) {
        int k = 0;
        k = Utils.rd.nextInt(fcp.N);

        for (int i = 0; i < fcp.M; i++)
            if(sol.facOf[i] == k){
                for (int j = 0; j < i; j++)
                    if(sol.facOf[j] == k){
                        p[i][j]++;
                    }
            }
    }

    @Override
    public Sol getSol() {
        return bestSol;
    }


    public double hc(Sol sol) {
        int solucao[] = sol.facOf;
        int cliente[] = fcp.clienteDem;
        int consumoAtual[] = sol.consumoAtual;
        

        double a = sol.funcaoObjetiva() * penalty(sol);

        boolean moved;
        //hillclimb
        do {
            moved = false;
            //busca local
            ls:
            for (int i = 0; i < fcp.M; i++) {
                int fi = solucao[i];
                for (int j = 0; j < fcp.N; j++)
                    if (fi != j && cliente[i] < fcp.facCap[j] - consumoAtual[j]) {
                        consumoAtual[fi] -= cliente[i];
                        consumoAtual[j] += cliente[i];
                        solucao[i] = j;
                        if(consumoAtual[fi] == 0){
                            sol.facOpened[fi] = 0;
                        }
                        if(sol.facOpened[j] == 0){
                            sol.facOpened[j] = 1;
                        }
                        //estou tendo problemas para otimizar esse negócio
                        
                        double x = sol.funcaoObjetiva() * penalty(sol);
                        
                        
                        if (x < a) {
                            a = x; // atualiza melhor custo
//                            System.out.println(" HC: " + sol.funcaoObjetiva());
                            moved = true;
                            break ls;
                        
                        }else{
                            consumoAtual[fi] += cliente[i];
                            consumoAtual[j] -= cliente[i];
                            solucao[i] = fi;
                            if(consumoAtual[j] == 0){
                                sol.facOpened[j] = 0;
                            }
                            sol.facOpened[fi] = 1;
                        }
                    }
            }
        } while (moved);


        return a;
    }

    private double penalty(Sol sol) {
        int s =0;
        for (int i = 0; i < fcp.M; i++) {
            for (int j = 0; j < i; j++)
            if(sol.facOf[i] == sol.facOf[j]){
                s+=p[i][j];
            }
        }
        return Math.log(s+1);
    }
}