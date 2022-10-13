package com.powsybl.pgm;

public class PgmWrapper {

    // Load C++ shared library
    static {
        System.loadLibrary("pgm");
    }

    private long pgmPtr = 0;

    public PgmWrapper() {
        pgmPtr = createCppObject();
    }

    public void workOnCppObject() {
        workOnCppObject(pgmPtr);
    }

    public void addNode(int Id, double u_rated) {
        addNode(pgmPtr, Id, u_rated);
    }

    public void addSource(int Id, int nodeId, boolean isConnected, double u_ref) {
        addSource(pgmPtr, Id, nodeId, isConnected, u_ref);
    }

    public void addSymLoaGendInput(int Id, int nodeId, boolean isConnected, int type, double p, double q) {
        addSymLoaGendInput(pgmPtr, Id, nodeId, isConnected, type, p, q);
    }

    public void addLine(int Id, int nodeFromId, boolean isFromConnected, int nodeToId,
            boolean isToConnected, double r1, double x1, double c1, double tan1, double ratedCurrent) {
        addLine(pgmPtr, Id, nodeFromId, isFromConnected, nodeToId, isToConnected, r1, x1, c1, tan1, ratedCurrent);
    }

    public void finalizeConstruction() {
        finalizeConstruction(pgmPtr);
    }

    public void runPf(){
        runPf(pgmPtr);

    }

    // Delete C++ object on cleanup
    public void cleanup() {
        deleteCppObject(pgmPtr);
        pgmPtr = 0;
    }

    // Native methods
    private native long createCppObject();

    private native void workOnCppObject(long cppHandler);

    private native void deleteCppObject(long cppHandler);

    private native void addNode(long cppHandler, int Id, double u_rated);

    private native void addSource(long cppHandler, int Id, int nodeId, boolean isConnected, double u_ref);

    private native void addSymLoaGendInput(long cppHandler, int Id, int nodeId, boolean isConnected, int type, double p,
            double q);

    private native void addLine(long cppHandler, int Id, int nodeFromId, boolean isFromConnected, int nodeToId,
            boolean isToConnected, double r1, double x1, double c1, double tan1, double ratedCurrent);

    private native void finalizeConstruction(long cppHandler);

    private native void runPf(long cppHandler);

}