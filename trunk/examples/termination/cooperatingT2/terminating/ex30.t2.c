int nondet() { int a; return a; }
_Bool nondet_bool() { _Bool a; return a; }
int main() {
int v1 = nondet();
int v2 = nondet();
int v3 = nondet();
int v4 = nondet();
int v5 = nondet();
int v6 = nondet();
int v7 = nondet();
goto loc_6;
loc_6:
 if (nondet_bool()) {
  goto loc_5;
 }
 goto end;
loc_CP_0:
 if (nondet_bool()) {
  goto loc_1;
 }
 goto end;
loc_CP_4:
 if (nondet_bool()) {
  goto loc_2;
 }
 goto end;
loc_2:
 if (nondet_bool()) {
  if (!( v4 <= v2 )) goto end;
  goto loc_3;
 }
 if (nondet_bool()) {
  if (!( 1+v2 <= v4 )) goto end;
  v2 = 1+v2;
  goto loc_CP_4;
 }
 goto end;
loc_1:
 if (nondet_bool()) {
  if (!( v5 <= v1 )) goto end;
  v7 = nondet();
  v4 = v3;
  v2 = 0;
  goto loc_CP_4;
 }
 if (nondet_bool()) {
  if (!( 1+v1 <= v5 )) goto end;
  v1 = 1+v1;
  goto loc_CP_0;
 }
 goto end;
loc_5:
 if (nondet_bool()) {
  v3 = nondet();
  v6 = nondet();
  v5 = nondet();
  v1 = 0;
  goto loc_CP_0;
 }
 goto end;
loc_3:
end:
;
}
