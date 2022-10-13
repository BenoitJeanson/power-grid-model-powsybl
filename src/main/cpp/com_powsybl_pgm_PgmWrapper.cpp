#include "com_powsybl_pgm_PgmWrapper.h"
#include "PgmWrapper.h"

using namespace power_grid_model;

JNIEXPORT jlong JNICALL Java_com_powsybl_pgm_PgmWrapper_createCppObject(JNIEnv *, jobject)
{
  return (jlong) new PgmWrapper();
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_workOnCppObject(JNIEnv *, jobject, jlong handler)
{
  std::cout << ((PgmWrapper *)handler)->to_String();
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_deleteCppObject(JNIEnv *, jobject, jlong handler)
{
  delete ((PgmWrapper *)handler);
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_addNode(
    JNIEnv *, jobject, jlong handler, jint id, jdouble u_rated)
{
  ((PgmWrapper *)handler)->add_node(NodeInput({{id}, u_rated}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_addSource(
    JNIEnv *, jobject, jlong handler, jint id, jint nodeId, jboolean isConnected, jdouble u_ref)
{
  ((PgmWrapper *)handler)->add_source(SourceInput({{{id}, nodeId, (bool)isConnected}, u_ref, 0, 1e10, 0.1, 1.0}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_addSymLoaGendInput(
    JNIEnv *, jobject, jlong handler, jint id, jint nodeId, jboolean isConnected, jint type, jdouble p, jdouble q)
{
  ((PgmWrapper *)handler)->add_sym_load_gen(SymLoadGenInput({{{{id}, nodeId, (bool)isConnected}, LoadGenType::const_pq}, p, q}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_addLine(
    JNIEnv *, jobject, jlong handler, jint id, jint nodeFromId, jboolean isFromConnected, jint nodeToId, jboolean isToConnected,
    jdouble r1, jdouble x1, jdouble c1, jdouble tan1, jdouble rated_current)
{
  ((PgmWrapper *)handler)->add_line(LineInput({{{id}, nodeFromId, nodeToId, (bool)isFromConnected, (bool)isToConnected}, r1, x1, c1, tan1, r1, x1, c1, tan1, rated_current}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_finalizeConstruction
  (JNIEnv *, jobject, jlong handler)
  {
    ((PgmWrapper *)handler)->finalize_construction();

  }

  JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_runPf
  (JNIEnv *, jobject, jlong handler)
  {
    auto info = CalculationInfo();
    ((PgmWrapper *)handler)->run_pf_sym(CalculationMethod::newton_raphson, info);
  }
