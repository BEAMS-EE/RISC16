package editor;


import java.util.*;

import javax.swing.JOptionPane;



public class Architecture {


	static final String [][] DEFAULTINSTRUCTIONSET = {{ "add", "addi", "nand", "lui", "lw", "sw","beq", "jalr" },
		{ "add", "sub", "nand", "lui", "shl", "sha","nor", "xor","addi", "shifti", "bl", "bg", "lw","sw", "beq", "jalr"},
		{ "add", "sub", "nand", "lui", "shl", "sha","nor", "xor","addi", "shifti", "bl", "mul", "lw","sw", "beq", "jalr"}};
	static final boolean[] DEFAULTBRANCHONCARRY={false,true,true};
	private static final int DEFAULTNUMBEROFREGISTER = 8;
	private static final int DEFAULTOPSIZE = 16;
	private static final int MINIMMRRRSIZE = 4;
	private static final int MINIMMRRISIZE = 7;
	private static final int MINIMMRISIZE = 10;
	private static final int MAXIMMRISIZE = 16;

	private String[] instructionSet;
	int numberOfRegister;

	boolean branchOnCarry;
	int instructionSize;
	private int immRRRSize;
	private int immRRISize;
	private int immRISize;
	private int opSize;
	private int regSize;

	private int shift_op;
	private int shift_a;
	private int shift_b;
	private int shift_c;
	private int shift_imm;

	private String name;

	private Hashtable<String, Integer> opTable;



	private int newpc=0;
	private boolean lastInstructionBranch=false;
	private boolean isSigned=true;
	private int lastLWDest;
	private boolean stall;


	public Architecture(){
		this("Default",DEFAULTINSTRUCTIONSET[0],DEFAULTNUMBEROFREGISTER,DEFAULTOPSIZE,DEFAULTBRANCHONCARRY[0],false);

	}

	public Architecture(String name,String[] instruSet,int numberOfReg,int instruSize,boolean branchOnCarry,boolean isSigned){
		this.name=name;
		this.instructionSet=instruSet;
		this.numberOfRegister=numberOfReg;
		this.instructionSize=instruSize;
		this.branchOnCarry=branchOnCarry;
		this.isSigned=isSigned;

		initConstants();







		opTable=new Hashtable<String, Integer>();
		for(int i=0;i<instructionSet.length;i++){
			opTable.put(instructionSet[i].toUpperCase(),i);
		}


	}

	private void initConstants() {
		opSize=(int) Math.ceil(Math.log(instructionSet.length)/Math.log(2.0));
		regSize=(int) (Math.log(numberOfRegister)/Math.log(2.0));

		for(;!instructionSizeIsOk();instructionSize++);
		for(;!immRISizeIsOk();instructionSize--);
		immRRRSize=instructionSize-3*regSize-opSize;
		immRRISize=instructionSize-2*regSize-opSize;
		immRISize=instructionSize-regSize-opSize;

		shift_op=instructionSize-opSize;
		shift_a=shift_op-regSize;
		shift_b=shift_a-regSize;
		shift_c=0;
		shift_imm=regSize;
	}

	private boolean instructionSizeIsOk(){
		return (opSize+3*regSize+MINIMMRRRSIZE <= instructionSize) &&
		(opSize+2*regSize+MINIMMRRISIZE <= instructionSize) &&
		(opSize+regSize+MINIMMRISIZE <= instructionSize);
	}
	private boolean immRISizeIsOk(){
		return (instructionSize-opSize-regSize <=MAXIMMRISIZE) ;
	}

	public int getOpcode(String instr){

		try {
			return opTable.get(instr.toUpperCase());
		} catch (NullPointerException e) {
			return -1;
		}
	}
	public String getOpname(int opcode){

		Set entry = opTable.entrySet () ;
		Iterator it = entry.iterator () ;
		while (it.hasNext ()) {
			Map.Entry e = (Map.Entry) it.next() ;
			if ((Integer) e.getValue()==opcode) return (String) e.getKey();
		}
		return null;
	}

	public int getNumberOfRegister() {
		return numberOfRegister;
	}

	public String getName(){
		return name;
	}


