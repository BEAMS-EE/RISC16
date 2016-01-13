package risc16_pipeline;
import java.awt.*;
import java.io.Serializable;

public class Register extends Chip {
  /**
	 * 
	 */
	private static final long serialVersionUID = 5005084072060144558L;
private String nomReg;
  boolean op=false;         // afficher l'opérande ?
  private CtlSignal sta,sto;
  private int staid,stoid;
  private boolean stall=false,stomp=false,clocked=false;
  private Color couleurTgt = new Color(153,153,255);

//=====FORMAT====================
  private int display=0;           // 0= hex  / 1=dec / 2 =signed dec / 3 =binaire

//////////////////////////////////////////////////////
  public Register(String nomReg,int x,int y,int lg,int ht) {
    super(x,y,lg,ht,Color.white);
    this.nomReg=nomReg; }
//////////////////////////////////////////////////////
  public Register(String nomReg,int x,int y,int lg,int ht,Bus output,Bus input) {
    super(x,y,lg,ht,Color.white,output,input);
    this.nomReg=nomReg;}
///////////////////////////////////////////////////////
  public Register(String nomReg,int x,int y,int lg,int ht,Bus output,Bus input,boolean op) {
    super(x,y,lg,ht,Color.white,output,input);
    this.nomReg=nomReg;
    this.op=op;}
//////////////////////////////////////////////////////
  public void setCtlStall(CtlSignal sta){this.sta=sta;  staid=sta.getId();}
  public void setCtlStomp(CtlSignal sto){this.sto=sto;  stoid=sto.getId();}
  public void setClocked(){this.clocked=true;}
  public boolean isClocked(){return this.clocked;}
  public void setFormat(int d){this.display=d;}
  public void setFormat(int d,boolean resize){
	  this.display=d;
	  if(resize && getLg()>=100)
		  if(d==3) setLg(160); 
		  else setLg(100);
  }
  public void setColorChange(){
	  setColor(couleurTgt);//couleur bleu après avoir écris dans le registre
  }
  public boolean getStomp(){return stomp;}
  public boolean getStall(){return stall;}
  public void resetStomp(){this.stomp=false;}
  public void resetStall(){this.stall=false;}


 public boolean checkInput(){ // si on ispose de signaux stall/stomp >> si activ > true >> si entree = 0 , rien
   if (sta!=null)
      if (/*sta.isActive() &&*/ sta.getData()==1) { stall = true;System.out.println("\t\t-----------------------stall  true ");}
  if (sto!=null)
    if (sto.getData()==1){
    	stomp = true;  
      //System.out.println("\t\t-----------------------stomp  true ");
      }
      

      if(clocked) return false;
      return super.checkInput();
}

public void  receive(){
    if(!clocked)  super.receive();
}
public void latch(){
//      if (stomp)  super.setData(0);
//      if (stall)  stall =false;
//      else
  super.latch();
}

public void reset(){
  super.reset();
  stall=false;
  stomp=false;
}

public void  L(){
  if (!stall)
    super.receive();
  else {super.setData(0);System.out.println("Reg L stall");stall=false;}
  if (stomp){
	  super.setData(0);
	  stomp=false;      
       // System.out.println("\t\t-----------------------stomp  L ");
        }
  super.setBusy();
}
//////////////////////////////////////////////////////////////////
public String getAsm(String word) {
	
	
	String temp=word;
  
  if(temp=="0000000000000000") return "NOP";
  if(temp=="???") return "???";
  if (temp.length()>=3) temp=temp.substring(0,3);
  int i=Integer.parseInt(temp,2);
  switch (i){
    case 0:  return "ADD";  // break;
    case 1:  return "ADDI";  //break;
    case 2:  return "NAND";  //break;
    case 3:  return "LUI";  // break;
    case 4:  return "LW";   // break;
    case 5:  return "SW";   // break;
    case 6:  return "BEQ";   //break;
    case 7:  return "JALR"; // break;
    default : return "--";  // break;
  }}
//////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////
  public void paint(Graphics g){
    super.paint(g);
    g.setColor(Color.BLACK);
    String temp=new String();

     if (super.getLg()>50){
  //---------------------------------// REGISTRE normal -->   NAME= hex | dec

      if (display==3){
             // binary
             String sb=new String(Integer.toBinaryString(super.getData()));
             while(sb.length()<16)  sb="0"+sb;
             if(isBusy())sb="????????????????";
             
             if (getLg()<130) {
            	 g.setColor(color);
                 g.fillRect(super.getX()+40,super.getY()+2,70,15);
                 g.fillRect(super.getX()+55, super.getY()+17, 55, 10);
                 g.setColor(Color.black);
            	 g.drawString(nomReg+" = "+sb.substring(0,8), super.getX()+3,super.getY()+14);
            	 g.drawString(sb.substring(8,16), super.getX()+55,super.getY()+25);}
             else{
            	 g.setColor(color);
                 g.fillRect(super.getX()+40,super.getY()+2,120,15);
                 g.setColor(Color.black); 
             
            	 g.drawString(nomReg+" = "+sb,super.getX()+3,super.getY()+14);}
       
      }else{
       if (display<1){ // hex
             temp=Integer.toHexString(super.getData());
             if(temp.length()>4)     temp=temp.substring(temp.length()-4,temp.length());
             while(temp.length()<4)  temp="0"+temp;
             if(isBusy())temp = "????";
             g.drawString(nomReg+" = Ox"+temp,super.getX()+3,super.getY()+14);
       }else{ // decimal
             int nombre=getData();
             if (display==2 && nombre>=32768) nombre = nombre-65536;
             if(isBusy()) g.drawString(nomReg+" = ?",super.getX()+3,super.getY()+14); else
             g.drawString(nomReg+" = "+Integer.toString(nombre),super.getX()+3,super.getY()+14);
 }}

  } else{
//----------------------------------// REGISTRE small -->  binairy 3bit
        temp=Integer.toBinaryString(super.getData());
        while(temp.length()<3)   temp="0"+temp;
        if(isBusy())temp = "???";
        g.drawString(temp, super.getX()+2,super.getY() + 14);
        if (op)
          g.drawString(getAsm(temp),super.getX()-35,super.getY()+(super.getHt()/2)+15);
    }}

//////////////////////////////////////////////////////
}
