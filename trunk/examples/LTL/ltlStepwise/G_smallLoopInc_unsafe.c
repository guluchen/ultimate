#include <stdio.h> 
#include <assert.h>
#include <math.h>

//#Unsafe
//@ ltl invariant positive: []( AP(a >= b) ) ;

//Run of > 8 steps is a violation

extern void __VERIFIER_error() __attribute__ ((__noreturn__));
extern void __VERIFIER_ltl_step() __attribute__ ((__noreturn__));

int a = 5;
int b = 3;
	
	
int main()
{
	while(a < 100){
		__VERIFIER_ltl_step();
		b = b-1;
		a = a + b; 
	}
}
