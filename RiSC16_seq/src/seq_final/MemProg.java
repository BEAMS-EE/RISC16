package seq_final;
import java.awt.*;
//import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
//import java.lang.System;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;



public class MemProg extends Memoire {

	/**
	 * 
	 */
	private static final long serialVersionUID = -781133839929335937L;
	private Hashtable<String, Integer> labelTable;
	/**
	 * List of addresses holding orphan labels (i.e. labels without instruction).
	 */
	private ArrayList<Integer> orphanLabels;

	//  private JButton assemblage;
	//  private Bus address;

	//================================================================================================
	//   INITIALISATION
	//================================================================================================
	public MemProg(String title, int x, int y, int lg, int ht, String[] columnNames, Bus output) {
		super(title, x, y, lg, ht, Color.cyan/*new Color(252,111,26)*/, columnNames, output);
		super.getJButtonAss().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				assembler();			
			}});
		super.getJButtonRM().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMemory();			
			}});
	}
	/////////////////////////////////////////////////////////////////////SEQ c comme ci dessous qu'on init la ROM
	public MemProg(String title, int x, int y, int lg, int ht,String[] columnNames, Bus output,Bus address) {
		this(title, x, y, lg, ht, columnNames, output);
		super.setBusAddr(address);
	}
	/////////////////////////////////////////////////////////////////////


	//================================================================================================
	//   CHIP
	//================================================================================================
	public void latch(){
		if (getCurrentAddr()>=0) {
			//String instr = getIns(super.getCurrentAddr(), false);//si on laisse �a, on doit m�me plus appuyer sur assembler mais la colonne du milieu n'est pas raffraichie...
			String instr = getCase(super.getCurrentAddr(),1);

			if (instr.indexOf("error") != -1)  // si c est une erreur >> nop
				instr = "0000000000000000";
			else {
				instr=Integer.toBinaryString(Integer.decode(instr));//hextobin
				while (instr.length()<16) instr="0"+instr;
			}
			//system.out.println("MEM >   writing word (sur le bus de sortie) =    "+instr);
			super.getOutput().receive(instr);
		}}


	//================================================================================================
	//   MEMORY
	//================================================================================================
	public void assembler() {

		if(this.labelTable == null) {
			this.labelTable = new Hashtable<String, Integer>();
		}
		for (int i = 0; i < super.getAddressMax(); ++i) {
			getLabel(i, super.getCase(i, 2));
		}
		
		System.out.println("Print dat table.");
		System.out.println(labelTable);
		
		for (int i = 0;i < super.getAddressMax() ; i++) {
			String instruction = getIns(i,true);
			if ( instruction.toLowerCase().indexOf("movi") != -1){//si c'est un movi
				this.movi(instruction, i);//permet de d�composer en "lui" et "addi" mais il faut encore assembler
				instruction = getIns(i,true); // et donc on doit prendre le "lui" ici
			}

			if ( instruction.indexOf("error") == -1){
				instruction=Integer.toHexString(Integer.parseInt(instruction,2)).toUpperCase();
				while(instruction.length()<4) instruction="0"+instruction;
				instruction="0x"+instruction;}//bintohex
			super.setCase(instruction, i, 1);}  
	}
	
	
	public void resetMemory(){
		for(int i=0;i < super.getAddressMax() ; i++) {
			super.setCase(Integer.toString(i),i,0);
			super.setCase("nop",i,2);
			super.setCase("0x0000", i, 1);
			super.setCaseB(false,i,3);
		}
		labelTable=null;
	}
	// ////////////////////////////////////////////////////////////////////////////
	
	public void getLabel(int address, String command) {
		if(command != null) {
			// Split 'command'.
			StringTokenizer st = new StringTokenizer(command,", \t\n\r\f");
			// Check if the first token is a label.
			String firstToken = st.nextToken().toLowerCase();
			Pattern p = Pattern.compile("^[\\w]*:");
			Matcher m = p.matcher(firstToken);
			// If it is, set it as the address, and fetch the opcode (next token).
			if (m.matches()) {
				firstToken = firstToken.substring(0, firstToken.length()-1); // Remove the trailing ':'.
				if(this.labelTable.get(firstToken)==null) {
					this.labelTable.put(firstToken.toLowerCase(), address);//TODO Should we really put it to lowerCase?
				}
				//TODO If the label is already in the table, it should be discarded (like when we import a ROM).
				super.setCase(firstToken, address, 0);
				// As for the command, it needs to be stripped from its label.
				// We thus write the same command, but beginning from /after/ the label.
				// The '+1' is there to ignore the space after the ':' in the syntax '<label>: command'.
				super.setCase(command.substring(firstToken.length()+1), address, 2);//TODO Here we should trim the extra leading space.
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns back the opcode of the instruction at the given address.
	 * 
	 * @param a Address
	 * @param assemb True if assembling
	 * @return String of the opcode on 3 bits.
	 */
	public String getIns(int a, boolean assemb) {//assemb => true si on est en train d'assembler
		//renvoie le string equivalent de l'instruction en bit a l'adresse int a
		//et trouve le format de l'instruction

		// If the line is empty, this is a 'nop'.
		if ( super.getCase(a, 2)==null){  //on decompose la chaine en sous chaine contenu ds un vect
			super.setCase("nop",a,2);
			return "0000000000000000";
		}
		else {
			String asm= new String(super.getCase(a, 2));// Get shit from the third column at the line 'a'.
			if ( asm.length()<3)
				return "0000000000000000";

			String[] sTab = new String[3];// Tokens of the ASM line.
			String sol = new String();// opcode (3-bit)
			int format=0;  // 0=RRR 1=RRI 2=RI; R=register, I=immediate

			//-------------------------------------------------------------------------------------
			// INSTRUCTION !
			//-------------------------------------------------------------------------------------
			StringTokenizer st = new StringTokenizer(asm,", \t\n\r\f");
			String opcode = "";
			
			
			// Check if the first token is a label.
			String firstToken = st.nextToken().toLowerCase();
//			Pattern p = Pattern.compile("[a-zA-Z]*:");
//			Matcher m = p.matcher(firstToken);
//			// If it is, set it as the address, and fetch the opcode (next token).
//			if (m.matches()) {
//				if(labelTable.get(firstToken)!=null) {
//					labelTable.put(firstToken.toLowerCase(), a);
//				}
//				//TODO If the label is already in the table, it should be discarded (like when we import a ROM).
//				super.setCase(firstToken, a, 0);
//				// As for the command, it needs to be stripped from its label.
//				// We thus write the same command, but beginning from /after/ the label.
//				// The '+1' is there to ignore the space after the ':' in the syntax '<label>: command'.
//				super.setCase(asm.substring(firstToken.length()+1), a, 2);
//				opcode = st.nextToken().toLowerCase();
//			}
//			else {
				opcode = firstToken;
//			}
			
			
			
			if (opcode.indexOf("addi") != -1) { // il faut d abord mettre addi avant add !!!
				sol = "001";
				format = 1;
			}
			else if (opcode.indexOf("add") != -1) {
				sol = "000";
				format = 0;
			}
			else if (opcode.indexOf("nand") != -1) {
				sol = "010";
				format = 0;
			}
			else if (opcode.indexOf("lui") != -1) {
				sol = "011";
				format = 2;		
			}
			else if (opcode.indexOf("sw") != -1) {
				sol = "101";
				format = 1;			
			}
			else if (opcode.indexOf("lw") != -1) {
				sol = "100";
				format = 1;			
			}
			else if (opcode.indexOf("beq") != -1) {
				sol = "110";
				format = 1;				
			}
			else if (opcode.indexOf("jalr") != -1) {
				sol = "111";
				format = 1;
			}
			else // == pseudo instruction ==
				if (opcode.indexOf("nop") != -1) {
					return "0000000000000000";
				}
				else if (opcode.indexOf("reset") != -1) {
					return "1110000000000000";    // = JALR 0,0,0
				}
				else if (opcode.indexOf("halt") != -1){
					return "1110000001111111";
				}
				else if(opcode.indexOf("movi") != -1){
					return asm;
				}
				else {
					warning("error : bad instruction", a, assemb);
					return "error : bad instruction";
				}

			//-------------------------------------------------------------------------------------
			//  d�composition du format  -> sTab[0,1,2]
			//-------------------------------------------------------------------------------------
			
			for(int i=0;i<3;i++){
				if (st.hasMoreTokens())	{
					sTab[i]=st.nextToken();
				}
			}
			///////////////////////////////////////////////
			String RegA = new String();
			String RegB = new String();
			String RegC = new String();

			if (sTab[0] == null || sTab[1] == null){
				warning("error : format error",a, assemb);   
				return "error : format error";
			}



			//system.out.println("==["+asm+"]=="+sol+"==="+sTab[0]+"==="+sTab[1]+"==="+sTab[2]);
			// [ RegA ]
			// RegA = Integer.toBinaryString( (int) (sTab[0].charAt(0) - '0'));//'0' => 110000 (300 est pris pour un 3 par ex)
			RegA = Integer.toBinaryString( Integer.parseInt(sTab[0]));
			////system.out.println("REGAAAAAAAA"+RegA+"T"+sTab[0]);
			if (RegA.length() > 3){
				warning("error : format error (rA)"+"\n"+"The RiSC-16 contains only 8 registers (0->7)",a, assemb);   	  
				return "error : format error (rA)";
			}
			while (RegA.length() < 3)     RegA = "0" + RegA;

			if (assemb) {
				if (!sol.equals("100") && !sol.equals("110")
						&& RegA.equals("000")) {
					warning1("Warning : R0 is put as destination!",a, assemb);
				}


			}

			switch (format) {
			//-------------------------------------------------------------------------------------
			//  RRR
			//-------------------------------------------------------------------------------------
			case 0: {
				if (sTab[2] == null){
					warning("error : data missing (RRR type)",a,assemb);
					return "error : data missing (RRR type)";
				}
				//RegB = Integer.toBinaryString( (int) (sTab[1].charAt(0) - '0'));
				RegB = Integer.toBinaryString( Integer.parseInt(sTab[1]));
				if (RegB.length() > 3){
					warning("error : format error (rB)"+"\n"+"The RiSC-16 contains only 8 registers (0->7)",a, assemb);   	  
					return "error : format error (rB)";
				}
				while (RegB.length() < 3)     RegB = "0" + RegB;
				//RegC = Integer.toBinaryString( (int) (sTab[2].charAt(0) - '0'));
				RegC = Integer.toBinaryString( Integer.parseInt(sTab[2]));
				if (RegC.length() > 3){
					warning("error : format error (rC)"+"\n"+"The RiSC-16 contains only 8 registers (0->7)",a, assemb);   	  
					return "error : format error (rC)";
				}
				while (RegC.length() < 3)     RegC = "0" + RegC;
				sol = sol + RegA + RegB + "0000" + RegC;
				break;
			}
			//-------------------------------------------------------------------------------------
			//  RRI  (63 > -64)
			//-------------------------------------------------------------------------------------
			case 1: { //format RRI-type
				
//				System.out.println("sTab[]: '" + sTab[0] + "', '" + sTab[1] + "', '" + sTab[2] + "'");
				
				// Missing data, throw warning.
				if (sTab[2] == null && opcode.indexOf("jalr") == -1){
					warning("error : data missing (RRI type)",a,assemb);
					return "error : data missing (RRI type)";
				}


				// Extract register B from the second token.
				RegB = Integer.toBinaryString( Integer.parseInt(sTab[1]));
				if (RegB.length() > 3){
					warning("error : format error (rB)"+"\n"+"The RiSC-16 contains only 8 registers (0->7)",a, assemb);   	  
					return "error : format error (rB)";
				}
				while (RegB.length() < 3)     RegB = "0" + RegB;

				
				if (opcode.indexOf("jalr") == -1) {
					int imm;
					try {
						imm=Integer.decode(sTab[2]);
					} catch (NumberFormatException e) {	// si il s'agit d'un label
						try {
//							imm=labelTable.get(sTab[2])-(a+1);
							// opcode is BEQ, relative jump
							if(sol == "110") {
//								System.out.println("I'm BEQ, I want that label from that table.");
//								System.out.println("Label is '" + sTab[2] + "'");
								imm = labelTable.get(sTab[2]) - (a + 1);
//								System.out.println("imm is '" + imm + "'");
							}
							// JALR is an absolute jump.
							else {
								imm = labelTable.get(sTab[2]);
							}
						} catch (Exception e1) {
							warning("error : Unknown label or format error",a,assemb);
							return "error : Unknown label or format error arg0";
						}
					}

					if (imm>63){
						warning("error : Imm too big (RRI type)",a,assemb);
						return "error : Imm too big (RRI type)";
					}
					if (imm<-64){
						warning("error : Imm too big (RRI type)",a,assemb);
						return "error : Imm too big (RRI type)";
					}
					if (imm<0) imm = 128+imm;  // si negatif !
					RegC = Integer.toBinaryString(imm);  //10  > decimal
					////system.out.println("IIIIIIIIIIM"+imm+"       TAB2    "+sTab[2]+"     REGC  "+ RegC);
					if (RegC.length() > 7)   {
						warning("error : Imm too big (RRI type)",a,assemb);
						return "error : Imm too big (RRI type)";
					}
					while (RegC.length() < 7)  RegC = "0" + RegC;
				}
				// If the opcode is JALR, the immediate is 0000000.ta
				else{
					RegC="0000000";
				}

				sol = sol + RegA + RegB +  RegC;
				break;
			}
			//-------------------------------------------------------------------------------------
			//  RI   0 1023 no limit en asm ! imm = (imm > 6) & 0x3FF
			//-------------------------------------------------------------------------------------
			case 2: { //format RI-type
				int imm;
				try {
					imm=Integer.decode(sTab[1]);
				} catch (NumberFormatException e) {
					warning("error : format error",a);   
					return "error : format error arg1";
				}
				if(imm>65472) {
					int newimm=((imm/64)& 0x3FF)*64;
					warning1("Imm too big (RI type)\nMax Value is : 65472 (0xFFC0)\nValue was remplaced by : "+newimm,a,assemb);
					setCase("lui "+sTab[0]+","+newimm, a, 2);
				}
//				else if(imm>0 && imm%64!=0){
//					int newimm=(imm/64)*64;
//					warning1("The accuracy of the value is limited to "+newimm,a,assemb);
//					setCase("lui "+sTab[0]+","+newimm, a, 2);
//				}
				imm = imm & 0x3FF;
				RegC = Integer.toBinaryString(imm);

				while (RegC.length() < 10)     RegC = "0" + RegC;
				sol = sol + RegA + RegC;
				break;
			}
			} // END switch

			if (sol.length() > 16) {
				warning("error : registers values too big",a,assemb);
				return "error : registers values too big";
			}
			////system.out.println(sol);
			return sol;
		}} //  END if !null +  END getIns()

	public void warning(String text, int a, boolean assemb){
		if(assemb){
			String text2 = new String();
			text2 = "\nLigne "
				+ a
				+ " :\n"+text+"\n"
				//+"The instruction is replaced with NOP \n\n"
				;
			JOptionPane.showMessageDialog(null, text2, "Warning : Program Memory",
					JOptionPane.WARNING_MESSAGE);
		}

		//	laMemoire.changeSelection(a, 1, false, false);
	}

	public void warning1(String text, int a, boolean assemb){
		if(assemb){
			String text2 = new String();
			text2 = "\nLigne "
				+ a
				+ " :\n"+text+"\n";
			JOptionPane.showMessageDialog(null, text2, "Warning : Program Memory",
					JOptionPane.INFORMATION_MESSAGE);
		}

		//	laMemoire.changeSelection(a, 1, false, false);
	}


	public void movi(String s, int i){
		String movi;
		String rx="0";
		String intermediaire="0";

		StringTokenizer st= new StringTokenizer(s,", \t\n\r\f");
		if (st.hasMoreTokens()) movi=st.nextToken();
		if (st.hasMoreTokens()) rx=st.nextToken();
		if (st.hasMoreTokens()) intermediaire=st.nextToken();


		int immhi = (Integer.decode(intermediaire) >> 6) & 0x03FF;// Get the 10 high bits to the lower bits.
		int immlo = Integer.decode(intermediaire) & 0x003F;
		String immH = Integer.toString(immhi);
		String imml=Integer.toString(immlo);
		s="lui "+rx+","+immH;
		setCase(s, i, 2);
		++i;
		s="addi "+rx+","+rx+","+imml;
		setCase(s, i, 2);

	}



	//================================================================================================
	//   FILE ACCESS
	//================================================================================================

	public void fileopen() {
		String p= new String("");
		this.fileopen(p);
	}
	
	public void fileopen(String path) {
		int i = 0;
		String s = "";
		Fich fi;
		orphanLabels = new ArrayList<Integer>();


		if (path.length()>3)    
			fi = new Fich(path);
		else                    {
			fi = new Fich();
			fi.open();
		}



		if (fi.isOpen()) {
			s = fi.getLine();

			int address=0; 
			String label="",opcode="",arg0,arg1,arg2,arg3,instructionwhitoutlabel = "";
			labelTable=new Hashtable<String, Integer>();

			// Will be null when fi.getLine() returns null, that is the end of the file.
			while (s!=null){
				StringTokenizer st=new StringTokenizer(s);
				// Analyze the tokens, but only looking for special ones,
				// like comments, labels, addresses, 'movi', etc.
				if (st.hasMoreTokens()){
					String firsttoken = st.nextToken();
					// If comment line, ignore it.
					if (firsttoken.indexOf("//")==0 || firsttoken.indexOf("#")==0){
						label="";
						opcode="";
						s = fi.getLine();
						continue;
					}
					// Instruction to be placed at a specific address. Extract the address, omitting the "@".
					else if(firsttoken.indexOf("@")==0){
						s = firsttoken.substring(1, firsttoken.length());
						address = Integer.decode(s)-1;
						label="";
						opcode="";
						continue;
					}
					// Ending with ":" => label.
					else if(firsttoken.charAt(firsttoken.length()-1)==':'){
						label = firsttoken.substring(0, firsttoken.length()-1);
						if (st.hasMoreTokens()){
							opcode=st.nextToken();
						}
						// Mark the label as orphan for later processing.
						else {
							orphanLabels.add(address);
						}
					}
					else {
						label="";
						opcode = firsttoken;
					}
					// If it was a label
					if (label!=""){
						// If the label is already in the table, it means
						// that the label is already in use and should be
						// ignored.
						// TODO Discarding a label for multiple use should raise a warning.
						if(labelTable.get(label)!=null){
							setCase(String.valueOf(address),address,0);
						}
						// Label not in the labelTable yet.
						else{
							labelTable.put(label.toLowerCase(),address);
							setCase(label,address,0);
						}
					} else {
						setCase(String.valueOf(address),address,0);
					}
					// If it is a "movi" opcode, skip an address, as it is an alias for two operations: LUI, then ADDI.
					if(opcode.toLowerCase().indexOf("movi") != -1) {
						address+=2;
//						System.out.println("if movi boucle 1 fileopen");	
					}
					else address++;
				}
				s = fi.getLine();
			}

		
			// Open the file a second time.
			//TODO This is not good performance-wise. Do not open the same file twice in a row.
			fi = new Fich(fi.getPath());
			s = fi.getLine();
			while (s != null){
				if(s.indexOf("@")==0){//aller � l'adresse @XXXX
					int j=0;
					s = s.substring(1, s.length());// Address to go to.
					i = Integer.decode(s);// Integer version of the address
					s="nop";
					while(j<i){
						if(getCase(j,2) == null){
							setCase(s, j, 2);
						}
						++j;
					}	   
				}else{
					// If there is a comment after the command, remove it.
					if(s.indexOf("//") > 0) s = s.substring(0, s.indexOf("//"));// Comment after the instruction.
					if(s.indexOf("#") > 0) s = s.substring(0, s.indexOf("#"));
					if(s.indexOf("//") == -1  && s.indexOf("#") == -1){// No more comments

						s = s.trim();
						if(s.length() != 0){

							StringTokenizer st=new StringTokenizer(s);
							if (st.hasMoreTokens()){
								String firsttoken = st.nextToken();
								System.out.println(firsttoken);

								if(firsttoken.charAt(firsttoken.length()-1)==':'){
									label = firsttoken.substring(0, firsttoken.length()-1);
									if (st.hasMoreTokens()) opcode=st.nextToken();				
								}
								else {
									label="";
									opcode = firsttoken;
								}
								instructionwhitoutlabel=opcode;
								if (st.hasMoreTokens()) {
									arg0=st.nextToken(", \t\n\r\f");
									instructionwhitoutlabel+=" "+arg0;
								}
								if (st.hasMoreTokens()) {
									arg1=st.nextToken(", \t\n\r\f");
									instructionwhitoutlabel+=", "+arg1;
								}
								if (st.hasMoreTokens()) {
									arg2=st.nextToken(", \t\n\r\f");
									instructionwhitoutlabel+=", "+arg2;
								}
								if (st.hasMoreTokens()) {
									arg3=st.nextToken(", \t\n\r\f");
									instructionwhitoutlabel+=", "+arg3;
								}
								if(opcode.toLowerCase().indexOf("movi") != -1) {//si l'instruction est un movi 
									movi(instructionwhitoutlabel.trim(),i);
									++i;
								}else{//si ce n'est pas un movi
									setCase(instructionwhitoutlabel.trim(), i, 2);
								}
							}
							// If the current address is an orphan label, do not increment the address and free the orphan.
							if(orphanLabels.contains(i)) {
								orphanLabels.remove(i);
							}
							else {
								i++;
							}
						}
//						else {
//							System.out.println("Empty line!");
//						}
					}
				}
				s = fi.getLine();
			}
			fi.openclose(); // ferme le stream
		}
		
		this.assembler(); // assemblage a la fin du chargement
	}

	
	//////////////////////////////////////////////////////////////////////////////
	public void filesave() {
		Fich fi = new Fich();
		if(fi.save()){
			int i = 0;
			boolean adressWrite=false;
			String adress;


			while(i<addressMax){

				
					adress = Integer.toString(i);
		
				if(super.getCase(i,2).indexOf("nop")== -1){
					if(adressWrite){
						fi.setLine("@"+adress);
						
						try {
							Integer.decode(super.getCase(i,0));
							fi.setLine(super.getCase(i, 2));
						} catch (Exception e) {
							fi.setLine(super.getCase(i,0)+": "+super.getCase(i, 2));
						}
						
						adressWrite=false;
					}else{
						try {
							Integer.decode(super.getCase(i,0));
							fi.setLine(super.getCase(i, 2));
						} catch (Exception e) {
							fi.setLine(super.getCase(i,0)+": "+super.getCase(i, 2));
						}
					}

				}else{
					adressWrite=true;
				}


				++i;
			}

			////system.out.println("last get case"+super.getCase(i, 2));
			fi.saveclose();
		}
	}

	//================================================================================================
	//   GRAPHICs
	//================================================================================================
	public void dessine(Graphics g) {
		super.dessine(g);
		printText(g,18,"PROG",X()+getLg()/2-27,Y()+getHt()/2-5,Color.black);
		printText(g,18,"MEM",X()+getLg()/2-10,Y()+getHt()/2+15,Color.black);
		//     g.drawString("ADDR",   X() + 3,          Y() + getHt()/2 );
		//     g.drawString("OUT", X() + getLg()/2+10,Y() + getHt()-3);
	}

	////////////////////////////////////////////////////////////////

}
