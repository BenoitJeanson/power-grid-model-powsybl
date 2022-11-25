#include "PgmWrapper.h"

using namespace power_grid_model;

template <bool sym>
Result<sym>::Result(int nb_nodes, int nb_branches, int nb_appliances)
{
    m_nodes = new std::vector<NodeOutput<sym>>(nb_nodes);
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
    m_main_model.output_result<sym, Node>(math_output, result.m_nodes->begin());
    m_main_model.output_result<sym, Branch>(math_output, result.m_branches->begin());
    m_main_model.output_result<sym, Appliance>(math_output, result.m_appliances->begin());
    return result;
}

void PgmWrapper::sym_result_to_java(std::function<void(int id, double u_pu, double u, double u_angle)> eat_node,
                                    std::function<void(int id, double p_from, double q_from,
                                                       double i_from, double s_from, double p_to, double q_to,
                                                       double i_to, double s_to)>
                                        eat_branch,
                                    std::function<void(int id, double p, double q, double i, double s, double pf)> eat_appliance)

{
    Result<true> result = retrieve_results<true>(sym_math_output);
    for (auto n : *result.m_nodes)
        eat_node(n.id, n.u_pu, n.u, n.u_angle);

    for (auto br : *result.m_branches)
        eat_branch(br.id, br.p_from, br.q_from, br.i_from, br.s_from, br.p_to, br.q_to, br.i_to, br.s_to);

    for (auto ap : *result.m_appliances)
        eat_appliance(ap.id, ap.p, ap.q, ap.i, ap.s, ap.pf);
}

template <bool sym>
void PgmWrapper::run_pf(CalculationMethod calculation_method, CalculationInfo &info)
{
    if (m_is_sym)
        sym_math_output = m_main_model.calculate_power_flow<true>(1e-8, 20, calculation_method);
    else
        asym_math_output = m_main_model.calculate_power_flow<false>(1e-8, 20, calculation_method);

    CalculationInfo info_extra = m_main_model.calculation_info();
    info.merge(info_extra);
    auto result = retrieve_results<sym>(sym_math_output);
    result.print();

    if (m_is_sym)
        print_math_output(sym_math_output);

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
