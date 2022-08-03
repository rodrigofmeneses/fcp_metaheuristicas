package fcp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class FCP {
    /**
     * quantidade de facilidades
     */
    int N;
    /**
     * quantidade de clientes
     */
    int M;
    /**
     * capacidade original de cada facilidade
     */
    int facCap[];
    /**
     * custo fixo de instalação de cada facilidade
     */
    double[] facAbertura;
    /**
     * demanda de cada cliente
     */
    int[] clienteDem;
    /**
     * clientes provisórios, antes de remover;
     */
    /**
     * relacoes de cientes (linhas) e facilidades (colunas)
     */
    double relacoes[][];
    /**
     * relacoes provisorias, antes de remover os clientes inviáveis
     */
    @Override
    public String toString() { // quando chamar o system.out.printl()
        return "FCP{" +
                "N=" + N +
                ", M=" + M +
                ", \ncapacidade=" + Arrays.toString(facCap) +
                ", \ncusto=" + Arrays.toString(facAbertura) +
                ", \nclientes=" + Arrays.toString(clienteDem) +
                '}';
    }
    
    /**
     * abrir arquivo path
     */
    public FCP(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new FileInputStream(path));
        sc.useLocale(Locale.US);
        int clienteP[];
        double relacoesP[][];


        N = sc.nextInt();
        M = sc.nextInt();
        facCap = new int[N];
        facAbertura = new double[N];
        for(int i = 0; i < N; i++){
            facCap[i] = sc.nextInt();
            facAbertura[i] = sc.nextDouble();
        }
        
        clienteP = new int[M];
        relacoesP = new double[M][N];
        
        for(int i = 0; i < M; i++){
            clienteP[i] = sc.nextInt();
            for(int j = 0; j < N; j++){
                relacoesP[i][j] = sc.nextDouble();
            }
        }
        sc.close();
        
        //RESOLVER PROBLEMA DO -1
        //estratégia
        ArrayList<Integer> remover = new ArrayList<Integer>();
        //calcular o máximo comportado, e creio que ao remover um elemento, tem-se que pegar o segundo maior, coisa que não fiz.
        int max = 0;
        for (int i = 0; i < N; i++) {
            if(facCap[i] > max)
                max = facCap[i];
        }
        //guardar os indices defeituosos
        for (int i = 0; i < M; i++) {
            if(clienteP[i] > max)
                remover.add(i);
        }
        //criar os vetores definitivos do tamanho correto
        M -= remover.size();
        relacoes = new double[M][N];
        clienteDem = new int[M];
        int k = 0;
        int y = 0; //usurfruir de certas gambiarras para preenchelos em uma só rodada
        for (int i = 0; i < M; i++) {
            if(remover.contains(i + y)){
                k++;
                y++;
            }
            for (int j = 0; j < N; j++) {
                relacoes[i][j] = relacoesP[k][j]; 
            }
            k++;
        }
        k = 0;
        y = 0;
        for (int i = 0; i < M; i++) {
            if(remover.contains(i + y)){
                k++;
                y++;
            }
            clienteDem[i] = clienteP[k];
            k++;
        }//problema "resolvido"
    }
    
}