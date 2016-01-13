package risc16_pipeline;
import java.awt.*;



public class Alu extends Chip{
	private int fonction=0;
	private int eq = 0;
	private CtlSignal ctl;
	private boolean rctl=false; // l ALU a t il recu le signal de controle ?
	private boolean beq = false;
	

	//////////////////////////////////////////////////////////////////////
	public Alu(int x,int y,int lg,int ht,Bus out,Bus eq,Bus in1,Bus in2) {
		super(x,y,lg,ht);
		
		Bus[] temp = new Bus[2];
		temp[0]=out;
		temp[1]=eq;
		super.setOutput(temp);

		Bus[] temp2 = new Bus[2];
		temp2[0]=in1;
		temp2[1]=in2;
		super.setInput(temp2);
	}
	//////////////////////////////////////////////////////////////////////
	public void setCtlBus(CtlSignal ctl){this.ctl=ctl; ctl.getId();}     //define output

	public void reset(){
		super.reset();
		fonction = 0;
		delay=0;
		rctl=false;
	}
	/////////////////////////////////////////////////////////////////////
	public boolean checkInput(){  // n effectue des actions que lorsqu il recoit signal controle
		if (ctl.isActive()){
			fonction = ctl.getData();
			rctl=true;
		}
		return rctl;
		
	}
	/////////////////////////////////////////////////////////////////////
	public void receive(){
		System.out.println("ALU > \t receiving data ["+fonction+"]");
		compute(receive(0), receive(1));
	}
	////////////////////////////////////////////////////////////
	public void compute(int op1,int op2){
		rctl=false;
		System.out.println("ALU > \t computing ["+fonction+" | "+getMode()+"]  op1 = "+op1+"   op2="+op2);
		switch(fonction){
		case 0:  setData(add(op1,op2));     break;//c'est un add
		case 1:  setData(nand(op1,op2));    break;//c'est un nand
		case 2:  setData(op1);              break;//c'est un pass 1
		case 3://c'est un eq  --> sortie sur le bus EQ (ssi demandé par FUNC alu !)
			
			beq = true;
			if(op1==op2) eq=1;
			else eq=0;
			break;
		}}




	////////////////////////////////////////////////////////////
	public void latch(){
		if (beq){
			Bus[] out = new Bus[2];
			out=getOutput();
			out[1].receive(eq);
			beq=false;
		}else
			super.latch();
	}
	////////////////////////////////////////////////////////////
	public int nand(int op1,int op2){

		int temp = op1 & op2;
		temp = ~temp;//en décimal on peut encore avoir des nombres négatifs...
		if(temp<0){
			//temp = temp + 65536;
			temp &=0x0000FFFF;
		}
		return temp;
	}

	//////////////////////////////////////////////////// chip.latch --> latch slmt out[0] ---> OK


	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public String getMode(){
		switch(fonction) {
		case 0:    return "ADD";
		case 1:    return "NAND";
		case 2:    return "PASS2";
		case 3:    return "EQ?";
		default:   return "-";
		}}
	//////////////////////////////////////////////////////////////////
	public void paint(Graphics g){
		int X[]=new int[7];
		int Y[]=new int[7];
		X[0]=super.getX();
		X[4]=super.getX()+super.getLg();
		X[2]=(X[0]+X[4])/2;
		X[1]=X[2]-10;
		X[3]=X[2]+10;
		X[5]=X[4]-20;
		X[6]=X[0]+20;
		Y[0]=super.getY();
		Y[1]=Y[0];
		Y[2]=Y[1]+10;
		Y[3]=Y[0];
		Y[4]=Y[0];
		Y[5]=Y[0]+super.getHt();
		Y[6]=Y[5];

		g.setColor(getColor());
		g.fillPolygon(X,Y,7);
		g.setColor(Color.black);
		g.drawPolygon(X,Y,7);
		
		printText(g,18,"ALU",  super.getX()+super.getLg()/3, super.getY()+2*super.getHt()/3-5, Color.black);
		
		if (state==1 || state==2){
			g.drawString("["+getMode()+"]",super.getX()+2*super.getLg()/3-60,super.getY()+super.getHt()-5);
			g.drawString("[0x"+Integer.toHexString(readInput(0)).toUpperCase()+"]",super.getX()-38,super.getY()-5);
			g.drawString("[0x"+Integer.toHexString(readInput(1)).toUpperCase()+"]",super.getX()+super.getLg()/2-12,super.getY()-5);
			if(state==2 && fonction!=3) g.drawString("[0x"+Integer.toHexString(getData()).toUpperCase()+"]",super.getX()+2*super.getLg()/3-20,super.getY()+super.getHt()+10);
		}
		g.drawString("op2",super.getX()+10,super.getY()+12);
		g.drawString("op1",super.getX()+super.getLg()-35,super.getY()+12);
	}
	//////////////////////////////////////////////////
}
