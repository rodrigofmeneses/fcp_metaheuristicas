package fcp;

public class VND implements Solver{

    private FCP fcp;
    private Sol sol;
    private int idxC[], idxF[];

    public VND(FCP fcp, Sol sol) {
        this.fcp = fcp;
        this.sol = sol;
        idxC = new int[fcp.M];
        idxF = new int[fcp.N];
        for (int i = 0; i < idxC.length; i++)
            idxC[i] = i;
        for (int i = 0; i < idxF.length; i++)
            idxF[i] = i;
    }
    
    @Override
    public String toString() {
        return "VND{" +
                "sol=" + sol +
                '}';
    }
    
    @Override
    public void setFCP(FCP fcp) {
        this.fcp = fcp;
        this.sol = new Sol(fcp);   
    }
    /**
     * Tirar 1 cliente de alguma facilidade e realoca-lo aleatoriamente
     * @return 
     */
    boolean move1(){
        
        for (int a = 0; a < fcp.M; a++) {
            int i = idxC[a]; // i = cliente
            int fi = solucao[i]; // fi = facilidade onde está i
            for (int b = 0; b < fcp.N; b++){
                int j = idxF[b];
                
                if (fi != j &&
                        cliente[i] < fcp.facCap[j] - consumoAtual[j] 
                        && sol.deltaFacChg(i, fi, j) < -0.001) {
                    
//                      double joao = sol.deltaFacChg(i, fi, j);
                    consumoAtual[fi] -= cliente[i];
                    consumoAtual[j] += cliente[i];
                    solucao[i] = j;
                    if(consumoAtual[fi] == 0){
                        install[fi] = 0;
                    }
                    if(install[j] == 0){
                        install[j] = 1;
                    }                        
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * trocar 2 clientes entre si, caso a troca seja benéfica para a funcao objetiva manter
     * @return 
     */
    boolean move2() { 
        
        double fo = sol.funcaoObjetiva();
        
        final int m = solucao.length;
        for (int a = 0; a < m; a++) {
            int i = idxC[a];
            int fi = solucao[i]; // indice da facilidade do cliente i
            for (int b = a + 1; b < m; b++) {
                int j = idxC[b];
                int fj = solucao[j]; // indice da facilidade do cliente j
                if (fi != fj && // tem que ser diferente
                    consumoAtual[fi] - cliente[i] + cliente[j] <= fcp.facCap[fi] &&// j tem que caber em i
                    consumoAtual[fj] - cliente[j] + cliente[i] <= fcp.facCap[fj] &&// i tem que caber em j
                    sol.facOpened[fi] == 1 && sol.facOpened[fj] == 1){ // ambas tem que estar abertas?
                    
                    consumoAtual[fi] += cliente[j] - cliente[i];
                    consumoAtual[fj] += cliente[i] - cliente[j];
                    int aux = solucao[i];
                    solucao[i] = solucao[j];
                    solucao[j] = aux;
                            
                    double x = sol.funcaoObjetiva(); //precisa otimizar isso
//                    System.out.println("MV2 "+x);

                    if (x < fo) {
                        fo = x; // atualiza melhor custo
//                        System.out.println("MV2 "+x);
                        return true;
                    }else{
                        consumoAtual[fi] += cliente[i] - cliente[j];
                        consumoAtual[fj] += cliente[j] - cliente[i];
                        aux = solucao[i];
                        solucao[i] = solucao[j];
                        solucao[j] = aux;
                    }
                }
            }
        }

        return false;
    }
    
    int solucao[];
    int cliente[];
    int consumoAtual[];
    int install[];

    @Override
    public double run() {
        
    solucao = sol.facOf;
    cliente = fcp.clienteDem;
    consumoAtual = sol.consumoAtual;
    install = sol.facOpened;

        //VND

        boolean flag;
        do {
            Utils.shuffler(idxC);
            Utils.shuffler(idxF);

            flag = move1();
            if (!flag)
                flag = move2();
//            System.out.println(sol);
        } while (flag);


        return sol.funcaoObjetiva();
        
    }

    @Override
    public Sol getSol() {
        return sol;
    }
    
}

