package risc16_pipeline;
import java.awt.*;

public class LeftShift extends Chip{
//////////////////////////////////////
 public LeftShift(int x,int y,int lg,int ht,Bus out,Bus in) {
    super(x,y,lg,ht);
    setInput(in);
    setOutput(out);}
////////////////////////////////////////////////////////////////////////
 public void computes(){
    String temp=new String();
    temp=Integer.toBinaryString(super.getData());
    //system.out.println("LS > \t  in  =           " + temp);
    while(temp.length()<10)  temp="0"+temp;
    while(temp.length()<16)  temp=temp+"0";
    setData(Integer.parseInt(temp,2));
    //system.out.println("LS > \t  out  =           " + temp);
 }
////////////////////////////////////////////////////////////////////////
 public void paint(Graphics g){
    super.paint(g);
    g.drawString("Left Shift",super.getX()+(super.getLg()/2)-30,super.getY()+15);
 }
////////////////////////////////////////////////////////////////////////

}
