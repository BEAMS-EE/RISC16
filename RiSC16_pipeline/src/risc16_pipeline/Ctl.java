package risc16_pipeline;

import java.awt.*;



public class Ctl extends Chip {

  protected static boolean alertOnDataForward=true;

protected CtlSignal[] ctl; // output (contient les signaux de controle)

  protected int[] data;//data est envoyer aux signaux de contrôle pour voir lequel va être activé
  protected int[] changed; // on affiche que si le signal change !
 // private int clockState = 0;//donne l'état du micropro
  protected int opcode = 0;//operande
  protected int out1=-1;

protected int out2 = -1;
  protected boolean RecOp = false;  
//  private int delay=0;
 // private int eq=0;
  
  protected int nCtl = 0;


//////////////////////////////////////////////////////
  public Ctl(int x, int y, int r) {
    super(x, y, r);//, new Color(237,27,52));
    this.nCtl = 0;
  }
 /* public Ctl(int n, int x, int y, int r) {
	    super(x, y, r, new Color(237,27,52));
	    this.nCtl = n;
	  }*/

//////////////////////////////////////////////////////
  public void setSignal(CtlSignal s1) {
	    ctl = new CtlSignal[1];
	    ctl[0] = s1;
	    data = new int[ctl.length];
	    changed = new int[ctl.length];
	    for (int i = 0; i < data.length; i++) {
	      data[i] = 0;
	      changed[i] = 0;
	    }
	  }

	  public void setSignal(CtlSignal s1, CtlSignal s2) {
	    ctl = new CtlSignal[2];
	    ctl[0] = s1;
	    ctl[1] = s2;
	    data = new int[ctl.length];
	    changed = new int[ctl.length];
	    for (int i = 0; i < data.length; i++) {
	      data[i] = 0;
	      changed[i] = 0;
	    }
	  }

	  public void setSignal(CtlSignal s1, CtlSignal s2, CtlSignal s3) {
	    ctl = new CtlSignal[3];
	    ctl[0] = s1;
	    ctl[1] = s2;
	    ctl[2] = s3;
	    data = new int[ctl.length];
	    changed = new int[ctl.length];
	    for (int i = 0; i < data.length; i++) {
	      data[i] = 0;
	      changed[i] = 0;
	    }
	  }
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
  public void reset() {
	//delay=0;
    super.reset();
    data = new int[ctl.length];
    changed = new int[ctl.length];
    for (int i = 0; i < data.length; i++) {
      data[i] = 0;
      changed[i] = 0;
    }
   // clockState = 0;
    opcode=0;
 //   out1 = -1;
 //   out2 = -1;
  }
//////////////////////////////////////////////////////

 
  //////////////
  public void act(boolean level,int clockState) { // 1= raising edge || 0= fallingedge
	   // cette méthode est redéfinie dans chaque unité CTLx
	  }
  
//  public int getOp(){ return opcode;}
///////////////////////////////////////////////////////////////////////
  public void changeData(int i, int newdata) {
    // on envoit info sur ctlsignal >> CTL busy >> latch 
    setBusy();
    data[i] = newdata;
    changed[i] = 1;
  }
  public void changeDataReset(int i) {
	    // on envoit info sur ctlsignal >> CTL busy >> latch au prochain front descendant
	    //setBusy();			// probleme si on commente, du coup,il va pas latcher...
	    if (data[i] != 0) {
	      data[i] = 0;
	      //changed[i] = 1;
	      ctl[i].receive(data[i]);
	     }
	  }
///////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////

  public void paint(Graphics g) {
	    super.paint(g);
	    if (nCtl == 0)    printText(g, 14, "CTL", getX() + getR() / 2 - 15, getY() + 35, Color.black);
	    else {              printText(g, 14, "CTL", getX() + getR() / 2 - 15, getY() + 20, Color.black);
	    
	        printText(g, 14, "" + nCtl, getX() + getR() / 2 - 2, getY() + 35, Color.black);
	    }//else{
	     //   printText(g, 14, "clockState= " + clockState, getX() + getR() / 2 - 15, getY() + 45,  Color.black);
//	    }
	  }

public static void alertActivation(boolean dataForwardSelected) {
	
	alertOnDataForward=dataForwardSelected;
}

//////////////////////////////////////////////////////////////

}
