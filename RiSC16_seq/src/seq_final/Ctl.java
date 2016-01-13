package seq_final;

import java.awt.*;

public class Ctl extends Chip {

  private CtlSignal[] ctl; // output (contient les signaux de controle)

  private int[] data;//data est envoyer aux signaux de contrôle pour voir lequel va être activé
  private int[] changed; // on affiche que si le signal change !
  private int clockState = 0;//donne l'état du micropro
  private int opcode = 0;//operande
  private int out1=-1,out2 = -1;
  private boolean RecOp = false;  
  private int delay=0;
  private int eq=0;


//////////////////////////////////////////////////////
  public Ctl(int x, int y, int r) {
    super(x, y, r, new Color(237,27,52));
  }

//////////////////////////////////////////////////////

  public void setSignal(CtlSignal[] sign) {
    ctl = sign;
    data = new int[ctl.length];
    changed = new int[ctl.length];
    for (int i = 0; i < data.length; i++) {
      data[i] = 0;
      changed[i] = 0;
    }
  }

//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////
  public void incrState(){
	  clockState++; // incrémente l'état à chaque coup d'horloge
      if (clockState==21){
    	  clockState=1;//12coups d'horloge
    	  delay=0;
      }
  }
  public int getState() {
    return clockState;}
//////////////////////////////////////////////////////
  public void reset() {
	delay=0;
    super.reset();
    data = new int[ctl.length];
    changed = new int[ctl.length];
    for (int i = 0; i < data.length; i++) {
      data[i] = 0;
      changed[i] = 0;
    }
    clockState = 0;
    opcode=0;
    out1 = -1;
    out2 = -1;
  }
//////////////////////////////////////////////////////
  public void act() {      
	  if(opcode==6 && clockState==16){//si BEQ on reste busy
		  setBusy();
		  RecOp=true;
	  }else{
		  setIdle();//inactif
	  }

  // in = OP              out =  ..
      //   [0]=FCal; [1]=MUXa1; [2]=MUXa2; [3]=MUXpc; [4]=MUXrf; [5]=MUXtg;  [6]=WEr; [7]=WEm; [8]=PC0Read; [9]=PSEN

          System.out.println("CTL > state= " + clockState + "  OP= " + opcode);
         

          if (isInActive(0)) {//est ce que ctl a l'opcode à son entrée?
        	  setBusy();
        	  RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
        	  if(delay==1){                  
                  System.out.println("CTL > (" + clockState + ") : op decode");
                  opcode = receive(0);//reçoit l'opcode venant du IR et désactive le signal donnant l'opcode
                  //RecOp=true; //ici?
        	  }
        	  ++delay;
          }


          switch (clockState) {

          			
                case 8:
    //==== MUX TGT ====
                	if (opcode==5 || opcode==6) System.out.println("CTL=>le multiplexeur n'agit pas!");//beq et sw;
                	else if (opcode==4) changeData(5, 2); // LW
                	else if (opcode==7) changeData(5, 0); // JALR
                	else changeData(5, 1);
    //==== FUNCTION ALU ====
                    if (opcode==2)           		changeData(0, 1); // nand
                    else if (opcode==6)             changeData(0, 3); // beq -eq?
                    else if (opcode==3 || opcode==7)    changeData(0, 2); // pass1
                    else						changeData(0, 0); // add
                  break;
	
               case 18:
    //==== WE rf ====
                    if (opcode!=5 && opcode!=6)  changeData(6, 1); //  WErf diff de SW & BEQ
                    if (opcode==7) changeData(6,1);
                    break;
               case 17 :       
   //==== MUX PC ====
                    if (opcode!=6 && opcode!=7) changeData(3, 2);
                    if (opcode==7) changeData(3, 0);//JALR
                    if (opcode==6){//BEQ
                           if (eq == 1)   changeData(3, 1); //BRANCH   PC = PC + 1 + imm
                           else  changeData(3, 2); // PC = PC +1
                    }
                    break;
               case 15:
    //==== WE MEMORY ====
                    if (opcode==5)    changeData(7, 1); // SW > op=4
                    
                    if (opcode==6){//Si BEQ
                        eq = receive(1);
                        RecOp=true;
                        setBusy();
                        System.out.println("CTL > input active : EQ= " + eq);

                	}
                	break;
         }

//------------------------------------------------------------
    	switch(clockState){
    //==== PSEN ====
  		case 1:
  			changeData(9,1);
  			break;
    	case 8:
    //==== MUXalu1 ====
    		if(opcode!=3) changeData(1, 1);
    		else changeData(1,0);
    //==== MUX RF ====
    		if(opcode==0 || opcode==2) changeData(4,1);
    		else if(opcode==5 || opcode==6)changeData(4,0);
    		else System.out.println("CTL >  mux_rf inactive");
    //==== MUXalu2 ====
    		if(opcode!=3 && opcode!=7){
    			if(opcode==0 || opcode==2 || opcode==6)changeData(2,1);
    			else changeData(2,0);
    		}else System.out.println("CTL > mux_alu2 inactive");
    		break;
    //==== PCO READ====
    	case 19:
    		changeData(8,1);
    		break;
    	}
//------------------------------------------------------------
    
    if (isBusy() && !RecOp) { // si busy > latch
        setIdle();
       int i;
       for (i = 0; i < data.length; i++) {
         if (changed[i] == 1) {//cherche le signal a activer
           setLatch();
           changed[i] = 0;
           System.out.println("CTL i=" + i + "  nbout= " +
                              data.length + "  data= " + data[i] + "  chang= " +
                              changed[i] + "   [ " + ctl[i].getName() + " ]");
           ctl[i].receive(data[i]); //dis quel signal doit être ativé
         }
       }
       if (out1>=0 || out2>=0){
         Bus[]  out=super.getOutput();
         if (out1>=0){          out[0].receive(out1); out1=-1;}
         if (out2>=0){          out[1].receive(out2); out2=-1;}
       }
     }
    RecOp = false;
  }
///////////////////////////////////////////////////////////////////////
  private void changeData(int i, int newdata) {
    // on envoit info sur ctlsignal >> CTL busy >> latch 
    setBusy();
    data[i] = newdata;
    changed[i] = 1;
  }

///////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////
  public void paint(Graphics g) {
    super.paint(g);
    printText(g, 14, "CTL", getX() + getR() / 2 - 15, getY() + 35, Color.black);
 
  }

//////////////////////////////////////////////////////////////

}
