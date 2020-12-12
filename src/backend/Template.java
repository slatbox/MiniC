package backend;

public class Template {
    public static String asmHead  = 
    "include \\masm32\\include\\masm32rt.inc\n" + 
    ".data?\n" +
    "\tvalue dd ?\n" +
    ".data\n" +
    "\tt1 dd 0\n" + 
    "\tt2 dd 0\n" + 
    "\tt3 dd 0\n" + 
    "\tt4 dd 0\n" + 
    "\tt5 dd 0\n" + 
    "\tt6 dd 0\n" + 
    "\tt7 dd 0\n" + 
    "\tt8 dd 0\n" + 
    "\tt9 dd 0\n" + 
    "\tt10 dd 0\n" + 
    "\tt11 dd 0\n" + 
    "\tregpos dd 0\n" + 
    "\tinputCount dd 0\n" +
	"\toutputCount dd 0\n"+
	"\tinputBuffer dd 0\n"+
	"\tinputLabel db '%d',0\n"+
	"\toutputNote db 'output num%d is:%d',0Ah, 0Dh,0\n"+
	"\tinputNote db 'Please input num%d',0Ah, 0Dh,0\n"+
    ".code\n" +
    "\tstart:\n" +
    "\tcall main\n" +
    "\tinkey\n" +
    "\texit\n" +
    "printNum proc\n"+
	"\tpush ebp\n"+
	"\tmov ebp,esp\n"+
	"\tpush edi\n"+
	"\tpush esi\n"+
	"\tpush edx\n"+
	"\tmov regpos,esp\n"+

	"\tpush [ebp + 8]\n"+
	"\tpush outputCount\n"+
	"\tpush offset outputNote\n"+
	"\tcall crt_printf\n"+
	"\tinc outputCount\n"+


	"\tmov esp,regpos\n"+
	"\tpop edx\n"+
	"\tpop esi\n"+
	"\tpop edi\n"+
	"\tmov esp,ebp\n"+
	"\tpop ebp\n"+
	"\tret\n"+
    "printNum endp\n"+
    "inputNum proc\n"+
	"\tpush ebp\n"+
	"\tmov ebp,esp\n"+
	"\tpush edi\n"+
	"\tpush esi\n"+
	"\tpush edx\n"+
	"\tmov regpos,esp\n"+

	"\tpush inputCount\n"+
	"\tpush offset inputNote\n"+
	"\tcall crt_printf\n"+
	
	"\tpush offset inputBuffer\n"+
	"\tpush offset inputLabel\n"+
    "\tcall crt_scanf\n"+
    "\tinc inputCount\n" +
	"\tmov eax,inputBuffer\n"+

	"\tmov esp,regpos\n"+
	"\tpop edx\n"+
	"\tpop esi\n"+
	"\tpop edi\n"+
	"\tmov esp,ebp\n"+
	"\tpop ebp\n"+
	"\tret\n"+
    "inputNum endp\n";

    public static String asmEnd = 
    "end start\n";
    public static String functionHead = 
    "\tpush ebp\n"+
	"\tmov ebp,esp\n"+
	"\tpush edi\n"+
	"\tpush esi\n"+
	"\tpush edx\n"+
    "\tmov regpos,esp\n";
    public static String functionEnd = 
    "\tmov esp,regpos\n"+
    "\tpop edx\n"+
	"\tpop esi\n"+
	"\tpop edi\n"+
	"\tmov esp,ebp\n"+
	"\tpop ebp\n"+
	"\tret\n";


}
