int nondet() { int a; return a; }
_Bool nondet_bool() { _Bool a; return a; }
int main() {
int v1 = nondet();
int v2 = nondet();
goto loc_4;
loc_4:
 if (nondet_bool()) {
  goto loc_0;
 }
 goto end;
loc_CP_1:
 if (nondet_bool()) {
  v1 = 1+v1;
  goto loc_2;
 }
 if (nondet_bool()) {
  if (!( 2 <= 0 )) goto end;
  goto loc_3;
 }
 goto end;
loc_0:
 if (nondet_bool()) {
  v1 = 1;
  v2 = 1;
  goto loc_CP_1;
 }
 goto end;
loc_2:
 if (nondet_bool()) {
  goto loc_CP_1;
 }
 goto end;
loc_3:
end:
;
}
