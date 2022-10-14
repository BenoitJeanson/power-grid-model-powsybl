#ifndef PGM_WRAPPER_H
#define PGM_WRAPPER_H

#include "com_powsybl_pgm_PgmWrapper.h"
#include "power_grid_model/main_model.hpp"
#include "power_grid_model/enum.hpp"
#include <iostream>

using namespace power_grid_model;

template <bool sym>
class Result
{
public:
    Result(int nb_nodes, int nb_branches, int nb_appliances);
    ~Result();
    std::vector<NodeOutput<sym>> get_nodes() { return m_nodes; }
    std::vector<BranchOutput<sym>> get_branches() { return *m_branches; }
    std::vector<ApplianceOutput<sym>> get_appliances() { return *m_appliances; }
    void print();

    // private:
    std::vector<NodeOutput<sym>> *m_nodes;
    std::vector<BranchOutput<sym>> *m_branches;
    std::vector<ApplianceOutput<sym>> *m_appliances;
};

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
    void add_transformer(TransformerInput);
    void finalize_construction();

    template <bool sym>
    Result<sym> retrieve_results(std::vector<MathOutput<sym>> const &math_output);

    template <bool sym>
    void run_pf(CalculationMethod, CalculationInfo &);

    void run_pf(CalculationMethod, CalculationInfo &);

private:
    MainModel m_main_model;
    // input vector
    std::vector<NodeInput> m_nodes;
    std::vector<SourceInput> m_sources;
    std::vector<SymLoadGenInput> m_sym_load_gens;

    bool m_is_sym;

    // std::vector<AsymLoadGenInput> asym_load_input;
    // std::vector<TransformerInput> transformer_input;
    std::vector<LineInput> m_lines;
    std::vector<TransformerInput> m_transformers;
    // std::vector<LinkInput> link_input;
};

#endif