	public String calculate(String hexOp,Registers registers,Memoire mem,int pc){
		// decode
		int opcode= (Integer.decode(hexOp) >> shift_op) & (int) (Math.pow(2, opSize)-1);
		String opname=getOpname(opcode);
		int regA= (Integer.decode(hexOp) >> shift_a) & (int) (Math.pow(2, regSize)-1);
		int regB= (Integer.decode(hexOp) >> shift_b) & (int) (Math.pow(2, regSize)-1);
		int regC= (Integer.decode(hexOp) >> shift_c) & (int) (Math.pow(2, regSize)-1);
		int immRRR=(Integer.decode(hexOp) >> shift_imm) & (int) (Math.pow(2, immRRRSize)-1);
		if((immRRR & (1 << (immRRRSize-1)))!=0) immRRR=immRRR-(int) (Math.pow(2, immRRRSize));
		int immRRI=(Integer.decode(hexOp)) & (int) (Math.pow(2, immRRISize)-1);
		if((immRRI & (1 << (immRRISize-1)))!=0) immRRI=immRRI-(int) (Math.pow(2, immRRISize));		
		int immRI=(Integer.decode(hexOp)) & (int) (Math.pow(2, immRISize)-1);

		int regAValue=Integer.decode(registers.getCase(regA,1)).intValue();
		int regBValue=Integer.decode(registers.getCase(regB,1)).intValue();
		int regCValue=Integer.decode(registers.getCase(regC,1)).intValue();

		setNewpc(pc);
		setLastInstructionBranch(false);
		setLastInstructionStall(false);


		int res=0;	
		String trace="";
		boolean carryFlag=false;
		boolean overflowFlag=false;
		boolean test=false;
		int sens=0;
		short val=0;
		System.out.println("OHAI");
		switch(Instruction.toInstru(opname)){
		case ADD :
			res=Integer.decode(registers.getCase(regB,1))+Integer.decode(registers.getCase(regC,1));
			carryFlag=((res & 0x10000)!=0);
			// For subtraction, OF=(src1(15) XNOR src2(15)) AND (res(15) XOR src1(15))
			// Overflow when (+)+(+)=(-) and (-)+(-)=(+)
			overflowFlag=((~(regBValue & 0x8000 ^ regCValue & 0x8000)) & (res & 0x8000 ^ regBValue & 0x8000))!=0 ;
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			if (isOverFlow(carryFlag,overflowFlag)){
				System.out.println("Overflow!");
				setNewpc(pc+immRRR);
				setLastInstructionBranch(true);
			}
			else {
				System.out.println("No overflow.");
			}
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case ADDI :
			res=Integer.decode(registers.getCase(regB,1))+immRRI;
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB));
			setLastLWDest(0);
			break;
		case NAND :
			res=~(Integer.decode(registers.getCase(regB,1))&Integer.decode(registers.getCase(regC,1)));
			res=res & 0xFFFF;
			registers.write(regA, res);		
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case LUI :
			System.out.println("immRISize="+immRISize);
//			res=(immRI << (16-immRISize)) & ((int) (Math.pow(2, immRISize)-1) << (16-immRISize));
			res=(immRI << 6) & 0xFFC0;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastLWDest(0);
			break;
		case LW :
			res=Integer.decode(mem.getCase(Integer.decode(registers.getCase(regB,1))+immRRI,1));
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB));
			setLastLWDest(regA);
			break;
		case SW :
			res=Integer.decode(registers.getCase(regA,1));
			mem.setCase(Integer.decode(registers.getCase(regB,1))+immRRI, res);
			trace="m[" + (Integer.decode(registers.getCase(regB,1))+immRRI) + "] = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regA) | equalLastLWDest(regB));
			setLastLWDest(0);
			break;
		case BEQ :
			if(Integer.decode(registers.getCase(regA,1)).equals(Integer.decode(registers.getCase(regB,1)))){
				setNewpc(pc+immRRI);
				setLastInstructionBranch(true);
			}
			setLastInstructionStall(equalLastLWDest(regA) | equalLastLWDest(regB));
			setLastLWDest(0);
			break;
		case JALR :		
			if (immRRI==0){
				setNewpc(Integer.decode(registers.getCase(regB,1)).intValue()-1);
				setLastInstructionBranch(true);
				res=pc+1;
				registers.write(regA, res);
				if (regA!=0) trace="r" + regA + " = " + decToHex(res);
				setLastInstructionStall(equalLastLWDest(regB));
			}
			else // halt
				trace="halt";
			setLastLWDest(0);
			break;
		case SUB :
			res=Integer.decode(registers.getCase(regB,1))-Integer.decode(registers.getCase(regC,1));
			String tmp = registers.getCase(regB, 1);
			// Two's complement translation.
			int tmpRes = Integer.valueOf((registers.getCase(regB, 1)).substring(2), 16).shortValue() - Integer.valueOf((registers.getCase(regC, 1)).substring(2), 16).shortValue();
			if(isSigned) {
				if(tmpRes < -32768 || tmpRes > 32767) {
					overflowFlag = true;
					System.out.println("SUB overflow");
				}
				else {
					System.out.println("No sub overflow");
				}
			}
			else {
				if(res > 65535 || res < 0) {
					overflowFlag = true;
					System.out.println("SUB overflow");
				}
				else {
					System.out.println("No sub overflow");
				}
			}
				
			// For subtraction the carry out is inverted
			carryFlag=((res & 0x10000)==0);//TODO WHY?
			// For subtraction, OF=(src1(15) XOR src2(15)) AND (res(15) XNOR src2(15))
			// Overflow when (+)-(-)=(-) and (-)-(+)=(+)
