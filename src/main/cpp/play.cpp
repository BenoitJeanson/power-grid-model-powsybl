#include "PgmWrapper.h"
#include <iostream>

int main()
{
    PgmWrapper pgmw;

    //   add_node(power_grid_model::NodeInput({{id}, u_rated}));
    pgmw.add_node(power_grid_model::NodeInput({{1}, 10.5e3}));
    pgmw.add_node(power_grid_model::NodeInput({{2}, 10.5e3}));
    pgmw.add_node(power_grid_model::NodeInput({{6}, 10.5e3}));

    //   add_line(power_grid_model::LineInput({{{id}, nodeFromId, nodeToId, (bool)isFromConnected, (bool)isToConnected}, r1, x1, c1, tan1, r1, x1, c1, tan1, rated_current}));
    pgmw.add_line(power_grid_model::LineInput({{{3}, 1, 2, true, true}, 0.25, 0.2, 10e-6, 0.0, 0.25, 0.2, 10e-6, 0.0, 1e3}));
    pgmw.add_line(power_grid_model::LineInput({{{5}, 2, 6, true, true}, 0.25, 0.2, 10e-6, 0.0, 0.25, 0.2, 10e-6, 0.0, 1e3}));
    pgmw.add_line(power_grid_model::LineInput({{{8}, 1, 6, true, true}, 0.25, 0.2, 10e-6, 0.0, 0.25, 0.2, 10e-6, 0.0, 1e3}));

    //   add_sym_load_gen(power_grid_model::SymLoadGenInput({{{{id}, nodeId, (bool)isConnected}, power_grid_model::LoadGenType::const_pq}, p, q}));
    pgmw.add_sym_load_gen(power_grid_model::SymLoadGenInput({{{{4}, 2, true}, power_grid_model::LoadGenType::const_pq}, 20e6, 5e6}));
    pgmw.add_sym_load_gen(power_grid_model::SymLoadGenInput({{{{7}, 6, true}, power_grid_model::LoadGenType::const_pq}, 10e6, 5e6}));

    //   add_source(power_grid_model::SourceInput({{{id}, nodeId, (bool)isConnected}, u_ref, 0, 0, 0, 0}));
    pgmw.add_source(power_grid_model::SourceInput({{{10}, 1, true}, 1.0, 0, 1e10, 0.1, 1.0}));

    pgmw.finalize_construction();
    auto info = power_grid_model::CalculationInfo();

    pgmw.run_pf_sym(power_grid_model::CalculationMethod::newton_raphson, info);

    std::cout << "toString" << pgmw.to_String();
}
