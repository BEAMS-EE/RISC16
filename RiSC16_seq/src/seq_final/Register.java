package seq_final;
import java.awt.*;

public class Register extends Chip{
  private String nomReg;
  boolean op=false;         // afficher l'opérande ?
  private CtlSignal read;
  private Color couleurTgt = new Color(153,153,255);
  private boolean reset=true, PC0=false;
  //private Color tgtColor=Color.white;

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
 /* public Register(String nomReg,int x,int y,int lg,int ht,Bus output,Bus input,boolean op) {
    super(x,y,lg,ht,Color.white,output,input);
    this.nomReg=nomReg;
    this.op=op;}*/
//////////////////////////////////////////////////////
  public void setCtlRead (CtlSignal read){this.read=read; read.getId();}

  public void setFormat(int d){
	  this.display=d;
	  if(d==3) setLg(160);
	  else setLg(100);
  }

  public void PC0(){reset = false; PC0 = true;}
  public void setColorChange(){
	  setColor(couleurTgt);//couleur bleu après avoir écris dans le registre
  }

 public boolean checkInput(){
  if(read!=null){
	  if (read.isActive()){
		  return true;
	  }else{

		  return false;
	  }
  }


      return super.checkInput();
}

 public void act(){
	 reset = true;
	 super.act();
 }

public void  receive(){
	if(read.getData()==1);
		super.receive();
}
public void latch(){

  super.latch();
}

public void reset(){
  super.reset();
  if (PC0)
	  reset=false;

}

//////////////////////////////////////////////////////////////////
public String getAsm(String word) {
  String temp=word;//word =>16bits en string


  if(temp=="0000000000000000") return "NOP";


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
             if (sb.length()==32) sb =sb.substring(16);

             if(isBusy()&& reset)sb="????????????????";
             g.setColor(color);
             g.fillRect(super.getX()+40,super.getY()+2,120,15);
             g.setColor(Color.black);


             g.drawString(nomReg+" = "+sb,super.getX()+3,super.getY()+14);
       }else{
       if (display<1){ // hex
             temp=Integer.toHexString(super.getData());
             if(temp.length()>4)     temp=temp.substring(temp.length()-4,temp.length());
             while(temp.length()<4)  temp="0"+temp;

             if(isBusy()&& reset)temp = "????";
             g.drawString(nomReg+" = Ox"+temp,super.getX()+3,super.getY()+14);
       }else{ // decimal
             int nombre=getData();
             if (display==2 && nombre>=32768) nombre = nombre-65536;//décimal signée!
             if(isBusy()&& reset) g.drawString(nomReg+" = ?",super.getX()+3,super.getY()+14); else
             g.drawString(nomReg+" = "+Integer.toString(nombre),super.getX()+3,super.getY()+14);
 }}

  } else{
//----------------------------------// REGISTRE small -->  binairy 3bit
        temp=Integer.toBinaryString(super.getData());
        while(temp.length()<3)   temp="0"+temp;
        g.drawString(temp, super.getX()+2,super.getY() + 14);
        if (op)
          g.drawString(getAsm(temp),super.getX()-35,super.getY()+(super.getHt()/2)+10);
    }

}

//////////////////////////////////////////////////////
}
