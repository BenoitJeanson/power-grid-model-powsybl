package com.powsybl.pgm;

import java.io.IOException;

import org.scijava.nativelib.NativeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgmWrapper {

    enum LoadGenType {
        CONSTPOWER(0),
        CONSTIMPEDANCE(1),
        CONSTCURRENT(2);

        int val;

        LoadGenType(int val) {
            this.val = val;
        }

        int val() {
            return val;
        }

    }

    // Load C++ shared library
    static {
        try {
            NativeLoader.loadLibrary("pgm");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PgmWrapper.class);
    private long pgmPtr = 0;

    public PgmWrapper() {
        pgmPtr = createCppObject();
    }

    public void addNode(int Id, double u_rated) {
        addNode(pgmPtr, Id, u_rated);
        LOGGER.debug("addNode\t Id: {}\tu_rated: {}", Id, u_rated);
    }

    public void addSource(int Id, int nodeId, boolean isConnected, double u_ref, double u_ref_angle, double sk,
            double rx_ratio, double z01_ratio) {
        addSource(pgmPtr, Id, nodeId, isConnected, u_ref, u_ref_angle, sk, rx_ratio, z01_ratio);
        LOGGER.debug(
                "addSource\t Id: {}\tnodeId: {}\tisConnected: {}\tu_ref: {}\tu_ref_angle: {}\tsk: {}\trx_ratio: {}\tz01_ratio: {}",
                Id, nodeId, isConnected, u_ref, u_ref_angle, sk, rx_ratio, z01_ratio);
    }

    public void addSource(int Id, int nodeId, boolean isConnected, double u_ref) {
        addSource(Id, nodeId, isConnected, u_ref, 0, 1e10, 0.1, 1.0);
    }

    public void addSymLoadGenInput(int Id, int nodeId, boolean isConnected, LoadGenType type, double p, double q) {
        addSymLoadGenInput(pgmPtr, Id, nodeId, isConnected, type.val(), p, q);
        LOGGER.debug("addSymLoaGendInput\tId: {}\tnodeId: {}\tisConnected: {}\ttype: {}\tp: {}\tq: {}",
                Id, nodeId, isConnected, type, p, q);
    }

    public void addLoad(int Id, int nodeId, boolean isConnected, LoadGenType type, double p, double q){
        addSymLoadGenInput(Id, nodeId, isConnected, type, p, q);
    }
    
    public void addLoad(int Id, int nodeId, boolean isConnected, double p, double q){
        addSymLoadGenInput(Id, nodeId, isConnected, LoadGenType.CONSTPOWER, p, q);
    }

    public void addGen(int Id, int nodeId, boolean isConnected, LoadGenType type, double p, double q){
        addSymLoadGenInput(Id, nodeId, isConnected, type, -p, -q);
    }
    
    public void addGen(int Id, int nodeId, boolean isConnected, double p, double q){
        addSymLoadGenInput(Id, nodeId, isConnected, LoadGenType.CONSTPOWER, -p, -q);
    }
    
    public void addLine(int Id, int nodeFromId, boolean isFromConnected, int nodeToId, boolean isToConnected,
            double r1, double x1, double c1, double tan1,
            double r2, double x2, double c2, double tan2, double ratedCurrent) {
        addLine(pgmPtr, Id, nodeFromId, isFromConnected, nodeToId, isToConnected, r1, x1, c1, tan1, r2, x2, c2, tan2,
                ratedCurrent);
        LOGGER.debug(
                "addLine\tId: {}\tnodeFromId: {}\tisFromConnected: {}\tnodeToId: {}\tisToConnected: {}\tr1: {}\tx1: {}\tc1: {}\ttan1: {}\tr2: {}\tx2: {}\tc2: {}\ttan2: {}\tratedCurrent: {}",
                Id, nodeFromId, isFromConnected, nodeToId, isToConnected, r1, x1, c1, tan1, r2, x2, c2, tan2,
                ratedCurrent);
    }

    public void addLine(int Id, int nodeFromId, boolean isFromConnected, int nodeToId, boolean isToConnected,
            double r1, double x1, double c1, double tan1, double ratedCurrent) {
        addLine(Id, nodeFromId, isFromConnected, nodeToId, isToConnected, r1, x1, c1, tan1, r1, x1, c1, tan1,
                ratedCurrent);
    }

    public void addTransformer(int Id, int nodeFromId, boolean isFromConnected, int nodeToId,
            boolean isToConnected,
            double u1, double u2, double sn, double uk, double pk, double i0, double p0,
            int winding_from, int winding_to, byte clock,
            int tap_side, byte tap_pos, byte tap_min, byte tap_max, byte tap_nom, double tap_size,
            double uk_min, double uk_max,
            double pk_min, double pk_max,
            double r_grounding_from, double x_grounding_from, double r_grounding_to, double x_grounding_to) {

        addTransformer(pgmPtr, Id, nodeFromId, isFromConnected, nodeToId, isToConnected, u1, u2, sn, uk, pk, i0, p0,
                winding_from, winding_to, clock, tap_side, tap_pos, tap_min, tap_max, tap_nom, tap_size,
                uk_min, uk_max, pk_min, pk_max, r_grounding_from, x_grounding_from, r_grounding_to, x_grounding_to);
    }

    public void finalizeConstruction() {
        finalizeConstruction(pgmPtr);
    }

    public void runPf() {
        runPf(pgmPtr);

    }

    // Delete C++ object on cleanup
    public void cleanup() {
        deleteCppObject(pgmPtr);
        pgmPtr = 0;
    }

    public void getResult(Result result) {
        getResult(pgmPtr, result);
    }

    // Native methods
    private native long createCppObject();

    private native void deleteCppObject(long cppHandler);

    private native void addNode(long cppHandler, int Id, double u_rated);

    private native void addSource(long cppHandler, int Id, int nodeId, boolean isConnected, double u_ref,
            double u_ref_angle, double sk, double rx_ratio, double z01_ratio);

    private native void addSymLoadGenInput(long cppHandler, int Id, int nodeId, boolean isConnected, int type, double p,
            double q);

    private native void addLine(long cppHandler, int Id,
            int nodeFromId, boolean isFromConnected, int nodeToId, boolean isToConnected,
            double r1, double x1, double c1, double tan1,
            double r2, double x2, double c2, double tan2, double ratedCurrent);

    private native void addTransformer(long cppHandler, int Id,
            int nodeFromId, boolean isFromConnected, int nodeToId, boolean isToConnected,
            double u1, double u2, double sn, double uk, double pk, double i0, double p0,
            int winding_from, int winding_to, byte clock,
            int tap_side, byte tap_pos, byte tap_min, byte tap_max, byte tap_nom, double tap_size,
            double uk_min, double uk_max,
            double pk_min, double pk_max,
            double r_grounding_from, double x_grounding_from, double r_grounding_to, double x_grounding_to);

    private native void finalizeConstruction(long cppHandler);

    private native void runPf(long cppHandler);

    private native void getResult(long cppHandler, Result result);
}