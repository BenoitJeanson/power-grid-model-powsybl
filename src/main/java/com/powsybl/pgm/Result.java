package com.powsybl.pgm;

public interface Result {
    void toNode(int id, double uPu, double u, double uAngle);

    void toBranch(int id,
            double pFrom, double qFrom, double iFrom, double sFrom,
            double pTo, double qTo, double iTo, double sTo);

    void toAppliance(int id, double p, double q, double i, double s, double pf);

}
