package risc16_pipeline;

public class Ctl6 extends Ctl {
	
	  public Ctl6(int x, int y, int r) {
		    super(x, y, r);
		    this.nCtl = 6;
		  }
		public void act(boolean level,int clockState) {
	    if (level) {
		      //clockState++; // incrémente l'état à chaque coup d'horloge
		      setIdle();     
		        if (clockState==5){
		        	if (isInActive(0)) {
		        	  setBusy();
		        	  RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
		            int op = receive(0);
		            //system.out.println("CTL 6 > input > op= " + op);

		            //===== MUX op
		            if (op == 3) {changeData(0, 0);}                               // LUI -- leftshift
		            else  /*if (op == 1 || op == 4 || op == 5)*/ {changeData(0, 1); }  // ADDI  SW  LW    -- sign

		            //===== MUX s2
		            if (op == 0 || op == 2)            changeData(1, 1);           // -rC - ADD NAND
		            else /*if (op==5 || op==6)*/{         changeData(1, 0);}          // - rA -  JALR BEQ
		          }}
		
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
