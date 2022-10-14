# SPDX-FileCopyrightText: 2022 Contributors to the Power Grid Model project <dynamic.grid.calculation@alliander.com>
#
# SPDX-License-Identifier: MPL-2.0

# CMakeList.txt : CMake project for power_grid_model, include source and define
# project specific logic here.
#
cmake_minimum_required (VERSION 3.9)
project (pgm)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(EIGEN_INCLUDE_DIRS "/home/linuxbrew/.linuxbrew/Cellar/eigen/3.4.0_1/include/eigen3")
set(CMAKE_BUILD_TYPE "Debug")

find_package(Eigen3 CONFIG REQUIRED)
include_directories(${EIGEN_INCLUDE_DIRS})
find_package(Boost REQUIRED)

# add_library(${PROJECT_NAME} INTERFACE)
# add_library(${PROJECT_NAME} SHARED src/gpm_test.cpp)
add_library(${PROJECT_NAME} SHARED src/main/cpp/com_powsybl_pgm_PgmWrapper.cpp src/main/cpp/PgmWrapper.cpp)
# add_executable(${PROJECT_NAME} src/main/cpp/play.cpp src/main/cpp/com_powsybl_pgm_PgmWrapper.cpp src/main/cpp/PgmWrapper.cpp)
target_link_libraries(${PROJECT_NAME} INTERFACE Eigen3::Eigen)
include_directories(include include/power_grid_model)
include_directories(src/main/cpp)
include_directories(${Boost_INCLUDE_DIRS})
include_directories($ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/linux)
# only use MPL version of eigen
target_compile_definitions(${PROJECT_NAME} INTERFACE EIGEN_MPL2_ONLY=1)
target_compile_features(${PROJECT_NAME} INTERFACE cxx_std_11)
# header only library
target_include_directories(${PROJECT_NAME} INTERFACE ${CMAKE_CURRENT_SOURCE_DIR}/include)

if(WIN32)
  add_compile_options(/utf-8 /WX /bigobj /FC)
else()
  add_compile_options(-Wall -Wextra -pedantic -Werror)
  add_compile_options(-Wno-deprecated-copy)  # bug in boost
  if(UNIX AND NOT APPLE)
	# test coverage for debug build in linux, if specified
	if ((CMAKE_BUILD_TYPE STREQUAL "Debug") AND (DEFINED POWER_GRID_MODEL_COVERAGE) AND (POWER_GRID_MODEL_COVERAGE EQUAL 1))
	  add_compile_options(-fprofile-arcs -ftest-coverage)
	  add_link_options(-fprofile-arcs)
	endif()
  else()
  endif()
  # thread
  find_package(Threads REQUIRED)
  target_link_libraries(${PROJECT_NAME} INTERFACE Threads::Threads)
endif()

if (CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
  # using GCC
  add_compile_options(-Wno-maybe-uninitialized)  # bug in boost
endif()

