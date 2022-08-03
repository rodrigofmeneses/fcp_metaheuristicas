package fcp;

import java.util.Arrays;
import java.util.Objects;
import static java.util.Arrays.fill;



public class Sol implements Comparable<Sol>{
    /**
     * vetor solucao
     */
    int[] facOf; //binOf
    /**
     * referencia para o problema de facilidade
     */
    FCP fcp;
    /**
     *  vetor que indica se a facilidade foi instalada
     */
    int[] facOpened;
    /**
     *  vetor de capacidades após operações, 
     */
    int[] consumoAtual;
    
    /**
     * construtor 
     * @param fcp  referencia para o problema de facilidade
     */
    public Sol(FCP fcp){
        this.fcp = fcp;
        facOf = new int[fcp.M]; //tam. num clientes
        facOpened = new int[fcp.N]; //tam, num fac
        consumoAtual = new int[fcp.facCap.length];
        
//        System.arraycopy(fcp.capacidade, 0, consumoAtual, 0, fcp.capacidade.length);
        fill(consumoAtual, 0);
        fill(facOf, -1);
        fill(facOpened, 0);
    }
    
    @Override
    public String toString(){
        return "Sol{ " + Arrays.toString(facOf) +
                " \nFuncao Objetiva = " + funcaoObjetiva() +
                " \nInstall = " + Arrays.toString(facOpened) +
                " \nCapacidades = " + Arrays.toString(consumoAtual) +
                '}';
    }
    
    /**
     * Função que calcula o custo total
     * @return custo total
     */
    public double funcaoObjetiva(){ //ver qual é a desse BAGULHO
        
        double custo = 0;
        /**
         * calcula o custo de abertura das facilidades
         */
        for(int i = 0; i < fcp.N; i++){
            custo += facOpened[i] * fcp.facAbertura[i];
        }

        /**
         * calcula o custo das relação entre clientes e facilidades
         */
        for(int i = 0; i < fcp.M; i++)
            if(facOf[i] == -1)
                return Double.MAX_VALUE;
            else
                custo += fcp.relacoes[i][facOf[i]];
        
        return custo;
    }
    //Solução gulosa
    public void hungrySol(){
        double min = Double.MAX_VALUE;
        int jmin = -1;
        
        fill(facOf, -1);   
        fill(facOpened, 0);
        fill(consumoAtual, 0);
//        System.arraycopy(fcp.capacidade, 0, consumoAtual, 0, fcp.capacidade.length);

        for (int i = 0; i < fcp.M; i++) {
            for (int j = 0; j < fcp.N; j++) {
               if(fcp.clienteDem[i] < fcp.facCap[j] - consumoAtual[j]){
                   double x = somaCustoLocal(i, j);
                   if(min > x){
                       min = x;
                       jmin = j;
                   }
               }
            }
            
            if(jmin != -1){
                facOpened[jmin] = 1;
                facOf[i] = jmin;
                consumoAtual[jmin] += fcp.clienteDem[i];
                jmin = -1;
            }else
                facOf[i] = -1; //cliente i nao foi atendido

            min = Double.MAX_VALUE;
        }
    }
    //utilizado no RMS
    public void RandomSol(){
        //ideia: (se couber) alocar cada cliente em uma facilidade aleatória 
        fill(facOf, -1);
        fill(facOpened, 0);
        fill(consumoAtual, 0);
//        System.arraycopy(fcp.capacidade, 0, consumoAtual, 0, fcp.capacidade.length);
        
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
        
        for (int a = 0; a < fcp.M; a++) {
            int i = idxC[a];
            for (int b = 0; b < fcp.N; b++) {
                int j = idxF[b];
                if(fcp.clienteDem[i] < fcp.facCap[j] - consumoAtual[j]){ 
                   facOpened[j] = 1;
                   facOf[i] = j;
                   consumoAtual[j] += fcp.clienteDem[i];
                   break;
                }
            }
        }
    }
    
    public void copy(Sol s){
        for (int i = 0; i < facOf.length; i++) {
            facOf[i] = s.facOf[i];
        }
        
        for(int i = 0; i < consumoAtual.length; i++){
            consumoAtual[i] = s.consumoAtual[i];
        }
        
        for(int i = 0; i < facOpened.length; i++){
            facOpened[i] = s.facOpened[i];
        }
    }
    
    /**
     * Funcao que soma o custo total da relacao cliente/facilidade
     * @param i cliente
     * @param j facilidade
     * @return 
     */
    public double somaCustoLocal(int i, int j){
        return fcp.relacoes[i][j] + (fcp.facAbertura[j] * (1 - facOpened[j]))/(fcp.M - i);
    }
    
    public double deltaFacChg(int c, int facOld, int facNew){
        double d = fcp.relacoes[c][facNew] - fcp.relacoes[c][facOld];
        if(consumoAtual[facOld] == fcp.clienteDem[c])
            d -= fcp.facAbertura[facOld];
        if(consumoAtual[facNew] == 0)
            d += fcp.facAbertura[facNew];
        return d;
    }
    
     @Override
    public boolean equals(Object obj) {
        //se forem o mesmo objeto, retorna true
        if(obj == this) return true;

        // aqui o cast é seguro por causa do teste feito acima
        Sol sol = (Sol) obj;

        //aqui você compara a seu gosto, o ideal é comparar atributo por atributo
        return Arrays.equals(facOf, sol.facOf) &&
                Objects.equals(fcp, sol.fcp);
    }
    
    
    public int compareTo(Sol sol) {
        return Double.compare(this.funcaoObjetiva(), sol.funcaoObjetiva());
    }
    
}