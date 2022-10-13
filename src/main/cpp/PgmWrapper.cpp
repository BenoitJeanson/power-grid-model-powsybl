#include "PgmWrapper.h"

using namespace power_grid_model;

PgmWrapper::PgmWrapper() : m_main_model(MainModel(50.0))
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

void PgmWrapper::finalize_construction()
{
    m_main_model.add_component<Node>(m_nodes.cbegin(), m_nodes.cend());
    m_main_model.add_component<Source>(m_sources.cbegin(), m_sources.cend());
    m_main_model.add_component<SymLoad>(m_sym_load_gens.cbegin(), m_sym_load_gens.cend());
    // m_main_model.add_component<AsymLoad>(asym_load_input.cbegin(), asym_load_input.cend());
    // m_main_model.add_component<Transformer>(transformer_input.cbegin(), transformer_input.cend());
    m_main_model.add_component<Line>(m_lines.cbegin(), m_lines.cend());

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
void PgmWrapper::run_pf(CalculationMethod calculation_method, CalculationInfo &info)
{
    auto const m_math_output = m_main_model.calculate_power_flow<sym>(1e-8, 20, calculation_method);

    std::vector<NodeOutput<sym>> m_nodes_res(m_nodes.size());
    // std::vector<BranchOutput<sym>> branch(line_input.size() + transformer_input.size() + link_input.size());
    std::vector<BranchOutput<sym>> m_branches_res(m_lines.size());
    // std::vector<ApplianceOutput<sym>> appliance(source_input.size() + sym_load_input.size() +
    //                                             asym_load_input.size());
    std::vector<ApplianceOutput<sym>> m_appliance_res(m_sources.size() + m_sym_load_gens.size());

    {
        m_main_model.output_result<sym, Node>(m_math_output, m_nodes_res.begin());
        m_main_model.output_result<sym, Branch>(m_math_output, m_branches_res.begin());
        m_main_model.output_result<sym, Appliance>(m_math_output, m_appliance_res.begin());
    }
    CalculationInfo info_extra = m_main_model.calculation_info();
    info.merge(info_extra);
    auto const [min_l, max_l] = std::minmax_element(m_branches_res.cbegin(), m_branches_res.cend(),
                                                    [](auto x, auto y)
                                                    { return x.loading < y.loading; });
    std::cout << "Min loading: " << min_l->loading << ", max loading: " << max_l->loading << "\n\n";

    for (auto n : m_nodes_res)
    {
        std::cout << "node output: id: " << n.id << "\tu_pu: " << n.u_pu << "\tu: " << n.u << "u_angle: " << n.u_angle << "\n";
    }
    for (auto br : m_branches_res)
    {
        std::cout << "branch: id: " << br.id << "\tloading:" << br.loading << "\tp_from: " << br.p_from << "\tp_to: " << br.p_to << "\n";
    }

    print_math_output(m_math_output);

    print(info);
}

void PgmWrapper::run_pf_sym(CalculationMethod calculation_method, CalculationInfo &info)
{
    run_pf<true>(calculation_method, info);
}

std::string PgmWrapper::to_String()
{
    return "component_count:\n\t- Nodes: " + std::to_string(m_main_model.component_count<Node>()) +
           "\n\t- Sources: " + std::to_string(m_main_model.component_count<Source>()) +
           "\n\t- SymLoads: " + std::to_string(m_main_model.component_count<SymLoad>()) +
           "\n\t- Lines: " + std::to_string(m_main_model.component_count<Line>()) + "\n";
}
