package com.powsybl.pgm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.computation.ComputationManager;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Terminal;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowProvider;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;

public class PgmLoadFlowProvider implements LoadFlowProvider {

    enum assetType {
        BUS, LINE, LOAD, GEN
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowProvider.class);

    @Override
    public String getName() {
        return "PgmLoadFlow";
    }

    @Override
    public String getVersion() {
        return "0.01";
    }

    @Override
    public CompletableFuture<LoadFlowResult> run(Network network, ComputationManager computationManager,
            String workingVariantId, LoadFlowParameters parameters) {

        return CompletableFuture.supplyAsync(() -> {

            // network.getVariantManager().setWorkingVariant(workingVariantId);

            // Stopwatch stopwatch = Stopwatch.createStarted();
            run(network);
            LoadFlowResult lfResult = new LoadFlowResultImpl(true, new HashMap<String, String>(), "");

            // LoadFlowResult result = parameters.isDc() ? runDc(network, parameters,
            // lfReporter)
            // : runAc(network, parameters, lfReporter);

            // stopwatch.stop();
            // LOGGER.info(Markers.PERFORMANCE_MARKER, "Load flow ran in {} ms",
            // stopwatch.elapsed(TimeUnit.MILLISECONDS));

            return lfResult;

        }, computationManager.getExecutor());
    }

    public void run(Network network) {
        PgmMap pgmMap = new PgmMap();
        PgmWrapper pgmw = buildGridModel(network, pgmMap);
        pgmw.runPf();
        PowsyblResult result = new PowsyblResult(pgmMap);
        pgmw.getResult(result);
    }

    private int terminalToPgmNodeId(Map<String, Integer> busIdToNodeId, Terminal term) {
        return busIdToNodeId.get(term.getBusView().getBus().getId());
    }

    public PgmWrapper buildGridModel(Network network, PgmMap pgmMap) {
        PgmWrapper pgmw = new PgmWrapper();
        int i = 0;
        Map<String, Integer> busIdToPgmNodeId = new HashMap<>();
        for (Bus b : network.getBusView().getBusStream().collect(Collectors.toList())) {
            i++;
            pgmw.addNode(i, b.getVoltageLevel().getNominalV());
            pgmMap.put(i, b);
            busIdToPgmNodeId.put(b.getId(), i);
        }
        for (Line l : network.getLineStream().collect(Collectors.toList())) {
            i++;
            // TODO: investigate rated current
            pgmw.addLine(i, terminalToPgmNodeId(busIdToPgmNodeId, l.getTerminal1()), true,
                    terminalToPgmNodeId(busIdToPgmNodeId, l.getTerminal2()), true,
                    l.getR(), l.getX(), 1 / l.getB1(), 0, 1e3);
            pgmMap.put(i, l);
        }
        for (Load ld : network.getLoadStream().collect(Collectors.toList())) {
            i++;
            pgmw.addLoad(i, terminalToPgmNodeId(busIdToPgmNodeId, ld.getTerminal()), true,
                    ld.getP0() * 1e6, ld.getQ0() * 1e6);
        }
        for (Generator gen : network.getGeneratorStream().collect(Collectors.toList())) {
            i++;
            // TODO: investigate u_pu for source
            pgmw.addSource(i, terminalToPgmNodeId(busIdToPgmNodeId, gen.getTerminal()), true, 1.0);
            pgmMap.put(i, gen);

        }

        pgmw.finalizeConstruction();

        return pgmw;
    }

    class PgmMap {
        Map<Integer, Bus> pgmId2bus = new HashMap<>();
        Map<Integer, Line> pgmId2line = new HashMap<>();
        Map<Integer, Generator> pgmId2gen = new HashMap<>();

        void put(Integer id, Line line) {
            pgmId2line.put(id, line);
        }

        void put(Integer id, Bus bus) {
            pgmId2bus.put(id, bus);
        }

        void put(Integer id, Generator gen) {
            pgmId2gen.put(id, gen);
        }

        Bus getBus(int id) {
            return pgmId2bus.get(id);
        }

        Line getLine(int id) {
            return pgmId2line.get(id);
        }

        Generator getGenerator(int id) {
            return pgmId2gen.get(id);
        }
    }

    class PowsyblResult implements Result {
        PgmMap pgmMap;

        PowsyblResult(PgmMap pgmMap) {
            this.pgmMap = pgmMap;
        }

        @Override
        public void toNode(int id, double uPu, double u, double uAngle) {
            pgmMap.getBus(id).setV(u).setAngle(uAngle);

        }

        @Override
        public void toBranch(int id, double pFrom, double qFrom, double iFrom, double sFrom, double pTo, double qTo,
                double iTo, double sTo) {
            Line line = pgmMap.getLine(id);
            line.getTerminal1().setP(pFrom).setQ(qFrom);
            line.getTerminal2().setP(pTo).setQ(qTo);
            // TODO Auto-generated method stub

        }

        @Override
        public void toAppliance(int id, double p, double q, double i, double s, double pf) {
            // TODO Auto-generated method stub

            Generator gen = pgmMap.getGenerator(id);
            if (gen != null) {
                gen.getTerminal().setP(p).setQ(q);
            }

        }

    }
}
