package risc16_pipeline;
import java.awt.*;
//import javax.swing.*;

public class Clock extends Chip {
  private int time=0;          // nb d impulsions effectuées
  private boolean high=false;  // 1= etat haut / 0= etat bas
  private int cycle=0;
  private int state=0;

////////////////////////////////////////////////////////////////////////////////////////////
  public Clock(int x, int y, int lg, int ht) {
    super(x, y, lg, ht, Color.white);}
//////////////////////////////////////////////


  public void inc() {
    if (high) {high=false;state++; }
    else      {high=true;state++; time++;}
    if (state==15){state=1;cycle++;}

    // int value max = 2^31 -1  --> attention dépassement  | en pratique n'arrivera JAMAIS
  }
//////////////////////////////////////////////
  public void reset() {
    time = 0;
    high = false;
    cycle=0;
    state=0;
    }
//////////////////////////////////////////////
  public int getTime() { return this.time; }
  public boolean getLevel() { return this.high; }
  public int getState(){return state;}
  public int getCycle(){return cycle;}
  
  public void setTime(int time) {this.time=time;}
  public void setCycle(int cycle) {this.cycle=cycle;}
//////////////////////////////////////////////
public String getText(){
    if (high) return "  Clock = "+Integer.toString(time)+ "  |  high"+"\tstate n° "+state+"\t# Cycles : "+cycle;
    else      return "  Clock = "+Integer.toString(time)+ "  |  low "+"\tstate n° "+state+"\t# Cycles : "+cycle;
}

////////////////////////////////////////////////////////////////////////////////////////////
  public void paint(Graphics g) {
    super.paint(g);
    g.setColor(Color.black);
    if (high) g.drawString("Clock = " + Integer.toString(time)+ " | HI", super.getX() + 3, super.getY() + 15);
    else      g.drawString("Clock = " + Integer.toString(time)+ " | LO", super.getX() + 3, super.getY() + 15);
  }
//////////////////////////////////////////////


public void setState(int state) {
	this.state=state;
	
}
}
