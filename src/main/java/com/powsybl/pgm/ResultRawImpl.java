package com.powsybl.pgm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultRawImpl implements Result {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultRawImpl.class);

    private List<Node> nodes = new ArrayList<>();
    private List<Branch> branches = new ArrayList<>();
    private List<Appliance> appliances = new ArrayList<>();

    @Override
    public void toNode(int id, double uPu, double u, double uAngle) {
        LOGGER.debug("toNode id: {}\tuPu: {}\tu: {}\tuAngle: {}", id, uPu, u, uAngle);
        nodes.add(new Node(id, uPu, u, uAngle));
    }

    @Override
    public void toBranch(int id,
            double pFrom, double qFrom, double iFrom, double sFrom,
            double pTo, double qTo, double iTo, double sTo) {
        LOGGER.debug("toBranch id: {}", id);
        LOGGER.debug("\tpFrom: {}\tqFrom: {}\tiFrom: {}\tsFrom: {}", pFrom, qFrom, iFrom, sFrom);
        LOGGER.debug("\tpTo: {}\tqTo: {}\tiTo: {}\tsTo: {}", pTo, qTo, iTo, sTo);
        branches.add(new Branch(id, pFrom, qFrom, iFrom, sFrom, pTo, qTo, iTo, sTo));
    }

    @Override
    public void toAppliance(int id, double p, double q, double i, double s, double pf) {
        LOGGER.debug("addAppliance id: {}",id);
        LOGGER.debug("p: {}\t q: {}\ti: {}\ts: {}\tpf: ",p, q, i, s, pf);
        appliances.add(new Appliance(id, p, q, i, s, pf));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public class Node {
        private int id;
        private double uPu;
        private double u;
        private double uAngle;

        Node(int id, double uPu, double u, double uAngle) {
            this.id = id;
            this.uPu = uPu;
            this.u = u;
            this.uAngle = uAngle;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getUPu() {
            return uPu;
        }

        public void setUPu(double uPu) {
            this.uPu = uPu;
        }

        public double getU() {
            return u;
        }

        public void setU(double u) {
            this.u = u;
        }

        public double getuAngle() {
            return uAngle;
        }

        public void setuAngle(double uAngle) {
            this.uAngle = uAngle;
        }
    }

    public class Branch {
        private int id;
        private double pFrom;
        private double qFrom;
        private double iFrom;
        private double sFrom;
        private double pTo;
        private double qTo;
        private double iTo;
        private double sTo;

        public Branch(int id, double pFrom, double qFrom, double iFrom, double sFrom, double pTo, double qTo,
                double iTo, double sTo) {
            this.id = id;
            this.pFrom = pFrom;
            this.qFrom = qFrom;
            this.iFrom = iFrom;
            this.sFrom = sFrom;
            this.pTo = pTo;
            this.qTo = qTo;
            this.iTo = iTo;
            this.sTo = sTo;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getPFrom() {
            return pFrom;
        }

        public void setPFrom(double pFrom) {
            this.pFrom = pFrom;
        }

        public double getQFrom() {
            return qFrom;
        }

        public void setQFrom(double qFrom) {
            this.qFrom = qFrom;
        }

        public double getIFrom() {
            return iFrom;
        }

        public void setIFrom(double iFrom) {
            this.iFrom = iFrom;
        }

        public double getSFrom() {
            return sFrom;
        }

        public void setSFrom(double sFrom) {
            this.sFrom = sFrom;
        }

        public double getPTo() {
            return pTo;
        }

        public void setPTo(double pTo) {
            this.pTo = pTo;
        }

        public double getQTo() {
            return qTo;
        }

        public void setQTo(double qTo) {
            this.qTo = qTo;
        }

        public double getITo() {
            return iTo;
        }

        public void setITo(double iTo) {
            this.iTo = iTo;
        }

        public double getSTo() {
            return sTo;
        }

        public void setSTo(double sTo) {
            this.sTo = sTo;
        }

    }

    public class Appliance {
        private int id;
        private double p;
        private double q;
        private double i;
        private double s;
        private double pf;

        public Appliance(int id, double p, double q, double i, double s, double pf) {
            this.id = id;
            this.p = p;
            this.q = q;
            this.i = i;
            this.s = s;
            this.pf = pf;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getP() {
            return p;
        }

        public void setP(double p) {
            this.p = p;
        }

        public double getQ() {
            return q;
        }

        public void setQ(double q) {
            this.q = q;
        }

        public double getI() {
            return i;
        }

        public void setI(double i) {
            this.i = i;
        }

        public double getS() {
            return s;
        }

        public void setS(double s) {
            this.s = s;
        }

        public double getPf() {
            return pf;
        }

        public void setPf(double pf) {
            this.pf = pf;
        }

    }

}
