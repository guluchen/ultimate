int nondet() { int a; return a; }
_Bool nondet_bool() { _Bool a; return a; }
int main() {
int v1 = nondet();
int v2 = nondet();
int v3 = nondet();
int v4 = nondet();
int v5 = nondet();
goto loc_5;
loc_5:
 if (nondet_bool()) {
  goto loc_4;
 }
 goto end;
loc_CP_1:
 if (nondet_bool()) {
  v2 = nondet();
  goto loc_0;
 }
 goto end;
loc_CP_3:
 if (nondet_bool()) {
  if (!( v5 <= 0 )) goto end;
  goto loc_CP_1;
 }
 if (nondet_bool()) {
  if (!( 1 <= v5 )) goto end;
  v1 = nondet();
  goto loc_2;
 }
 goto end;
loc_0:
 if (nondet_bool()) {
  if (!( v2 <= 0 )) goto end;
  v3 = 0;
  goto loc_CP_1;
 }
 if (nondet_bool()) {
  if (!( 1 <= v2 )) goto end;
  v3 = 1;
  goto loc_CP_1;
 }
 goto end;
loc_2:
 if (nondet_bool()) {
  if (!( v1 <= 0 )) goto end;
  v5 = -2+v5;
  goto loc_CP_3;
 }
 if (nondet_bool()) {
  if (!( 1 <= v1 )) goto end;
  v5 = -1+v5;
  goto loc_CP_3;
 }
 goto end;
loc_4:
 if (nondet_bool()) {
  v3 = 0;
  v4 = 0;
  v4 = 1;
  v4 = 0;
  goto loc_CP_3;
 }
 goto end;
end:
;
}
