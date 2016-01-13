package seq_final;
import java.awt.*;

/**
 * 
 * @author ENGLEBIN Laurent
 */
public class SignExt extends Chip{
////////////////////////////////////////
  public SignExt(int x,int y,int lg,int ht,Bus out,Bus in) {
    super(x,y,lg,ht);
    setInput(in);
    setOutput(out);  }
///////////////////////////////////////////////////////////////////////////

  public void computes(){
    String temp=new String();
    char c;

    temp=Integer.toBinaryString(super.getData() & 127);  // on ne garde que les 7 LSB
    while(temp.length()<7)  temp="0"+temp;
    System.out.println("SIGN > \t  in  =           " + temp);

    c = temp.charAt(0); // bit de signe
    while(temp.length()<16)  temp=c+temp;
    System.out.println("SIGN > \t  out =  " + temp);
    setData(Integer.parseInt(temp,2));
  }

//////////////////////////////////////
  public void paint(Graphics g){
      super.paint(g);
      g.drawString("Sign Ext",super.getX()+(super.getLg()/2)-30,super.getY()+15);
    }
//////////////////////////////////////////////////////////////////////////////
}
