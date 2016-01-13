package seq_final;
import java.awt.*;


/**
 * 
 * @author ENGLEBIN Laurent
 */
public class RegistreBank extends Chip{
	private Register[] reg;
	private CtlSignal ctl;

	// ===CONSTANTE ===
	private int nbReg=8;

	private int tgtData=0,tgt=0,op1=0,op2=0;
	private boolean ctgt=false,c1=false,c2=false;    // si une nouvelle adresse est appliquée aux entrees ?
	private boolean WE=false;
	private RegTable regtable;

	////////////////////////////////////////////////////////////////////////////////////////////////
	public RegistreBank(int x,int y,int lg,int ht,Bus src1,Bus src2,Bus tgt,Bus ssrc1,Bus ssrc2,Bus stgt) {
		super(x,y,lg,ht);

		Bus[] input=new Bus[4];
		input[1]=ssrc1;  input[2]=ssrc2;  input[3]=stgt; input[0]=tgt;
		setInput(input);
		Bus[] output=new Bus[2];
		output[0]=src1;    output[1]=src2;
		setOutput(output);

		reg=new Register[nbReg];
		for(int i=0;i<reg.length;i++)
			reg[i]=new Register("R"+Integer.toString(i), x+40,y+20+i*(18+4),100,18);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void setCtlBus(CtlSignal ctl){this.ctl=ctl; ctl.getId();}
	////////////////////////////////////////////////////////////////////////////////////////////////
	private int getRegData(int i){
		if(i==0 || i>=reg.length)  return 0;   // registre 0 --> always return 0
		else                       return reg[i].getData();}
	////////////////////////////////////////////////////////////////////////////////////////////////
	private void setRegData(int data){
		if(tgt<reg.length)
			if(tgt!=0) {      reg[tgt].setData(data);regtable.write(tgt,data);}
			else             reg[0].setData(0);

	}
	public void setRegData(int data,int tgt){
		if(tgt<reg.length)
			if(tgt!=0)       reg[tgt].setData(data);
			else             reg[0].setData(0);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Change le format (via menu display)
	public void setFormat(int d){
		for(int i=0;i<reg.length;i++){
			reg[i].setFormat(d);
		}
		regtable.setFormat(d);
		if (d==3) setLg(205);
		else setLg(180);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	public void reset(){
		super.reset();
		for(int i=0;i<reg.length;i++){
			reg[i].reset();
			regtable.write(i,0);
		}
		tgt=0;    tgtData=0;   op1=0;    op2=0;
		ctgt=false;    c1=false;    c2=false;
		WE=false;
	}
	public void restoreRegTable(){
		for(int i=0;i<reg.length;i++){
			regtable.write(i,reg[i].getData());
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void act(){  

		++delay; 

		if (state==2 && delay==4){
			setIdle();
		}

		if (state==0 && checkInput()){
			setBusy();        
			delay=1;
		}
		if(state==1 && delay == 2){
			ctl.getData();
		}
		if (state==1 && delay==3){
			receive();

			setLatch();
			latch();
		}



	}
	/////////////////////////////////////////////////////////////////////////////////////
	public boolean checkInput(){   // TRUE = > ok entree dispo
		for(int i=0;i<reg.length;i++)
			reg[i].setIdle();
		if (ctl.isActive()) {
			WE = (ctl.readData()==1);
			//System.out.println("RF > \treceiving Ctl : WE = " + WE);     
		}

		if (isInActive(1) || isInActive(2)|| isInActive(3)) // src1  ou src2 ou tgt
		{
			//System.out.println("RF > \tcheck input  ->  SRC 1 ou SRC2");
			return true;
		}
		if (WE && (isInActive(0) || isInActive(3))) //    dataTGT   || selectTGT
		{
			//System.out.println("RF > \tcheck input  -> TGT");
			return true;
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	public void receive(){
		if (isInActive(1)) {
			op1=receive(1);
			if (op1>reg.length-1)  op1=0;
			reg[op1].setLatch();
			c1=true;
		}
		if (isInActive(2)) {
			op2=receive(2);
			if (op2>reg.length-1)  op2=0;
			reg[op2].setLatch();
			c2=true;
		}
		if (isInActive(3)) {  // select target
			tgt=receive(3);
		if (tgt>reg.length-1)  tgt=0;
		if (WE) {
			tgtData=receive(0);
			ctgt=true; reg[tgt].setBusy();
		}
		}
		if (isInActive(0) && WE) // s1 s2 || tgt & WE & stgt
		{
			tgtData=receive(0);
			ctgt=true; reg[tgt].setBusy();
			setRegData(tgtData);
		}
		super.setColorIdle();
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void latch(){
		super.setColorIdle();
		// setLatch();
		//state=0;//??????????
		Bus[]  out;
		out=super.getOutput();
		if (c1)   {out[0].receive(getRegData(op1));reg[op1].setLatch();}
		if (c2)   {out[1].receive(getRegData(op2));reg[op2].setLatch();}
		if (ctgt) {//setRegData(tgtData);
			reg[tgt].setIdle();
			reg[tgt].setColorChange();//il devient bleu après avoir écris dedans

			//System.out.println("RF > \twriting to reg# "+tgt+"   data= "+tgtData);
		}
		c1=false;c2=false;ctgt=false; WE=false;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	/*public Register target(){
	  return reg[tgt];
  }*/
	//////////////////////////////////////////////////////////////////////////////
	public void paint(Graphics g){
		super.paint(g);
		printText(g,14,"REGISTER BANK",getX()+9,getY()+15,Color.black);
		if(WE)
			printText(g,13,"WE!",getX()+getLg()-27,getY()+14,Color.red,1);
		if(op1==tgt){
			if(op1==op2){
				g.drawString("1,2,T",super.getX()+3,super.getY()+34+op1*22);
			}else{
				g.drawString("1,T",super.getX()+8,super.getY()+34+op1*22);
				g.drawString("2",super.getX()+12,super.getY()+34+op2*22);
			}
		}else{
			if(op2==tgt){
				g.drawString("2,T",super.getX()+8,super.getY()+34+op2*22);
				g.drawString("1",super.getX()+12,super.getY()+34+op1*22);
			}else{
				if(op1==op2){
					g.drawString("1,2",super.getX()+8,super.getY()+34+op1*22);
					g.drawString("T",super.getX()+12,super.getY()+34+tgt*22);
				}else{
					g.drawString("1",super.getX()+12,super.getY()+34+op1*22);
					g.drawString("2",super.getX()+12,super.getY()+34+op2*22);
					g.drawString("T",super.getX()+12,super.getY()+34+tgt*22);
				}
			}
		}

		g.drawString("data 2",getOutput()[1].getX()[0][0]-20,               super.getY()+super.getHt()-3);
		g.drawString("data 1",getOutput()[0].getX()[0][0]-20, super.getY()+super.getHt()-3);
		for(int i=0;i<reg.length;i++)
			reg[i].paint(g);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	public Register getReg(int i){
		return this.reg[i];
	}
	/*public void setReg(Register r,int i){
		this.reg[i]=r;
	}*/
	public void setRegTable(RegTable regtable){
		this.regtable=regtable;
	}
}
