package editor;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

/**
 * 
 * @author ENGLEBIN Laurent
 */
public class MemProg extends Memoire {


	private Hashtable<String, Integer> labelTable;
	/**
	 * List of addresses holding orphan labels (i.e. labels without instruction).
	 */
	private ArrayList<Integer> orphanLabels;
	private Architecture architecture;


	private static final long serialVersionUID = -781133839929335937L;



	//================================================================================================
	//   INITIALISATION
	//================================================================================================
	public MemProg(String title, String[] columnNames,Architecture arch) {
		super(title,columnNames,arch);
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
		this.architecture=arch;
	}
	





	//================================================================================================
	//   MEMORY
	//================================================================================================
	
	public void assembler() {

		moveToFront();
		try {
			setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		for (int i = 0;i < super.getAddressMax() ; i++) {
			String instruction = getIns(i,true);
			if ( instruction.toLowerCase().indexOf("movi") != -1){//si c'est un movi
				this.movi(instruction, i);//permet de d�composer en "lui" et "addi" mais il faut encore assembler
				instruction = getIns(i,true); // et donc on doit prendre le "lui" ici
			}

			if ( instruction.indexOf("error") == -1){
				instruction=Integer.toHexString(Integer.decode(instruction)).toUpperCase();
				while(instruction.length()<Math.ceil(architecture.instructionSize/4.0)) instruction="0"+instruction;
				instruction="0x"+instruction; //bintohex
			}
			super.setCase(instruction, i, 1);
		}  
	}

	public String getIns(int a, boolean assemb) {
		
		String instructioncodee; 
		if ( super.getCase(a, 2)==null){  //on decompose la chaine en sous chaine contenu ds un vect
			super.setCase("nop",a,2);
			return "0";
		}
		else {
			String asm= new String(super.getCase(a, 2));
					
			String op="add";
			int arg0=0,arg1=0,arg2=0,arg3=0;
			String[] args=new String[4];
			
			String format="";

			StringTokenizer st = new StringTokenizer(asm,", \t\n\r\f");
			if (st.hasMoreTokens())	{
				op=st.nextToken().toLowerCase();
				format=architecture.getFormat(op);
			}
			if (st.hasMoreTokens())	{
				arg0=Integer.decode(st.nextToken());
			}
			else if (format.equals("RRR") || format.equals("RRI") || format.equals("RI")){
				warningMessage("error : data missing ("+format+" type)",a);   
				return "error : data missing ("+format+" type)";
			}
			if (st.hasMoreTokens())	{
				try {
					arg1=Integer.decode(st.nextToken());
				} catch (NumberFormatException e) {
					warningMessage("error : format error",a);   
					return "error : format error arg1";
				}
				}
			else if (format.equals("RRR") || format.equals("RRI") || format.equals("RI")){
				warningMessage("error : data missing ("+format+" type)",a);   
				return "error : data missing ("+format+" type)";
			}
			if (st.hasMoreTokens())	{
				String a2=st.nextToken();
				try {
					arg2=Integer.decode(a2);
				} catch (NumberFormatException e) {
					// If BEQ, relative jump.
					if("beq".equals(op) || "bg".equals(op) || "bl".equals(op)) {
						arg2=labelTable.get(a2)-(a+1);
					}
					// In other cases (like ADDI), absolute jump.
					else {
						arg2 = labelTable.get(a2);
					}
				}
			}
			else if (format.equals("RRR") || format.equals("RRI") && !op.equals("jalr")){
				warningMessage("error : data missing ("+format+" type)",a);   
				return "error : data missing ("+format+" type)";
			}
			if (st.hasMoreTokens())	{
				//Overflow argument
				String a3=st.nextToken();
				try {
					arg3=Integer.decode(a3);
				} catch (NumberFormatException e) {
					arg3=labelTable.get(a3)-(a+1);
				}
			}
			

			if (op.indexOf("nop") != -1) {
				return "0000000000000000";
			}
			else if (op.indexOf("reset") != -1) {
				instructioncodee=architecture.assemble("jalr",0,0,0,0,a);    // = JALR 0,0,0
			}
			else if (op.indexOf("halt") != -1) {
				instructioncodee=architecture.assemble("jalr",0,0,-1,0,a);    // = JALR 0,0,0
			}
			else if(op.indexOf("movi") != -1){
				return asm;
			}
			else {
				instructioncodee=architecture.assemble(op,arg0,arg1,arg2,arg3,a);
			}		
			return instructioncodee;
		}
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
	private void warningMessage(String text, int a){	
		String text2 = new String();
		text2 = "\nLigne "
			+ a
			+ " :\n"+text+"\n";
		JOptionPane.showMessageDialog(null, text2, "Warning : Program Memory",
				JOptionPane.WARNING_MESSAGE);

}
	
	
	

	public void movi(String s, int i){
		String movi;
		String rx="0";
		String intermediaire="0";

		StringTokenizer st= new StringTokenizer(s,", \t\n\r\f");
		if (st.hasMoreTokens()) movi=st.nextToken();
		if (st.hasMoreTokens()) rx=st.nextToken();
		if (st.hasMoreTokens()) intermediaire=st.nextToken();


		int immhi = (Integer.decode(intermediaire) >> 6) & 0x03FF;
//		int immhi = (Integer.decode(intermediaire)) & 0xFFC0;
		int immlo = Integer.decode(intermediaire) & 0x003F;
//		int immhi=Integer.decode(intermediaire);
//		int immlo=immhi%64;
//		immhi=(immhi/64)%64;
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

	public void fileopen(String path) {
		int i = 0;
		String s = "";
		Fich fi = new Fich(path);
		orphanLabels = new ArrayList<Integer>();

		if (fi.isOpen()) {
			s = fi.getLine();

			int address=0; 
			String label="",opcode="",arg0,arg1,arg2,arg3,instructionwhitoutlabel = "";
			labelTable=new Hashtable<String, Integer>();

			// premi�re passe pour trouver les labels
			while (s!=null){
				StringTokenizer st=new StringTokenizer(s);
				if (st.hasMoreTokens()){
					String firsttoken = st.nextToken();
					if (firsttoken.indexOf("//")==0 || firsttoken.indexOf("#")==0){
						label="";
						opcode="";
						s = fi.getLine();
						continue;
					}
					else if(firsttoken.indexOf("@")==0){
						s = firsttoken.substring(1, firsttoken.length());
						address = Integer.decode(s)-1;
						label="";
						opcode="";
						continue;
					}
					else if(firsttoken.charAt(firsttoken.length()-1)==':'){
						label = firsttoken.substring(0, firsttoken.length()-1);
						if (st.hasMoreTokens()){
							opcode=st.nextToken();
						}
						else {
							orphanLabels.add(address);
						}
					}
					else {
						label="";
						opcode = firsttoken;
					}
					if (label!=""){
						if(labelTable.get(label)!=null){	// label dupliqu�
							setCase(String.valueOf(address),address,0);
						}
						else{
							labelTable.put(label,address);
							setCase(label,address,0);
						}
					} else {
						setCase(String.valueOf(address),address,0);
					}
					if(opcode.toLowerCase().indexOf("movi") != -1) {
						address+=2;
					}
					else address++;
				}
				s = fi.getLine();
			}


			// seconde passe
			fi = new Fich(path);
			s = fi.getLine();
			while (s != null){
				if(s.indexOf("@")==0){//aller � l'adresse @XXXX
					int j=0;
					s = s.substring(1, s.length());
					i = Integer.decode(s);
					s="nop";
					while(j<i){
						if(getCase(j,2) == null){
							setCase(s, j, 2);
						}
						++j;
					}	   
				}else{
					if(s.indexOf("//") > 0) s = s.substring(0, s.indexOf("//")); // si on met un commentaire apr�s l'instruction
					if(s.indexOf("#") > 0) s = s.substring(0, s.indexOf("#")); // si on met un commentaire apr�s l'instruction
					if(s.indexOf("//") == -1  && s.indexOf("#") == -1){//permet d'ajouter des commentaires � l'aide de //

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
							if(orphanLabels.contains(i)) {
								orphanLabels.remove(i);
							}
							else {
								i++;
							}
						}
					}
				}
				s = fi.getLine();
			}
		}
		fi.openclose(); // ferme le stream
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
						fi.setLine(super.getCase(i, 2));
						adressWrite=false;
					}else{
						fi.setLine(super.getCase(i, 2));
					}

				}else{
					adressWrite=true;
				}
				++i;
			}
			fi.saveclose();
		}
	}
}
