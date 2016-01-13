package risc16_pipeline;
import java.awt.*;

public class InstrReg extends Register{
	private int nbBit; //ici > 16
	private int format; //il existe 3 formats : RRR 1,RRI 2 ,RI 3;
	private int iOp=0;
	private String opcode = ""; // op code > asm

	/////////////////////////////////////////////////////////////////////
	public InstrReg(int x, int y, int lg, int ht, Bus input) {
		super("IR", x, y, lg, ht);
		super.setInput(input);
		nbBit = 16;
		format = 0;
		setWord("0000000000000000");
	}

	/////////////////////////////////////////////////////////////////////
	public InstrReg(int x, int y, int lg, int ht, Bus input, Bus[] output) {
		this(x, y, lg, ht, input);
		super.setOutput(output);
	}

	///////////////////////////////////////////////////////////////////

	public void reset(){    super.reset(); format =0;opcode="???"; setWord("0000000000000000");}//???est écris lorsqu'on fait un reset
	//////////////////////////////////////////////////////////////////
	public int getFormat(String word) {
		String temp=word;
		if (temp.length()>=3) temp=temp.substring(0,3);
		int i=Integer.parseInt(temp,2);
		if (i==0 || i==2) return 1;  //RRR
		if (i==1 || i==4 || i==5 || i==6 || i==7) return 2; //RRI
		if (i==3) return 3;          //RI
		return 0;
	}
	// *****************************************************************

	////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	public int getOp(){
		String temp=new String(getWord().substring(0,3));//on va chercher word dans chip (la partie de word prise est [1->3])
		iOp =Integer.parseInt(temp,2);//le 2 signifie que le string est un nombre binaire
		//system.out.println("iOp = "+iOp);
		return iOp;}
	/////////////////////////////
	public int getRegA(){
		String temp=new String(getWord().substring(3,6));
		//system.out.println("RegA contient : "+temp);
		return Integer.parseInt(temp,2);}
	/////////////////////////////
	public int getRegB(){
		String temp=new String(getWord().substring(6,9));
		return Integer.parseInt(temp,2);}
	//////////////////////////////
	public int getRegC(){
		String temp=new String(getWord().substring(13,16));
		System.out.println(temp);
		return Integer.parseInt(temp,2);}
	/////////////////////////////////
	public int getImm(){
		String temp=new String(getWord().substring(6,16));
		return Integer.parseInt(temp,2);}
	/////////////////////////////////
	public String getOpcode(){
		return opcode;
	}
	public void setWord(String word){

		super.setWord(word);
		format = getFormat(word);
		opcode     = super.getAsm(word);
	}

	public void  receive(){
		//system.out.println("rec IR");
		if(!super.isClocked()){
			receiveW();
			format = getFormat(getWord());
			opcode     = super.getAsm(getWord());
		}
	}


	public void latch(){
		Bus[] out=getOutput();
		out[0].receive(getOp());    // OP  =>receive(int)
		out[1].receive(getRegA());  // rA
		if(iOp!=3)//3=>Lui=>on ne montre pas que SRC1 s'active
			out[2].receive(getRegB());  // rB
		if(iOp==0 || iOp==2)//0=>ADD,2=>NAND
			out[3].receive(getRegC());  // rC
		if (iOp!=0 && iOp!=2 && iOp!=7){//2=>NAND,7=>JALR,0=>ADD
			out[4].receive(getImm());   // Imm
			out[3].reset();	// on remet à 0 regC qd imm sinon risque de foirage avec stall
		}
	}

	public void  L(){
		//    //system.out.println("IR.L");
		//if (super.checkInput()){
		//    //system.out.println("IR.L ok receive");
		if (!getStall())
			receiveW();
		else resetStall();


		if (getStomp())
		{resetStomp() ;  super.setWord("0000000000000000");}
		format = getFormat(getWord());
		opcode     = super.getAsm(getWord());



		super.setBusy();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void paint(Graphics g){
		drawRect(g);       // on ne peut pas utiliser super.paint() -> (chip).drawRect(g);
		g.drawLine(super.getX() + (3 * super.getLg()/nbBit), super.getY(),
				super.getX() + 3 * super.getLg() / nbBit, super.getY() + super.getHt());
		// === TEXTE ===
		g.setColor(Color.BLACK);
		if (isBusy()) g.drawString("???",super.getX()-35,super.getY()+(super.getHt()/2)+15);
		else g.drawString(opcode+"",super.getX()-35,super.getY()+(super.getHt()/2)+15);
		g.drawString("OpC",super.getX()+2,super.getY()-3);
		g.drawString("rA",super.getX()+3*super.getLg()/nbBit+2,super.getY()-3);
		if(getWord()!=null){
			if(format==1){  // RRR
				g.drawString("rC",super.getX()+13*super.getLg()/nbBit+2,super.getY()-3);
				g.drawString("rB",super.getX()+6*super.getLg()/nbBit+2,super.getY()-3);}
			if(format==2){ // RRI
				g.drawString("rB",super.getX()+6*super.getLg()/nbBit+2,super.getY()-3);
				g.drawString("SignImVal",super.getX()+9*super.getLg()/nbBit+4,super.getY()-3);}
			if(format==3){  // RRR
				g.drawString("10bit Imm Val",super.getX()+6*super.getLg()/nbBit+4,super.getY()-3);}

			if(isBusy()){
				g.setFont(new Font("Monospaced", Font.BOLD, 12));
				g.drawString("????????????????", super.getX()+ 2, super.getY() + 15);
				g.setFont(new Font("Monospaced", Font.PLAIN, 12));
			}else {
				String inst = new String(getWord());
				for(int i=0;i<nbBit;i++){
					String temp = new String();
					temp = temp + inst.charAt(i);
					g.drawString(temp, super.getX() - 1 + i * super.getLg() / nbBit + 2,
							super.getY() + 15);
					//  placement barres
					if  ( (format == 1 && (i == 3 || i == 6 || i == 9 || i == 13))
							|| (format == 2 && (i == 3 || i == 6 || i == 9))
							|| (format == 3 && (i == 3 || i == 6)) )
						g.drawLine(super.getX() + (i * super.getLg()/nbBit), super.getY(),
								super.getX() + i * super.getLg() / nbBit, super.getY() + super.getHt());
				}
			}
		}}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
