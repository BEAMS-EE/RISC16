package seq_final;
import java.awt.*;
import java.awt.event.*;



public class MemData    extends Memoire {
/**
	 * 
	 */
	private static final long serialVersionUID = 2611857003143120587L;
/////////////////////////////////////////////////////////////////
  public MemData(String title, int x, int y, int lg, int ht, Bus out, Bus address, Bus in) {
    super(title, x, y, lg, ht, /*new Color(252,111,26)*/Color.cyan, out);
    super.setBusAddr(address);
    super.setBusIn(in);
    super.fillColumn(1,"0");
    
    super.getJButtonRM().addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			fillColumn(1,"0");
		} });
  }
/////////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////////////
  public void dessine(Graphics g) {
    super.dessine(g);
    printText(g,18,"DATA",X()+getLg()/2-27,Y()+getHt()/2-5,Color.black);
    printText(g,18,"MEM",X()+getLg()/2-10,Y()+getHt()/2+15,Color.black);
    g.drawString("OUT",  X() + getLg()/2-10, super.Y()+11);
    g.drawString("IN",   X() + 10,          Y() + getHt() - 3);
    g.drawString("ADDR", X() + getLg() - 40,Y() + getHt() - 3);
  }
////////////////////////////////////////////////////////////////
 
}
