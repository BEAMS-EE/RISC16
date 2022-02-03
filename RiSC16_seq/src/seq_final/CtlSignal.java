package seq_final;
import java.awt.*;

public class CtlSignal extends Bus{
  private String name;
  private float[] dash={10.0f};
  private int posX=42, posY=500;
////////////////////
  public CtlSignal(int x1,int y1,int x2,int y2,String name) {
    super(x1,y1,x2,y2);
    super.setBasicStroke(new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER,10.0f,dash,0.0f));
    super.setBasicStrokeMilieu(new  BasicStroke(0.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER,10.0f,dash,0.0f));
    super.setEpaisseur(3);

    this.name=name;
  }
/////////////////////
  ///  public CtlSignal(){etat=false;}
  public CtlSignal(int a[],int b[],String name) {
    super(a,b);
    super.setBasicStroke(new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER,10.0f,dash,0.0f));
    super.setBasicStrokeMilieu(new  BasicStroke(0.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER,10.0f,dash,0.0f));
    super.setEpaisseur(3);
    this.name=name;
  }

////////////////////////
  public String getName(){return name;}
  public void setStringPos(int x, int y){
	  posX = x;
	  posY = y;
  }
/////////////////////////

public void paint(Graphics2D g){
   if(super.getShow() && isActive() && readData()>=0){
      setColor(Color.red);
      super.paint((Graphics2D)g);
      if(name!=null){
           g.setColor(Color.black);
           g.drawString(name+" = "+readData(), posX, posY);
      }}
  }
///////////////////////////
}
