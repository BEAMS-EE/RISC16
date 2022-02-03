package seq_final;

import java.awt.*;

public class Add extends Chip{
//////////////////////////////////////
  public Add(int x,int y,int lg,int ht,Bus output,Bus src1,Bus src2) {
    super(x,y,lg,ht);
    Bus[] input=new Bus[2];     input[0]=src1;     input[1]=src2;
    super.setInput(input);
    super.setOutput(output);}
//////////////////////////////////////

 public boolean checkInput(){ return  (isInActive(0)&&isInActive(1)); }

 public void receive(){
   super.setData(add(receive(0),receive(1)));
  }

//////////////////////////////////////
 public void paint(Graphics g){
    super.paint(g);
    printText(g,18,"ADD",getX()+getLg()/2-18,getY()+17,Color.black); }
////////////////////////////////////////////////////////////////////////////
}
