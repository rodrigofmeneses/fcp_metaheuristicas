package fcp;

public class HC {
    final FCP fcp;
    Sol sol;

    public HC(FCP fcp, Sol sol) {
        this.fcp = fcp;
        this.sol = sol;
    }
    
     @Override
    public String toString() {
        return "HC{" +
                "sol=" + sol +
                '}';
    }   
    
    public double run(){
        int solucao[] = sol.facOf;
        int consumoAtual[] = sol.consumoAtual;
        int install[] = sol.facOpened;
        int cliente[] = fcp.clienteDem;
        
        boolean moved;
        //hillclimb
        do {
            moved = false;
            //busca local
            ls:
            for (int i = 0; i < fcp.M; i++) {
                int fi = solucao[i];
                for (int j = 0; j < fcp.N; j++)
                    if (fi != j &&
                        cliente[i] < fcp.facCap[j] - consumoAtual[j] 
                        && sol.deltaFacChg(i, fi, j) < -0.001) {
                        consumoAtual[fi] -= cliente[i];
                        consumoAtual[j] += cliente[i];
                        solucao[i] = j;
                        if(consumoAtual[fi] == 0){
                            install[fi] = 0;
                        }
                        if(install[j] == 0){
                            install[j] = 1;
                        }
                        moved = true;
                        break ls;
                    }
            }
        }while (moved);
        
//    System.out.println(" HC: " + a);
        return sol.funcaoObjetiva();
    }
    
}
