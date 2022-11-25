package com.powsybl.pgm;

public class main {
    public static void main(String[] args) {
        PgmWrapper pgmw = new PgmWrapper();
        pgmw.addNode(1, 10.5e3);
        pgmw.addNode(2, 10.5e3);
        pgmw.addNode(6, 10.5e3);

        pgmw.addLine(3, 1, true, 2,true, 0.25, 0.2, 10e-6, 0.0, 1e3);
        pgmw.addLine(5, 2, true, 6,true, 0.25, 0.2, 10e-6, 0.0, 1e3);
        pgmw.addLine(8, 1, true, 6,true, 0.25, 0.2, 10e-6, 0.0, 1e3);

        pgmw.addLoad(4, 2, true, 20e6, 5e6);
        pgmw.addLoad(7, 6, true, 10e6, 2e6);

        pgmw.addSource(10, 1, true, 1.0);
        pgmw.finalizeConstruction();
        pgmw.runPf();
        pgmw.getResult(new ResultRawImpl());

        pgmw.cleanup();

    }
    
}
