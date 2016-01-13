package risc16_pipeline;

import javax.swing.JOptionPane;

public class Ctl5 extends Ctl {
	
	  public Ctl5(int x, int y, int r) {
		    super(x, y, r);
		    this.nCtl = 5;
		  }
		public void act(boolean level,int clockState) {
				    if (level) {
	      //clockState++; // incrémente l'état à chaque coup d'horloge
	      setIdle();     
	           if (isInActive(0) && isInActive(1)) { //clockstate1
	            //system.out.println("CTL 5 : input active");
	        	  setBusy();
	        	  RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
	            int s1 = receive(0);
	            int s2 = receive(1);
	            int r3 = receive(2);
	            int r4 = receive(3);
	            int r5 = receive(4);

	            int MUXalu1, MUXalu2;

	            if ((s1!=0) && (s1 == r3)) {
	              MUXalu1 = 0;
	            }
	            else if ((s1!=0) && (s1 == r4)) {
	              MUXalu1 = 1;
	            }
	            else if ((s1!=0) && (s1 == r5)) {
	              MUXalu1 = 2;
	            }
	            else {
	              MUXalu1 = 3;
	            }
	            if ((s2!=0) && (s2 == r3)) {
	              MUXalu2 = 3;
	            }
	            else if ((s2!=0) && (s2 == r4)) {
	              MUXalu2 = 2;
	            }
	            else if ((s2!=0) && (s2 == r5)) {
	              MUXalu2 = 1;
	            }
	            else {
	              MUXalu2 = 0;

	            }
	            if((MUXalu1!=3 || MUXalu2 !=0) && alertOnDataForward){
	            	JOptionPane.showMessageDialog(null, "\u25AA \u25AA \u25AA DATA FORWARDING \n \u25AA During the next half-clocks, take a look near the two multiplexers in front of the ALU \n \u25AA Pipeline is SAFE but if the processor did not have this mechanism, there would be penalties !", "Data Hazard",JOptionPane.INFORMATION_MESSAGE);
	            }
	            changeData(0, MUXalu1);
	            changeData(1, MUXalu2);
	          }
	
	    }
	    else {
	//------------------------------------------------------------
	      if (isBusy() && !RecOp) { // si busy > latch
	         setIdle();
	        int i;
	        for (i = 0; i < data.length; i++) {
	          if (changed[i] == 1) {
	            setLatch();
	            changed[i] = 0;
	            //system.out.println("CTL " + nCtl + "   i=" + i + "  nbout= " + data.length + "  data= " + data[i] + "  chang= " +changed[i] + "   [ " + ctl[i].getName() + " ]");
	            ctl[i].receive(data[i]);
	          }
	        }
	        if (out1>=0 || out2>=0){
	          Bus[]  out=super.getOutput();
	          if (out1>=0){          out[0].receive(out1); out1=-1;}
	          if (out2>=0){          out[1].receive(out2); out2=-1;}
	        }
	      }
	      
	//------------------------------------------------------------
	   }
	    RecOp = false;
		}

}
