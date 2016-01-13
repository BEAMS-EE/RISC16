/*

 il faut mettre avant de latch
 has receive ( ctl) ??


 > remettre à zero

2005.03.08
 on ne désactive pas bus d entrée tant que pas de signal clt
 si ctl = -1 --> on ne selectionne rien
 si ctl = 0 ..1..2 on selectionne entrée > out

 */
//OLD
package risc16_pipeline;
import java.awt.*;

//OLD
public class Multiplexeur extends Chip{

  private int nbInput,select=0;  //select >   0 =input1   1 =input2 ..etc
  private int LgOut;            //longueur de la sortie verticale (dessin)
  private CtlSignal ctl;
  private boolean rctl=false; // le mux a t il recu le signal de contrôle ?
  private boolean used=false; // le mux est il utilisé pour cette instr ?

private boolean[] received;
////////////////////////////////////////////////////////////
///// INIT ///////////////////////////////////////////////////////
  public Multiplexeur(int nbInput,int x,int y,int lg,int ht,int LgOut) {
    super(x,y,lg,ht);//,Color.yellow);
    this.nbInput=nbInput;
    this.LgOut=LgOut;  }
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////  size = 2
  public Multiplexeur(int nbInput,int x,int y,int lg,int ht,int LgOut,Bus out,Bus in1,Bus in2) {
    this(nbInput,x,y,lg,ht,LgOut);
    super.setOutput(out);
    Bus[] temp = new Bus[2];
    temp[0]=in1;
    temp[1]=in2;
    super.setInput(temp);
    received = new boolean[temp.length];
    for (int i = 0; i < received.length; i++)  { received[i]=false;}
  }
////////////////////////////////////////////////////////////  size = 3
  public Multiplexeur(int nbInput,int x,int y,int lg,int ht,int LgOut,Bus out,Bus in1,Bus in2,Bus in3) {
    this(nbInput,x,y,lg,ht,LgOut);
    super.setOutput(out);
    Bus[] temp = new Bus[3];
    temp[0]=in1;
    temp[1]=in2;
    temp[2]=in3;
    super.setInput(temp);
    received = new boolean[temp.length];
    for (int i = 0; i < received.length; i++)  { received[i]=false;}
  }

////////////////////////////////////////////////////////////  size = 4
  public Multiplexeur(int nbInput,int x,int y,int lg,int ht,int LgOut,Bus out,Bus in1,Bus in2,Bus in3,Bus in4) {
    this(nbInput,x,y,lg,ht,LgOut);
    super.setOutput(out);
    Bus[] temp = new Bus[4];
    temp[0]=in1;
    temp[1]=in2;
    temp[2]=in3;
    temp[3]=in4;
    super.setInput(temp);
    received = new boolean[temp.length];
    for (int i = 0; i < received.length; i++)  { received[i]=false;}
  }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void setCtlBus(CtlSignal ctl){this.ctl=ctl; ctl.getId();}  //init bus output
  public int getLgOut(){return LgOut;}
////////////////////////////////////////////////////////////

  public void reset(){
    super.reset();
    select=0;
    rctl=false;used=false;
    for (int i = 0; i < received.length; i++)  { received[i]=false;}
  }
///////MAIN /////////////////////////////////////////////////////
   public int getCtl(){return ctl.getData();}
   public void setCtl(int data){this.select=data;}
////////////////////////////////////////////////////////////
  public boolean checkInput(){   // TRUE = > ok entree dispo
//      if (ctl.isActive()) return true;
//      //system.out.println(ctl.getName()+": check input slct=" + select);
      if (ctl.isActive()) {
        sel();
      }
/*
 for (int i = 0; (i < nbInput);  i++)
                      if  (isInActive(i) && !received[i]){
                       receive(i);received[i]=true;}
*/
   return rctl;

/*18.02.05      if(rctl && (isInActive(select) || received[select])) return true;
      else       for (int i = 0; (i < nbInput);  i++)
                   if  (isInActive(i) && !received[i]){
                    receive(i);received[i]=true;}
                    return false;*/
  }


public void sel(){
   if (ctl.getData()<nbInput) {
     int tmp=ctl.getData();
     if (tmp>=0) {select=tmp; used=true;}
     else used=false;

     if (!used) //system.out.println(ctl.getName()+" > \tmux not used :sel= " + select)
    	 ;
     else //system.out.println(ctl.getName()+" > \tchanging select to " + select)
    	 ;
   }
   rctl=true;
}

  public void receive(){

//   if (ctl.isActive()) {      sel();    }
    if (!used){

                     for (int i = 0; (i < nbInput);  i++)
                         if  (isInActive(i) && !received[i]){
                          receive(i);received[i]=true;}
    }else{
      int data=        receive(select);
      for (int i = 0; (i < nbInput);  i++)
        if ((i!=select) && isInActive(i) && !received[i])
          receive(i);
      setData(data);
      //system.out.println(ctl.getName()+" > \treceiving data =   " + getData()+"   ["+(select+1)+"/"+nbInput+"]");
    }
  }
  public void latch(){
    if (used)    super.latch();
//     //system.out.println(ctl.getName()+": latching data =   "+super.getData()+"  ["+(select+1)+"/"+nbInput+"]");
     rctl=false;
    for (int i = 0; i < received.length; i++)  { received[i]=false;}
  }


////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
  public void paint(Graphics g){
    int X[]=new int[4];
    X[0]=super.getX();
    X[1]=super.getX()+super.getLg();
    X[2]=X[1]-super.getLg()/4;
    X[3]=X[0]+super.getLg()/4;
    int Y[]=new int[4];
    Y[0]=super.getY();
    Y[1]=super.getY();
    Y[2]=super.getY()+super.getHt();
    Y[3]=Y[2];
    g.setColor(getColor());
    g.fillPolygon(X,Y,4);
    g.setColor(Color.black);
    g.drawPolygon(X,Y,4);
    g.fillRect(super.getX()+super.getLg()/2,super.getY()+super.getHt(),5,LgOut);
    g.drawLine(super.getX()+super.getLg()/2,super.getY()+super.getHt()-1,
                 super.getX()+(select+1)*super.getLg()/(nbInput+1),super.getY()+1);
//    if (ctl!=null)
//        g.drawString("["+(select+1)+"]",super.getX()+super.getLg()/2-20,super.getY()+super.getHt()/2);
//        g.drawString(ctl.getName()+">"+(select+1)+"/"+nbInput,super.getX()+super.getLg()/2-20,super.getY()+super.getHt()/2);

 }
////////////////////////////////////////////////////////////
public void setInactive(int op, int i) {
	// TODO Auto-generated method stub
	
}
}
