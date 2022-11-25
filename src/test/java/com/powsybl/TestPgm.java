package com.powsybl;

import static org.junit.Assert.assertEquals;

import com.powsybl.pgm.PgmWrapper;
import com.powsybl.pgm.ResultRawImpl;

import org.junit.Test;
import org.junit.Before;

/**
 * Unit test for simple App.
 */
public class TestPgm {
        PgmWrapper pgmw;

        @Before
        public void createCase() {
                pgmw = new PgmWrapper();
                pgmw.addNode(1, 10.5e3);
                pgmw.addNode(2, 10.5e3);
                pgmw.addNode(6, 10.5e3);

                pgmw.addLine(3, 1, true, 2, true, 0.25, 0.2, 10e-6, 0.0, 1e3);
                pgmw.addLine(5, 2, true, 6, true, 0.25, 0.2, 10e-6, 0.0, 1e3);
                pgmw.addLine(8, 1, true, 6, true, 0.25, 0.2, 10e-6, 0.0, 1e3);

                pgmw.addLoad(4, 2, true, 20e6, 5e6);
                pgmw.addLoad(7, 6, true, 10e6, 2e6);

                pgmw.addSource(10, 1, true, 1.0);
                pgmw.finalizeConstruction();

        }

        void assertNode(ResultRawImpl.Node n, int id, double uPu, double u, double uAngle) {
                assertEquals(id, n.getId());
                assertEquals(uPu, n.getUPu(), 1e-15);
                assertEquals(u, n.getU(), 1e-12);
                assertEquals(uAngle, n.getuAngle(), 1e-18);
        }

        void assertBranch(ResultRawImpl.Branch b, int id, double pFrom, double qFrom, double iFrom, double sFrom,
                        double pTo, double qTo, double iTo, double sTo) {
                assertEquals(id, b.getId());
                assertEquals(pFrom, b.getPFrom(), 1e-8);
                assertEquals(qFrom, b.getQFrom(), 1e-8);
                assertEquals(iFrom, b.getIFrom(), 1e-8);
                assertEquals(sFrom, b.getSFrom(), 1e-8);
                assertEquals(pTo, b.getPTo(), 1e-8);
                assertEquals(qTo, b.getQTo(), 1e-8);
                assertEquals(iTo, b.getITo(), 1e-8);
                assertEquals(sTo, b.getSTo(), 1e-8);
        }

        void assertAppliance(ResultRawImpl.Appliance a, int id, double p, double q, double i, double s, double pf) {
                assertEquals(id, a.getId());
                assertEquals(p, a.getP(), 1e-8);
                assertEquals(q, a.getQ(), 1e-8);
                assertEquals(i, a.getI(), 1e-8);
                assertEquals(s, a.getS(), 1e-8);
                assertEquals(pf, a.getPf(), 1e-8);
        }

        @Test
        public void Test() {
                pgmw.runPf();
                ResultRawImpl result = new ResultRawImpl();
                pgmw.getResult(result);
                assertEquals(3, result.getNodes().size());
                assertNode(result.getNodes().get(0), 1, 0.9989880993762683, 10489.375043450817, -0.003039473910333988);
                assertNode(result.getNodes().get(1), 2, 0.9521262076711293, 9997.325180546857, -0.026030794628270806);
                assertNode(result.getNodes().get(2), 6, 0.962096473839844, 10102.012975318361, -0.021894763760298454);

                assertEquals(3, result.getBranches().size());
                assertBranch(result.getBranches().get(0), 3,
                                1.736010020222369E7, 4072096.644186402, 981.4600411777236, 1.783129412327779E7,
                                -1.6634386255498417E7, -3821351.088651515, 985.6663240801984, 1.7067675009791248E7);
                assertBranch(result.getBranches().get(1), 5,
                                -3365613.7445015097, -1178648.9113484, 205.9399165545992, 3566029.883974648,
                                3396558.124766807, 886107.9920331766, 200.61732292860077, 3510241.3689751416);
                assertBranch(result.getBranches().get(2), 8,
                                1.385441352498137E7, 2919261.5108051673, 779.3114460061568, 1.4158632062796714E7,
                                -1.3396558124766812E7, -2886107.9920331817, 783.2063960833023, 1.3703918743627075E7);

                assertEquals(3, result.getAppliances().size());
                assertAppliance(result.getAppliances().get(0), 10,
                                3.1214513727205254E7, 6991358.15499088, 1760.6592830308023, 3.1987887646377068E7,
                                0.975822913731554);
                assertAppliance(result.getAppliances().get(1), 4,
                                2.0E7, 5000000.0, 1190.5565237988008, 2.0615528128088303E7, 0.9701425001453319);
                assertAppliance(result.getAppliances().get(2), 7,
                                1.0E7, 2000000.0, 582.8383503305039, 1.0198039027185569E7, 0.9805806756909202);

                pgmw.cleanup();
        }
}
