package fcp;

public class RMS implements Solver{
    private FCP fcp;
    private Sol bestSol;
    int ite = 100;
    
    @Override
    public String toString() {
        return "RMS{" +
                "ite=" + ite +
                '}';
    }
    
    public RMS(int ite){
        this.ite = ite;
    }
    
    @Override
    public void setFCP(FCP fcp) {
        this.fcp = fcp;
        this.bestSol = new Sol(fcp);
    }
    
    @Override
    public Sol getSol() {
        return bestSol;
    }
    
    @Override
    public double run() {
        Sol current = new Sol(fcp);
        VND vnd = new VND(fcp, current);
        
        double best = Integer.MAX_VALUE;
        for (int i = 0; i < ite; i++) {
            current.RandomSol();
            double x = vnd.run();

            if (x < best) {
                best = x;
                bestSol.copy(current);
                System.out.println(i + " RMS: " + x);
            }
        }
//        System.out.println(best);
        return best;
    }
}
