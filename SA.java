package fcp;

/**Simulated Annealing*/
public class SA implements Solver{
    private FCP fcp;
    public Sol bestSol;
    public int numInteracao;
    double t0,tf,l;

    public SA(int numInteracao) {
        this.numInteracao = numInteracao;
    }

    @Override
    public String toString() {
        return "SA{" +
                "t0=" + t0 +
                ", tf=" + tf +
                ", l=" + l +
                '}';
    }

    @Override
    public void setFCP(FCP fcp) {
        this.fcp = fcp;
    }

    @Override
    public double run() {
        Sol curr = new Sol(fcp);
        curr.hungrySol();
        double FO = curr.funcaoObjetiva();
        bestSol = new Sol(fcp);
        bestSol.copy(curr);
        double currFO = FO;
        double bestFO = FO;
        
        System.out.println(FO);
 
        double custoMedio = FO/fcp.M;
        t0 = custoMedio/Math.log(fcp.N * fcp.M);
        tf = 1/Math.log(fcp.N * fcp.M);
        l = Math.pow(tf/t0, 1 / (double)numInteracao);
        
        Sol aux = new Sol(fcp);
        for(double T = t0; T > tf; T*=l){
            if(!pertub(curr,aux)) 
                continue;
            double xFO = aux.funcaoObjetiva();
            if(currFO > xFO){
                curr.copy(aux);
                currFO = xFO;
                if(bestFO > currFO){
                    bestFO = currFO;
                    bestSol.copy(curr);
                    System.out.println("SA: " + bestFO);
                    T = t0;
                }
//                System.out.println(T + " "+currFO);
            }else if(Utils.rd.nextDouble() < P(xFO-currFO,T)){
                    curr.copy(aux);
                    currFO = xFO;
    //                System.out.println(T + " "+currFO+" *");
            }
        }
        
        return bestFO;
    }

    private double P(double delta, double t) {
        return Math.exp(-delta/t);
    }
    
    private boolean pertub(Sol curr, Sol aux) { //o segredo das operações com facilidades é ficar bem claro quem é cliente e quem é facilidade
        aux.copy(curr); 
        
        if( Utils.rd.nextBoolean()){ //um cliente troca de facilidade
            int idxC[] = new int[fcp.M];
            for (int i = 0; i < fcp.M; i++)
                idxC[i] = i;
            Utils.shuffler(idxC);
            
            int idxF[] = new int[fcp.N];
            for (int i = 0; i < fcp.N; i++)
                idxF[i] = i;
            Utils.shuffler(idxF);

            for(int bi : idxC){//escolhe um cliente bi aleatório
                for(int bj: idxF){//escolhe uma facilidade aleatória
                    if (curr.facOf[bi] != bj && // no vetor solucao a facilidade do cliente bi tem que ser diferente da escolhida aleatoriamente
                            curr.consumoAtual[bj] + fcp.clienteDem[bi] <= fcp.facCap[bj]) { //cliente bi cabe?
                        
                        int origem = curr.facOf[bi]; //facilidade de origem
                        aux.facOf[bi] = bj;        //efetivar a troca de facilidade
                        aux.consumoAtual[origem] -= fcp.clienteDem[bi];
                        aux.consumoAtual[bj] += fcp.clienteDem[bi];
                        //se o pacote do item esvaziar
                        if(aux.consumoAtual[origem] == 0)
                            //elemina pacote vazio
                            aux.facOpened[origem] = 0;
                        if(aux.facOpened[bj] == 0)
                            aux.facOpened[bj] = 1;
//                        System.out.println(aux);
                        return true;
                    }
                }
            }

        }else{ // dois itens trocam de pacotes entre si
            int idxC[] = new int[fcp.M];
            for (int i = 0; i < fcp.M; i++)
                idxC[i] = i;
            Utils.shuffler(idxC);
            final int N = fcp.M;
        for (int a = 0; a < N; a++) {
            int i = idxC[a];
            int bi = curr.facOf[i]; // indice da facilidade do cliente i
            for (int b = a + 1; b < N; b++) {
                int j = idxC[b];
                int bj = curr.facOf[j]; // indice da facilidade do cliente j
                if (bi != bj && // tem que ser diferente
                    curr.consumoAtual[bi] - fcp.clienteDem[i] + fcp.clienteDem[j] <= fcp.facCap[bi] &&// j tem que caber em i
                    curr.consumoAtual[bj] - fcp.clienteDem[j] + fcp.clienteDem[i] <= fcp.facCap[bj] &&// i tem que caber em j
                    curr.facOpened[bi] == 1 && curr.facOpened[bj] == 1){ // ambas tem que estar abertas?
                    
                    aux.consumoAtual[bi] += fcp.clienteDem[j] - fcp.clienteDem[i];
                    aux.consumoAtual[bj] += fcp.clienteDem[i] - fcp.clienteDem[j];
                    
                    int aux1 = curr.facOf[i];
                    aux.facOf[i] = curr.facOf[j];
                    aux.facOf[j] = aux1;
//                    System.out.println(aux);

                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public Sol getSol() {
        return bestSol;
    }
}