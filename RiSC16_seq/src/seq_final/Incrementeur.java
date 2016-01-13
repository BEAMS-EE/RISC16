package seq_final;
import java.awt.*;

public class Incrementeur extends Chip{
/////////////////////////////////////////////////////////////
  public Incrementeur(int x,int y,int lg,int ht,Bus output,Bus input) {
    super(x,y,lg,ht,new Color(0,180,214,180),output,input);}
//////////////////////////////////////////////////////////////
  public void computes(){
     setData(add(getData(),1));
  }
//////////////////////////////////////////////////////////////
  public void paint(Graphics g){
    super.paint(g);
    printText(g,18,"+1",getX()+3,getY()+18,Color.black);   }
//////////////////////////////////////////////////////////////
}
