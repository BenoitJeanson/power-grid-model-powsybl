package com.powsybl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Country;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.TopologyKind;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.pgm.PgmLoadFlowProvider;
import com.powsybl.pgm.PgmWrapper;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;

/**
 * Unit test for simple App.
 */
public class TestPowSyBlPgm {
        Network network;
        PgmWrapper pgmw;

        private VoltageLevel createVlBus(Network network, int index, double nominalV) {

                Substation ss = network.newSubstation()
                                .setId("ss" + index)
                                .setName("ss" + index)
                                .setCountry(Country.FR)
                                .add();
                return ss.newVoltageLevel()
                                .setId("vl" + index)
                                .setName("vl" + index)
                                .setTopologyKind(TopologyKind.BUS_BREAKER)
                                .setNominalV(nominalV)
                                .add();
        }

        private void createLine(Network network, String id, String vl1, String vl2, String bus1, String bus2,
                        double r, double x, double b) {
                network.newLine().setId(id)
                                .setVoltageLevel1(vl1).setBus1(bus1)
                                .setVoltageLevel2(vl2).setBus2(bus2)
                                .setR(r).setX(x).setB1(b).setB2(b).setG1(0).setG2(0)
                                .add();
        }

        @Before
        public void createCase() {
                pgmw = new PgmWrapper();

                network = Network.create("testCase1", "test");
                VoltageLevel vl1 = createVlBus(network, 1, 10.5e3);
                vl1.getBusBreakerView().newBus().setId("bus1").add();

                VoltageLevel vl2 = createVlBus(network, 2, 10.5e3);
                vl2.getBusBreakerView().newBus().setId("bus2").add();

                VoltageLevel vl6 = createVlBus(network, 6, 10.5e3);
                vl6.getBusBreakerView().newBus().setId("bus6").add();

                vl2.newLoad().setId("loadd4").setBus("bus2")
                                .setP0(20).setQ0(5).add();

                vl6.newLoad().setId("load7").setBus("bus6")
                                .setP0(10).setQ0(2).add();

                createLine(network, "line3", "vl1", "vl2", "bus1", "bus2", 0.25, 0.2, 1e5);
                createLine(network, "line5", "vl2", "vl6", "bus2", "bus6", 0.25, 0.2, 1e5);
                createLine(network, "line8", "vl1", "vl6", "bus1", "bus6", 0.25, 0.2, 1e5);

                vl1.newGenerator().setId("gen10").setName("gen10").setBus("bus1")
                                .setMinP(0).setMaxP(50).setVoltageRegulatorOn(true)
                                .setTargetV(10.5e3).setTargetP(30).add();

                PgmLoadFlowProvider pgmLfP = new PgmLoadFlowProvider();
                pgmLfP.run(network);
        }

        void assertBus(String busId, double u, double uAngle) {
                Bus bus = network.getBusBreakerView().getBus(busId);
                assertEquals(u, bus.getV(), 1e-12);
                assertEquals(uAngle, bus.getAngle(), 1e-18);
        }

        void assertLine(String lineId, double pFrom, double qFrom, double pTo, double qTo) {
                Line line = network.getLine(lineId);
                assertEquals(pFrom, line.getTerminal1().getP(), 1e-8);
                assertEquals(qFrom, line.getTerminal1().getQ(), 1e-8);
                assertEquals(pTo, line.getTerminal2().getP(), 1e-8);
                assertEquals(qTo, line.getTerminal2().getQ(), 1e-8);
        }

        void assertGenerator(String generatorId, double p, double q) {
                Generator gen = network.getGenerator(generatorId);
                assertEquals(p, gen.getTerminal().getP(), 1e-8);
                assertEquals(q, gen.getTerminal().getQ(), 1e-8);
        }

        @Test
        public void test() {
                assertBus("bus1", 10489.375043450817, -0.003039473910333988);
                assertBus("bus2", 9997.325180546857, -0.026030794628270806);
                assertBus("bus6", 10102.012975318361, -0.021894763760298454);

                assertLine("line3",
                                1.736010020222369E7, 4072096.644186402,  -1.6634386255498417E7, -3821351.088651515);
                assertLine("line5",
                                -3365613.7445015097, -1178648.9113484, 3396558.124766807, 886107.9920331766);
                assertLine("line8",
                                1.385441352498137E7, 2919261.5108051673, -1.3396558124766812E7, -2886107.9920331817);

                assertGenerator("gen10", 3.1214513727205254E7, 6991358.15499088);

                assertTrue(true);
        }
}
