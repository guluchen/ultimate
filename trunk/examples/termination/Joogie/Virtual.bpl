type ref;
type realVar;
type classConst;
// type Field x;
// var $HeapVar : <x>[ref, Field x]x;

const unique $null : ref ;
const unique $intArrNull : [int]int ;
const unique $realArrNull : [int]realVar ;
const unique $refArrNull : [int]ref ;

const unique $arrSizeIdx : int;
var $intArrSize : [int]int;
var $realArrSize : [realVar]int;
var $refArrSize : [ref]int;

var $stringSize : [ref]int;

//built-in axioms 
axiom ($arrSizeIdx == -1);

//note: new version doesn't put helpers in the perlude anymore//Prelude finished 



var Node$Internal$next254 : Field ref;


// procedure is generated by joogie.
function {:inline true} $neref(x : ref, y : ref) returns (__ret : int) {
if (x != y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $realarrtoref($param00 : [int]realVar) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $modreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $leref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $modint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $gtref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 8
// <Internal: int length()>
procedure int$Internal$length$2233(__this : ref) returns (__ret : int)  requires ($neref((__this), ($null))==1);
 {
var $i010 : int;
var $i111 : int;
var $r18 : ref;
var r07 : ref;
Block20:
	r07 := __this;
	 assert ($neref((r07), ($null))==1);
	 //  @line: 9
	$r18 := $HeapVar[r07, Node$Internal$next254];
	 assert ($neref(($r18), ($null))==1);
	 //  @line: 9
	 call $i010 := int$Node$length$2231(($r18));
	 //  @line: 9
	$i111 := $addint((1), ($i010));
	 //  @line: 9
	__ret := $i111;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqrealarray($param00 : [int]realVar, $param11 : [int]realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $addint(x : int, y : int) returns (__ret : int) {
(x + y)
}


// procedure is generated by joogie.
function {:inline true} $subref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $inttoreal($param00 : int) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shrint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negReal($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ushrint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $refarrtoref($param00 : [int]ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $divref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $mulref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $neint(x : int, y : int) returns (__ret : int) {
if (x != y) then 1 else 0
}


	 //  @line: 3
// <Internal: void <init>(Node)>
procedure void$Internal$$la$init$ra$$2232(__this : ref, $param_0 : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r16 : ref;
var r05 : ref;
Block19:
	r05 := __this;
	r16 := $param_0;
	 assert ($neref((r05), ($null))==1);
	 //  @line: 4
	 call void$Node$$la$init$ra$$2230((r05));
	 assert ($neref((r05), ($null))==1);
	 //  @line: 5
	$HeapVar[r05, Node$Internal$next254] := r16;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $ltreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 16
// <Virtual: void main(java.lang.String[])>
procedure void$Virtual$main$2237($param_0 : [int]ref) {
var r023 : [int]ref;
var $r317 : ref;
var $r215 : ref;
var $i020 : int;
var $r422 : ref;
var i225 : int;
var r524 : ref;

 //temp local variables 
var $freshlocal0 : int;

Block24:
	r023 := $param_0;
	 //  @line: 17
	$r215 := $newvariable((25));
	 assume ($neref(($newvariable((25))), ($null))==1);
	 assert ($neref(($r215), ($null))==1);
	 //  @line: 17
	 call void$Div$$la$init$ra$$2228(($r215));
	 //  @line: 18
	$r317 := $newvariable((26));
	 assume ($neref(($newvariable((26))), ($null))==1);
	 assert ($neref(($r317), ($null))==1);
	 //  @line: 18
	 call void$Nil$$la$init$ra$$2234(($r317));
	 //  @line: 18
	r524 := $r317;
	 //  @line: 19
	i225 := 28;
	 goto Block27;
	 //  @line: 20
Block27:
	 //  @line: 20
	$i020 := i225;
	 //  @line: 20
	i225 := $addint((i225), (-1));
	 goto Block28;
	 //  @line: 20
Block28:
	 goto Block29, Block31;
	 //  @line: 20
Block29:
	 assume ($leint(($i020), (0))==1);
	 goto Block30;
	 //  @line: 20
Block31:
	 //  @line: 20
	 assume ($negInt(($leint(($i020), (0))))==1);
	 //  @line: 23
	$r422 := $newvariable((32));
	 assume ($neref(($newvariable((32))), ($null))==1);
	 assert ($neref(($r422), ($null))==1);
	 //  @line: 23
	 call void$Internal$$la$init$ra$$2232(($r422), (r524));
	 //  @line: 23
	r524 := $r422;
	 goto Block27;
	 //  @line: 22
Block30:
	 assert ($neref((r524), ($null))==1);
	 //  @line: 22
	 call $freshlocal0 := int$Node$length$2231((r524));
	 goto Block33;
	 //  @line: 23
Block33:
	 return;
}


// procedure is generated by joogie.
function {:inline true} $reftorefarr($param00 : ref) returns (__ret : [int]ref);



// procedure is generated by joogie.
function {:inline true} $gtint(x : int, y : int) returns (__ret : int) {
if (x > y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $reftoint($param00 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $addref($param00 : ref, $param11 : ref) returns (__ret : ref);



	 //  @line: 14
// <Virtual: void <init>()>
procedure void$Virtual$$la$init$ra$$2236(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r014 : ref;
Block23:
	r014 := __this;
	 assert ($neref((r014), ($null))==1);
	 //  @line: 15
	 call void$java.lang.Object$$la$init$ra$$28((r014));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $xorreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $andref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $cmpreal(x : realVar, y : realVar) returns (__ret : int) {
if ($ltreal((x), (y)) == 1) then 1 else if ($eqreal((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $addreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $gtreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// <java.lang.Object: void <init>()>
procedure void$java.lang.Object$$la$init$ra$$28(__this : ref);



// procedure is generated by joogie.
function {:inline true} $eqreal(x : realVar, y : realVar) returns (__ret : int) {
if (x == y) then 1 else 0
}


// <Node: int length()>
procedure int$Node$length$2231(__this : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ltint(x : int, y : int) returns (__ret : int) {
if (x < y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $newvariable($param00 : int) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $divint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geint(x : int, y : int) returns (__ret : int) {
if (x >= y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $mulint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $leint(x : int, y : int) returns (__ret : int) {
if (x <= y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $shlref($param00 : ref, $param11 : ref) returns (__ret : int);



// <Node: void <init>()>
procedure void$Node$$la$init$ra$$2230(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r04 : ref;
Block18:
	r04 := __this;
	 assert ($neref((r04), ($null))==1);
	 //  @line: 1
	 call void$java.lang.Object$$la$init$ra$$28((r04));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqrefarray($param00 : [int]ref, $param11 : [int]ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftointarr($param00 : ref) returns (__ret : [int]int);



// procedure is generated by joogie.
function {:inline true} $ltref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $mulreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



	 //  @line: 2
// <Nil: int length()>
procedure int$Nil$length$2235(__this : ref) returns (__ret : int)  requires ($neref((__this), ($null))==1);
 {
var r013 : ref;
Block22:
	r013 := __this;
	 //  @line: 3
	__ret := 0;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $shrref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ushrreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $shrreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $divreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $orint($param00 : int, $param11 : int) returns (__ret : int);



// <Div: void <init>()>
procedure void$Div$$la$init$ra$$2228(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r01 : ref;
Block16:
	r01 := __this;
	 assert ($neref((r01), ($null))==1);
	 //  @line: 1
	 call void$Node$$la$init$ra$$2230((r01));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $reftorealarr($param00 : ref) returns (__ret : [int]realVar);



// procedure is generated by joogie.
function {:inline true} $cmpref(x : ref, y : ref) returns (__ret : int) {
if ($ltref((x), (y)) == 1) then 1 else if ($eqref((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $realtoint($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $orreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $eqint(x : int, y : int) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ushrref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $modref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $eqintarray($param00 : [int]int, $param11 : [int]int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negRef($param00 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $lereal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $nereal(x : realVar, y : realVar) returns (__ret : int) {
if (x != y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $instanceof($param00 : ref, $param11 : classConst) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $orref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $intarrtoref($param00 : [int]int) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $subreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shlreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negInt(x : int) returns (__ret : int) {
if (x == 0) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $gereal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 2
// <Div: int length()>
procedure int$Div$length$2229(__this : ref) returns (__ret : int)  requires ($neref((__this), ($null))==1);
 {
var $i03 : int;
var r02 : ref;
Block17:
	r02 := __this;
	 assert ($neref((r02), ($null))==1);
	 //  @line: 3
	 call $i03 := int$Div$length$2229((r02));
	 //  @line: 3
	__ret := $i03;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqref(x : ref, y : ref) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $cmpint(x : int, y : int) returns (__ret : int) {
if (x < y) then 1 else if (x == y) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $andint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $andreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $shlint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $subint(x : int, y : int) returns (__ret : int) {
(x - y)
}


// <Nil: void <init>()>
procedure void$Nil$$la$init$ra$$2234(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r012 : ref;
Block21:
	r012 := __this;
	 assert ($neref((r012), ($null))==1);
	 //  @line: 1
	 call void$Node$$la$init$ra$$2230((r012));
	 return;
}


