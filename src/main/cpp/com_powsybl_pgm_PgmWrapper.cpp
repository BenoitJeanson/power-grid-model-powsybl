#include "com_powsybl_pgm_PgmWrapper.h"
#include "PgmWrapper.h"
#include <iostream>

using namespace power_grid_model;

JNIEXPORT jlong JNICALL Java_com_powsybl_pgm_PgmWrapper_createCppObject(JNIEnv *env, jobject java_wrapper)
{
  return (jlong) new PgmWrapper();
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
    JNIEnv *, jobject, jlong handler, jint id, jint nodeId, jboolean isConnected, jdouble u_ref, jdouble u_ref_angle, jdouble sk, jdouble rx_ratio, jdouble z01_ratio)
{
  ((PgmWrapper *)handler)->add_source(SourceInput({{{id}, nodeId, (bool)isConnected}, u_ref, u_ref_angle, sk, rx_ratio, z01_ratio}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_addSymLoadGenInput(
    JNIEnv *, jobject, jlong handler, jint id, jint nodeId, jboolean isConnected, jint type, jdouble p, jdouble q)
{
  ((PgmWrapper *)handler)->add_sym_load_gen(SymLoadGenInput({{{{id}, nodeId, (bool)isConnected}, (LoadGenType)type}, p, q}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_addLine(
    JNIEnv *, jobject, jlong handler, jint id, jint nodeFromId, jboolean isFromConnected, jint nodeToId, jboolean isToConnected,
    jdouble r1, jdouble x1, jdouble c1, jdouble tan1,
    jdouble r2, jdouble x2, jdouble c2, jdouble tan2, jdouble rated_current)
{
  ((PgmWrapper *)handler)->add_line(LineInput({{{id}, nodeFromId, nodeToId, (bool)isFromConnected, (bool)isToConnected}, r1, x1, c1, tan1, r2, x2, c2, tan2, rated_current}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_addTransformer(
    JNIEnv *, jobject, jlong handler, jint id, jint nodeFromId, jboolean isFromConnected, jint nodeToId, jboolean isToConnected,
    jdouble u1, jdouble u2, jdouble sn, jdouble uk, jdouble pk, jdouble i0, jdouble p0,
    jint winding_type_from, jint winding_type_to, jbyte clock,
    jint tap_side, jbyte tap_pos, jbyte tap_min, jbyte tap_max, jbyte tap_nom, jdouble tap_size,
    jdouble uk_min, jdouble uk_max,
    jdouble pk_min, jdouble pk_max,
    jdouble r_grounding_from, jdouble x_grounding_from, jdouble r_grounding_to, jdouble x_grounding_to)
{
  ((PgmWrapper *)handler)->add_transformer(TransformerInput({{{id}, nodeFromId, nodeToId, (bool)isFromConnected, (bool)isToConnected}, u1, u2, sn, uk, pk, i0, p0, (WindingType)winding_type_from, (WindingType)winding_type_to, clock, (BranchSide)tap_side, tap_pos, tap_min, tap_max, tap_nom, tap_size, uk_min, uk_max, pk_min, pk_max, r_grounding_from, x_grounding_from, r_grounding_to, x_grounding_to}));
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_finalizeConstruction(JNIEnv *, jobject, jlong handler)
{
  ((PgmWrapper *)handler)->finalize_construction();
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_runPf(JNIEnv *, jobject, jlong handler)
{
  auto info = CalculationInfo();
  ((PgmWrapper *)handler)->run_pf(CalculationMethod::newton_raphson, info);
}

JNIEXPORT void JNICALL Java_com_powsybl_pgm_PgmWrapper_getResult(JNIEnv *env, jobject, jlong handler, jobject jresult)
{
  jclass result_class = env->GetObjectClass(jresult);

  jmethodID add_node_id = env->GetMethodID(result_class, "toNode", "(IDDD)V");

  std::function<void(int, double, double, double)> f_add_node =
      [env, jresult, add_node_id](int id, double u_pu, double u, double u_angle) -> void
  { env->CallVoidMethod(jresult, add_node_id, id, u_pu, u, u_angle); };

  jmethodID add_branch_id = env->GetMethodID(result_class, "toBranch", "(IDDDDDDDD)V");

  std::function<void(int, double, double, double, double, double, double, double, double)> f_add_branch =
      [env, jresult, add_branch_id](int id, double pFrom, double qFrom, double iFrom, double sFrom, double pTo, double qTo,
                                    double iTo, double sTo) -> void
  { env->CallVoidMethod(jresult, add_branch_id, id, pFrom, qFrom, iFrom, sFrom, pTo, qTo, iTo, sTo); };

  jmethodID add_appliance_id = env->GetMethodID(result_class, "toAppliance", "(IDDDDD)V");

  std::function<void(int, double, double, double, double, double)> f_add_appliance =
      [env, jresult, add_appliance_id](int id, double p, double q, double i, double s, double pf) -> void
  { env->CallVoidMethod(jresult, add_appliance_id, id, p, q, i, s, pf); };

  ((PgmWrapper *)handler)->sym_result_to_java(f_add_node, f_add_branch, f_add_appliance);
}
