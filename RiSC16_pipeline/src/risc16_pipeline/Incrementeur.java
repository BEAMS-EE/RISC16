package risc16_pipeline;
import java.awt.*;

public class Incrementeur extends Chip{
/////////////////////////////////////////////////////////////
  public Incrementeur(int x,int y,int lg,int ht,Bus output,Bus input) {
    super(x,y,lg,ht,Color.cyan/*new Color(0,180,214,180)new Color(51,204,255)*/,output,input);}
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