//			overflowFlag=(((regBValue & 0x8000 ^ regCValue & 0x8000)) & ~(res & 0x8000 ^ regCValue & 0x8000))!=0 ;
			if(overflowFlag) {
				System.out.println("SUB overflow");
			}
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			if (isOverFlow(carryFlag,overflowFlag)){
				setNewpc(pc+immRRR);
				setLastInstructionBranch(true);
				trace += " (overflow)";
			}
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case SHL :
			val = (short)regCValue;
			sens=0;
			if(val<0)
				sens=1;
			//sens=(regCValue >> 4) & 0x1;
			res = logicalShift(regBValue,sens,val);//regCValue & 0x1F);
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (sens==0){
				if(predictShiftOverFlow(regBValue,val)){//regCValue & 0x1F)) {
					setNewpc(pc+immRRR);
					setLastInstructionBranch(true);
				}
			}
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case SHA :
			val = (short)regCValue;
			sens=0;
			if(val<0)
				sens=1;
			//sens=(regCValue >> 4) & 0x1;
			res = arithmShift(regBValue,sens,val);//regCValue & 0x1F);
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (sens==0){
				if(predictShiftOverFlow(regBValue,val)){//regCValue & 0x1F)) {
					setNewpc(pc+immRRR);
					setLastInstructionBranch(true);
				}
			}
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case NOR :
			res=~(Integer.decode(registers.getCase(regB,1))|Integer.decode(registers.getCase(regC,1)));
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case XOR :
			res=(Integer.decode(registers.getCase(regB,1))^Integer.decode(registers.getCase(regC,1)));
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case SHIFTI :
			if (((immRRI >> 5) & 0x1) == 1) { 
				res = arithmShift(regBValue,(immRRI >> 4) & 0x1,immRRI & 0x1F); 
			}
			else if (((immRRI >> 5) & 0x1) == 0) { 
				res = logicalShift(regBValue,(immRRI >> 4) & 0x1,immRRI & 0x1F); 
			}
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB));
			setLastLWDest(0);
			break;

		case BL :
			test=false;
			if (isSigned){		
				test=((short)regAValue < (short)regBValue);
				System.out.println("test=((short)regAValue < (short)regBValue);");
				System.out.println("test="+test+", regAValue=" + regAValue + "regBValue="+regBValue);
			}
			else {
				test=(regAValue < regBValue);
				System.out.println("test=(regAValue < regBValue);");
				System.out.println("test="+test+", regAValue=" + regAValue + "regBValue="+regBValue);
			}
			if(test){
				System.out.println("branch");
				setNewpc(pc+immRRI);
				setLastInstructionBranch(true);
			}
			setLastInstructionStall(equalLastLWDest(regA) | equalLastLWDest(regB));
			setLastLWDest(0);
			break;
		case BG :
			test=false;
			if (isSigned){		
				test=((short)regAValue > (short)regBValue);
			}
			else {
				test=(regAValue > regBValue);
			}
			if(test){
				System.out.println("branch");
				setNewpc(pc+immRRI);
				setLastInstructionBranch(true);
			}
			setLastInstructionStall(equalLastLWDest(regA) | equalLastLWDest(regB));
			setLastLWDest(0);
			break;
		case MUL :
			res=Integer.decode(registers.getCase(regB,1))*Integer.decode(registers.getCase(regC,1));
			int msb=(res >> 16)& 0xFFFF;
			int lsb=res & 0xFFFF;
			registers.write(regA-1, msb);
			registers.write(regA, lsb);		
			if (regA!=0) trace="r" + regA + " = " + decToHex(lsb);
			if (regA>1) trace+=" r" + (regA-1) + " = " + decToHex(msb);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case XNOR :
			res=~(Integer.decode(registers.getCase(regB,1))^Integer.decode(registers.getCase(regC,1)));
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case OR :
			res=(Integer.decode(registers.getCase(regB,1))|Integer.decode(registers.getCase(regC,1)));			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		case AND :
			res=(Integer.decode(registers.getCase(regB,1))&Integer.decode(registers.getCase(regC,1)));
			res=res & 0xFFFF;
			registers.write(regA, res);
			if (regA!=0) trace="r" + regA + " = " + decToHex(res);
			setLastInstructionStall(equalLastLWDest(regB) | equalLastLWDest(regC));
			setLastLWDest(0);
			break;
		default :
		}

		registers.write(0, 0);

		return trace;
	}




	public String assemble(String op,int arg0,int arg1,int arg2,int arg3,int ligne){
		if (getOpcode(op)==-1){
			warningMessage("error : bad instruction",ligne);
			return "error : bad instruction";
		}

		int num=(getOpcode(op) << shift_op) + (arg0 << shift_a);

		int immRRR=arg3 & (int) (Math.pow(2, immRRRSize)-1);
		int immRRI=arg2 & (int) (Math.pow(2, immRRISize)-1);
//		int immRI=(arg1 >> (16-immRISize)) & (int) (Math.pow(2, immRISize)-1);
		int immRI = arg1;

		if (getFormat(op).equals("RRR")){
			num=num+(arg1 << shift_b) + (arg2 << shift_c) + (immRRR << shift_imm);
		}
		else if (getFormat(op).equals("RRI")){
			num=num+(arg1 << shift_b) +immRRI;
		}
		else if(getFormat(op).equals("RI")){
			num=num+immRI ;
		}
		
		String testSize = Integer.toBinaryString(arg0);
		if (testSize.length() > regSize){
			warningMessage("error : format error (rA)"+"\n"+"There are only "+numberOfRegister+" registers",ligne);   	  
			return "error : format error (rA)";
		}
		if (!op.equals("sw") && !op.equals("beq") && arg0==0 && !(op.equals("jalr") && arg2==-1)) {
			informationMessage("Warning : R0 is put as destination!",ligne);
		}

		if (getFormat(op).equals("RRR")){
			testSize = Integer.toBinaryString(arg1);
			if (testSize.length() > regSize){
				warningMessage("error : format error (rB)"+"\n"+"There are only "+numberOfRegister+" registers",ligne);   	  
				return "error : format error (rB)";
			}
			testSize = Integer.toBinaryString(arg2);
			if (testSize.length() > regSize){
				warningMessage("error : format error (rC)"+"\n"+"There are only "+numberOfRegister+" registers",ligne);   	  
				return "error : format error (rC)";
			}

			if (arg3<-(int)(Math.pow(2,immRRRSize-1)) || arg3>(int)(Math.pow(2,immRRRSize-1))-1){
				warningMessage("error : Imm too big (RRR type)",ligne);
				return "error : Imm too big (RRR type)";
			}

		}
		else if (getFormat(op).equals("RRI")){
			testSize = Integer.toBinaryString(arg1);
			if (testSize.length() > regSize){
				warningMessage("error : format error (rB)"+"\n"+"There are only "+numberOfRegister+" registers",ligne);   	  
				return "error : format error (rB)";
			}
			if (!op.equals("jalr")) {

				//testSize = Integer.toBinaryString(arg2);
				//if (testSize.length() > immRRISize)   {
				if (arg2<-(int)(Math.pow(2,immRRISize-1)) || arg2>(int)(Math.pow(2,immRRISize-1))-1){
					warningMessage("error : Imm too big (RRI type)",ligne);
					return "error : Imm too big (RRI type)";
				}				
			}	
		}

		else if (getFormat(op).equals("RI")){
			//int imm=Integer.parseInt(arg1, 10);
			int imm=arg1;
			int shift=(16-immRISize);
			int twoExpShift=(int) (Math.pow(2,16-immRISize));
			int maxRI=(int) (Math.pow(2, immRISize)-1);
			System.out.println("imm="+imm+", shift="+shift+", twoExpShift="+twoExpShift+", maxRI="+maxRI);
			if(imm >(maxRI << shift)) {
				int newimm=((imm/twoExpShift)& maxRI) *twoExpShift;
				informationMessage("Imm too big (RI type)\nMax Value is : "+(maxRI << shift)+"\nValue was remplaced by : "+newimm,ligne);
				//setCase("lui "+sTab[0]+","+newimm, a, 2);
			}
			else if(imm>0 && imm%twoExpShift!=0){
				int newimm=(imm/twoExpShift)*twoExpShift;
//				informationMessage("The accuracy of the value is limited to "+newimm,ligne);
				//setCase("lui "+sTab[0]+","+newimm, a, 2);
			}	
		}

		String instruction=Integer.toHexString(num).toUpperCase();
		while(instruction.length()<Math.ceil(instructionSize/4.0)) instruction="0"+instruction;
		instruction="0x"+instruction;

		return instruction;
	}

	/**
	 * @param res
	 * @return
	 */
	private String decToHex(int res) {
		String resHex;
		resHex=Integer.toHexString(res).toUpperCase();
		while(resHex.length()<4) resHex="0"+resHex;
		resHex="0x"+resHex;
		return resHex;
	}


	public void setNewpc(int newpc) {
		this.newpc = newpc;
	}

	public int getNewpc() {
		return newpc;
	}

	public boolean isOverFlow(boolean carry,boolean overflow){
		System.out.println("carry:" + carry + " overflow:"+ overflow+" branchOnCarry: "+branchOnCarry + " isSigned: "+isSigned);
		return (isSigned)? (branchOnCarry & overflow) : (branchOnCarry & carry)	;
	}


	public int logicalShift(int num,int sens,int n){
		if (sens==0){
			return (short)(num << n);
		}
		else if (sens==1){
			return  (short)(num >>> (-n));// logical right shifts
		}
		else return -1;		
	}


	public int arithmShift(int num,int sens,int n){
		if (sens==0){
			return (short)(num << n);
		}
		else if (sens==1){
			return (short)num >> (-n);
		}
		else return -1;		
	}

	public boolean predictShiftOverFlow(int num,int n){
		if (isSigned){


			if ((((short)num >> (15-n))& 0xFFFF)==0 & (((short)num >> 15) & 0x1)==0) {System.out.printf("IF\n");return false;}  
			else if ((((short)num >> (15-n))& 0xFFFF)==0xFFFF & (((short)num >> 15) & 0x1)==1) {System.out.printf("ELSEIF\n");return false;}            
			else {System.out.printf("ELSE\n");return true;}
		}
		else {
			if ((((short)num >> (16-n))& 0xFFFF)==0 ) {System.out.printf("IF\n");return false;}  
			//else if ((((short)num >> (15-n))& 0xFFFF)==0xFFFF & (((short)num >> 15) & 0x1)==1) {System.out.printf("ELSEIF\n");return true;}            
			else {System.out.printf("ELSE\n");return true;}

		}

	}

	private void setLastInstructionBranch(boolean lastInstructionBranch) {
		this.lastInstructionBranch = lastInstructionBranch;
	}

	private void setLastLWDest(int reg) {
		this.lastLWDest = reg;
	}
	private boolean equalLastLWDest(int reg){
		if (reg==0) return false;
		else return (reg==lastLWDest);
	}

	public boolean isLastInstructionStall(){
		return stall;		
	}
	private void setLastInstructionStall(boolean stall){
		this.stall=stall;

	}


	public boolean isLastInstructionBranch() {
		return lastInstructionBranch;
	}


	private void warningMessage(String text, int a){	
		String text2 = new String();
		text2 = "\nLigne "
			+ a
			+ " :\n"+text+"\n";
		JOptionPane.showMessageDialog(null, text2, "Warning : Program Memory",
				JOptionPane.WARNING_MESSAGE);

	}

	private void informationMessage(String text, int a){	
		String text2 = new String();
		text2 = "\nLigne "
			+ a
			+ " :\n"+text+"\n";
		JOptionPane.showMessageDialog(null, text2, "Warning : Program Memory",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public String getFormat(String op){

		String format="";

		switch(Instruction.toInstru(op)){
		case ADD : case NAND : 
			format= "RRR";			
			break;
		case ADDI : case LW : case SW : case BEQ : case JALR :
			format= "RRI";
			break;
		case LUI :
			format= "RI";
			break;
		case MOVI :
			format= "RI";
			break;
			
		case SUB : case SHL : case SHA : case NOR : case XOR :
			format= "RRR";
			break;
		case SHIFTI : case BL : case BG :
			format= "RRI";
			break;
		case MUL : case OR : case AND : case XNOR :
			format="RRR";
			break;
		default :			
		}

		return format;

	}


	public enum Instruction
	{
		ADD,ADDI,NAND,LUI,LW,SW,BEQ,JALR,SUB,SHL,SHA,NOR,XOR,SHIFTI,BL,BG,MOVI,MUL,OR,AND,XNOR,
		NOVALUE;

		public static Instruction toInstru(String str)
		{
			try {
				if(str != null)
				{
					return valueOf(str.toUpperCase());
				}
				else
				{
					return NOVALUE;
				}

			} 
			catch (Exception ex) {
				return NOVALUE;
			}
		}   
	}
}
