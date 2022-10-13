#include "gpm_test.h"
#include "power_grid_model/main_model.hpp"
#include "power_grid_model/timer.hpp"

void build_network()
{
    power_grid_model::LineInput const line{{{0}, 0, 0, true, true}, 0.063, 0.103, 0.4e-6, 0.0, 0.156, 0.1, 0.66e-6, 0.0, 1e3};
}
