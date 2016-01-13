package risc16_pipeline;

public class Ctl3 extends Ctl {
	
	  public Ctl3(int x, int y, int r) {
		    super(x, y, r);
		    this.nCtl = 3;
		  }
		public void act(boolean level,int clockState) {
				    if (level) {
	      //clockState++; // incrémente l'état à chaque coup d'horloge
	      setIdle();     
	           if (clockState==5){
	        	 if (isInActive(0)) {
	 	            opcode = receive(0);
	 	            //system.out.println("CTL 3 > input active  : op = " + opcode);
	 	        	  setBusy();
	 	        	  RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
	 	           }
	         	 
	            int FUNCalu = 0; // ADD     (ADDI
	            if (opcode == 2)             FUNCalu = 1; // NAND    (NAND)
	            if (opcode == 3 || opcode == 7)  FUNCalu = 2; // PASS 1  (LUI JALR
	            if (opcode == 6)             FUNCalu = 3; // EQ ?    (BEQ)
	            changeData(0, FUNCalu);
	          }
	//***************
	        if (clockState==9){
	         	  setBusy();
	        	  RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas// car signal EQ? ok à partir de 8
	          int MUXpc = 2;
	          int Pstomp = 0;

	          if (isInActive(1)) {
        	
	               int eq = receive(1);
	               System.out.println("CTL 3 > input active : EQ= "+eq);
	               if (eq==1)    {MUXpc=0; Pstomp=1;} //BRANCH BEQ
	         }


	         if (opcode == 7)    {MUXpc = 1; Pstomp=1;} // JALR
	         changeData(1, MUXpc);
	         if (Pstomp==1) changeData(2,1);
	       }

	      if (clockState==11){
         	 // setBusy();
        	 // RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
        	  changeDataReset(2);// reset stomp event
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
