#ifndef PGM_WRAPPER_H
#define PGM_WRAPPER_H

#include "com_powsybl_pgm_PgmWrapper.h"
#include "power_grid_model/main_model.hpp"
#include "power_grid_model/enum.hpp"
#include <iostream>

using namespace power_grid_model;

class PgmWrapper
{
public:
    PgmWrapper();
    ~PgmWrapper();
    std::string to_String();
    void add_node(NodeInput);
    void add_source(SourceInput);
    void add_sym_load_gen(SymLoadGenInput);
    void add_line(LineInput);
    void finalize_construction();
    
    template <bool sym>
    void run_pf(CalculationMethod, CalculationInfo&);

    void run_pf_sym(CalculationMethod, CalculationInfo&);


private:
    MainModel m_main_model;
    // input vector
    std::vector<NodeInput> m_nodes;
    std::vector<SourceInput> m_sources;
    std::vector<SymLoadGenInput> m_sym_load_gens;
    
    // std::vector<AsymLoadGenInput> asym_load_input;
    // std::vector<TransformerInput> transformer_input;
    std::vector<LineInput> m_lines;
    // std::vector<LinkInput> link_input;

};

#endif