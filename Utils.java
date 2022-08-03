/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fcp;

import java.util.Random;

public class Utils {
    public static Random rd = new Random(15);
    public static void shuffler(int v[]){
        for(int i = v.length-1; i > 1; i--){
            int x = rd.nextInt(i);
            int aux = v[i];
            v[i] = v[x];
            v[x] = aux;
        }
    }
}
