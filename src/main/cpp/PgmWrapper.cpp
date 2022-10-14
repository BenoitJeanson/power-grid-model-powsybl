#include "PgmWrapper.h"

using namespace power_grid_model;

template <bool sym>
Result<sym>::Result(int nb_nodes, int nb_branches, int nb_appliances)
{
    m_nodes= new std::vector<NodeOutput<sym>>(nb_nodes);
    m_branches = new std::vector<BranchOutput<sym>>(nb_branches);
    m_appliances = new std::vector<ApplianceOutput<sym>>(nb_appliances);
}

template <bool sym>
Result<sym>::~Result()
{
    delete m_nodes;
    delete m_branches;
    delete m_appliances;
}

template <bool sym>
void Result<sym>::print()
{
    for (auto n : *m_nodes)
    {
        std::cout << "node output: id: " << n.id << "\tu_pu: " << n.u_pu << "\tu: " << n.u << "u_angle: " << n.u_angle << "\n";
    }
    for (auto br : *m_branches)
    {
        std::cout << "branch: id: " << br.id << "\tloading:" << br.loading << "\tp_from: " << br.p_from << "\tp_to: " << br.p_to << "\n";
    }
}

PgmWrapper::PgmWrapper() : m_main_model(MainModel(50.0)),
                           m_is_sym(true)
{
}

PgmWrapper::~PgmWrapper()
{
}

void PgmWrapper::add_node(NodeInput node)
{
    m_nodes.push_back(node);
}

void PgmWrapper::add_source(SourceInput source)
{
    m_sources.push_back(source);
}

void PgmWrapper::add_sym_load_gen(SymLoadGenInput load_gen)
{
    m_sym_load_gens.push_back(load_gen);
}

void PgmWrapper::add_line(LineInput line)
{
    m_lines.push_back(line);
}

void PgmWrapper::add_transformer(TransformerInput transformer)
{
    m_transformers.push_back(transformer);
}


void PgmWrapper::finalize_construction()
{
    m_main_model.add_component<Node>(m_nodes.cbegin(), m_nodes.cend());
    m_main_model.add_component<Source>(m_sources.cbegin(), m_sources.cend());
    m_main_model.add_component<SymLoad>(m_sym_load_gens.cbegin(), m_sym_load_gens.cend());
    // m_main_model.add_component<AsymLoad>(asym_load_input.cbegin(), asym_load_input.cend());
    // m_main_model.add_component<Transformer>(transformer_input.cbegin(), transformer_input.cend());
    m_main_model.add_component<Line>(m_lines.cbegin(), m_lines.cend());
    m_main_model.add_component<Transformer>(m_transformers.cbegin(), m_transformers.cend());

    m_main_model.set_construction_complete();
}

void print(CalculationInfo const &info)
{
    for (auto const &[key, val] : info)
    {
        std::cout << key << ": " << val << '\n';
    }
}

void print_math_output(std::vector<MathOutput<true>> mov)
{
    for (auto mo : mov)
    {
        std::cout << "Math Output -> u\n";
        for (auto u : mo.u)
        {
            std::cout << "\tu.real: " << u.real() << ", u.imag: " << u.imag() << "\n";
        }
    }
}

template <bool sym>
Result<sym> PgmWrapper::retrieve_results(std::vector<MathOutput<sym>> const &math_output)
{
    Result<sym> result(m_nodes.size(), m_lines.size(), m_sources.size() + m_sym_load_gens.size());
    std::vector<NodeOutput<sym>> m_nodes_res(m_nodes.size());
    m_main_model.output_result<sym, Node>(math_output, result.m_nodes->begin());
    m_main_model.output_result<sym, Branch>(math_output, result.m_branches->begin());
    m_main_model.output_result<sym, Appliance>(math_output, result.m_appliances->begin());
    return result;
}

template <bool sym>
void PgmWrapper::run_pf(CalculationMethod calculation_method, CalculationInfo &info)
{
    auto math_output = m_main_model.calculate_power_flow<sym>(1e-8, 20, calculation_method);

    CalculationInfo info_extra = m_main_model.calculation_info();
    info.merge(info_extra);
    auto result = retrieve_results<sym>(math_output);
    result.print();

    print_math_output(math_output);

    print(info);
}

void PgmWrapper::run_pf(CalculationMethod calculation_method, CalculationInfo &info)
{
    // if (m_is_sym)
    // {
    run_pf<true>(calculation_method, info);
    // }
    // else
    // {
    //     run_pf<false>(calculation_method, info);
    // }
}

std::string PgmWrapper::to_String()
{
    return "component_count:\n\t- Nodes: " + std::to_string(m_main_model.component_count<Node>()) +
           "\n\t- Sources: " + std::to_string(m_main_model.component_count<Source>()) +
           "\n\t- SymLoads: " + std::to_string(m_main_model.component_count<SymLoad>()) +
           "\n\t- Lines: " + std::to_string(m_main_model.component_count<Line>()) + "\n";
}
