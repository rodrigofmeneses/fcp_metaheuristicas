package fcp;

public interface Solver {
    public void setFCP(FCP fcp);

    public Sol getSol();
    
    public double run();
}