/**
 * João Menezes - 379722
 * Rodrigo Fabrício - 376176
 * Saulo Costa - 364901
 */

package fcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static double benchmark(String dir, Solver solver) throws FileNotFoundException {
        File file = new File(dir);
        File arq[] = file.listFiles((d, name) -> name.endsWith(".txt"));
        Arrays.sort(arq);
        int count = 0;
        double gap = 0;
        long time = 0;
//        System.out.println(solver);
        for (File f : arq) {
            count++;
            FCP fcp = new FCP(f.getPath());
            Sol sol;

            Utils.rd.setSeed(111);

            solver.setFCP(fcp);
            long t = System.currentTimeMillis();
            double x = solver.run();
            t = System.currentTimeMillis() - t;
            time += t;
            gap += x;
            System.out.println(count + " - " + x + "  " + f + ": " + x + " T: " + t );
        }
        System.out.printf("%s\t %.2f\t %d\n", solver, gap/count, time / count);
        System.out.println();
        return 100 * gap / count;
    }

    public static void main(String args[]) throws FileNotFoundException {
        String dir = "instances/";
        
        FCP fcp = new FCP("instances/fcp.txt");
        Sol sol = new Sol(fcp);
        sol.hungrySol();
        System.out.println(sol);
//        HC hc = new HC(fcp,sol);
//        hc.run();
//        System.err.println(hc + "\n");
        
//        FCP fcp1 = new FCP("instances/fcp.txt");
//        Sol sol1 = new Sol(fcp1);
//        long t0 = System.currentTimeMillis();
//        sol1.hungrySol();
//        System.out.println(sol1.funcaoObjetiva());
//        VND vnd = new VND(fcp1,sol1);
//        vnd.run();
//        System.err.println(vnd + "\n");
//        System.out.println(System.currentTimeMillis() - t0);
//
//        //BENCHMARK
//    
//        benchmark(dir, new RMS(10000));
//        benchmark(dir, new VNS(10000,10,5));
//        benchmark(dir, new ILS(10000, 2));
//        benchmark(dir, new GRASP(10000, 3));
//        benchmark(dir, new SA(100000));
//        benchmark(dir, new GLS(10000));
//        benchmark(dir, new GA(1000, .5, 25, 5000));


    }
}