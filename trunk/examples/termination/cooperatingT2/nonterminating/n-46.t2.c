int nondet() { int a; return a; }
_Bool nondet_bool() { _Bool a; return a; }
int main() {
int v1 = nondet();
int v2 = nondet();
int v3 = nondet();
int v4 = nondet();
goto loc_4;
loc_4:
 if (nondet_bool()) {
  goto loc_2;
 }
 goto end;
loc_CP_0:
 if (nondet_bool()) {
  v2 = 1+v2;
  v3 = 1+v3;
  v4 = -1+v4;
  goto loc_1;
 }
 goto end;
loc_1:
 if (nondet_bool()) {
  goto loc_CP_0;
 }
 goto end;
loc_2:
 if (nondet_bool()) {
  if (!( 1-v2+v3 <= 0 )) goto end;
  v1 = nondet();
  goto loc_3;
 }
 if (nondet_bool()) {
  if (!( 0 <= -1*v2+v3 )) goto end;
  if (!( 1-v3+v4 <= 0 )) goto end;
  v1 = nondet();
  goto loc_3;
 }
 if (nondet_bool()) {
  if (!( 0 <= -1*v2+v3 )) goto end;
  if (!( 0 <= -1*v3+v4 )) goto end;
  if (!( 0 <= -1-v2+v4 )) goto end;
  v1 = nondet();
  goto loc_3;
 }
 if (nondet_bool()) {
  if (!( 0 <= -1*v2+v3 )) goto end;
  if (!( 0 <= -1*v3+v4 )) goto end;
  if (!( -1*v2+v4 <= 0 )) goto end;
  goto loc_CP_0;
 }
 goto end;
loc_3:
loc_3:
loc_3:
end:
;
}
