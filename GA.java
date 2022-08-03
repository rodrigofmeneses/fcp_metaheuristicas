package fcp;

import java.util.ArrayList;
import java.util.Collections;

public class GA implements Solver{

    private FCP fcp;
    private Sol bestSol;
    private int popSize;
    private double muteRatio;
    private int k;
    private int popIdx[];
    private int idx[];
    private int ite;
    
    public GA(int popSize, double muteRatio, int k, int ite) {
        this.ite = ite;
        this.popSize = popSize;
        this.muteRatio = muteRatio;
        this.k = k;
        popIdx = new int[popSize];
        for (int i = 0; i < popSize; i++)
            popIdx[i] = i;

    }
    
    @Override
    public void setFCP(FCP fcp) {
        this.fcp = fcp;
        bestSol = new Sol(fcp);
        pop = new ArrayList<>();
        idx = new int[fcp.M];
        for (int i = 0; i < idx.length; i++)
            idx[i] = i;    
    }
    
    ArrayList<Sol> pop;
    
    private Sol[] select() {
        
        Utils.shuffler(popIdx);
        Sol dad = pop.get(0);
        for (int i = 1; i < k; i++)
            if (pop.get(i).compareTo(dad) < 0) // i é melhor que dad
                dad = pop.get(i);
        
        Utils.shuffler(popIdx);
        Sol mom = null;
        for (int i = 0; i < k; i++)
            if (pop.get(i) != dad
                    && (mom == null || pop.get(i).compareTo(mom) < 0)) // i é melhor que dad
                mom = pop.get(i);
        return new Sol[]{dad, mom};
    }
    
    @Override
    public double run() {
        popIni(); //OK
        
        for (int i = 0; i < ite; i++) {
            Collections.sort(pop);
            
            while (pop.size() > popSize)
                pop.remove(pop.size() - 1);
            
            Sol parents[] = select();
            
            Sol son1 = crossover(parents[0], parents[1]);
            VND vnd = new VND(fcp,son1);    
            vnd.run();
            if (son1 != null) {
                if (Utils.rd.nextDouble() < muteRatio)
                    mutate(son1);
                if (!pop.contains(son1))
                    pop.add(son1);
            }
            

            Sol son2 = crossover(parents[1], parents[0]);
            if (son2 != null) {
                if (Utils.rd.nextDouble() < muteRatio)
                    mutate(son2);
                if (!pop.contains(son2))
                    pop.add(son2);
            }
//            System.out.println("A FO do melhor da populacao nessa rodade e: " + pop.get(0).funcaoObjetiva());
        }
        bestSol.copy(pop.get(0));
        System.err.println(bestSol);
        
        return bestSol.funcaoObjetiva();
    }
    
    // um cliente troca de facilidade
    private void mutate(Sol sol) {
     Utils.shuffler(idx);
        for (int i : idx) {
            int pIdx[] = new int[fcp.N];
            int bi = sol.facOf[i];
            for (int j = 0; j < pIdx.length; j++)
                pIdx[j] = j;
            Utils.shuffler(pIdx);
            for (int j : pIdx) {
                if (bi != j
                        && sol.consumoAtual[j] + fcp.clienteDem[i] <= fcp.facCap[j]) {
                    sol.consumoAtual[j] += fcp.clienteDem[i];
                    sol.consumoAtual[bi] -= fcp.clienteDem[i];
                    sol.facOf[i] = j;
                    if (sol.consumoAtual[bi] == 0) {
//                        System.out.println("mutation OBA!");
                        //fecha facilidade vazia
                        sol.facOpened[bi] = 0;
                    }
                    return;
                }
            }
        }
    }
    
    public static Sol crossover(Sol dad, Sol mom) {
        FCP fcp = dad.fcp;
        //determina os pontos de corte
        int a = fcp.M / 3;
        int b = 2 * a;

        //instancio o filho
        Sol son = new Sol(fcp);

        //a parte central do filho é igual ao do pai
        for (int i = a; i < b; i++) {//Primeiro dar um confere se vai caber
            if (son.consumoAtual[dad.facOf[i]] + fcp.clienteDem[i] <= fcp.facCap[dad.facOf[i]]) {
                son.facOpened[dad.facOf[i]] = 1;
                son.facOf[i] = dad.facOf[i];
                son.consumoAtual[dad.facOf[i]] += fcp.clienteDem[i];
            } else {//se não couber tenta botar em qualquer cara
                boolean cabeu = false;
                for (int j = 0; j < fcp.M; j++) {
                    if (son.consumoAtual[j] + fcp.clienteDem[i] <= fcp.facCap[mom.facOf[i]]) {
                        son.consumoAtual[j] += fcp.clienteDem[i];
                        son.facOf[i] = j;
                        cabeu = true;
                        break;
                    }
                }
                if (!cabeu) {
                    return null;
                }
            }
        }
        
        //Agora resolver isso pra mãe primeira parte, processo do pai se repete mais vezes
        for (int i = 0; i < a; i++) {
            if (son.consumoAtual[mom.facOf[i]] + fcp.clienteDem[i] <= fcp.facCap[mom.facOf[i]]) {
                son.facOpened[mom.facOf[i]] = 1;
                son.facOf[i] = mom.facOf[i];
                son.consumoAtual[son.facOf[i]] += fcp.clienteDem[i];
            } else {
                boolean cabeu = false;
                for (int j = 0; j < fcp.M; j++) {
                    if (son.consumoAtual[j] + fcp.clienteDem[i] <= fcp.facCap[mom.facOf[i]]) {
                        son.consumoAtual[j] += fcp.clienteDem[i];
                        son.facOf[i] = j;
                        cabeu = true;
                        break;
                    }
                }
                if (!cabeu) {
                    return null;
                }
            }
        }
        
        //Segunda parte da mãe
        for (int i = b; i < fcp.M; i++) {
            if (son.consumoAtual[mom.facOf[i]] + fcp.clienteDem[i] <= fcp.facCap[mom.facOf[i]]) {
                son.facOpened[mom.facOf[i]] = 1;
                son.facOf[i] = mom.facOf[i];
                son.consumoAtual[son.facOf[i]] += fcp.clienteDem[i];
            } else {
                boolean cabeu = false;
                for (int j = 0; j < fcp.M; j++) {
                    if (son.consumoAtual[j] + fcp.clienteDem[i] <= fcp.facCap[mom.facOf[i]]) {
                        son.consumoAtual[j] += fcp.clienteDem[i];
                        son.facOf[i] = j;
                        cabeu = true;
                        break;
                    }
                }
                if (!cabeu) {
                    return null;
                }
            }
        }
           
        
        return son;
    }
    
    private void popIni() {
        pop.clear();
        for (int i = 0; i < popSize; i++) {
            Utils.shuffler(idx);
            Sol sol = new Sol(fcp);
            sol.RandomSol();
            pop.add(sol);
        }
    }
    
    @Override
    public Sol getSol() {
        return bestSol;
    }
    
}
