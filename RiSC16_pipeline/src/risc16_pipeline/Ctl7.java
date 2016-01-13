package risc16_pipeline;

public class Ctl7 extends Ctl {
	
	  public Ctl7(int x, int y, int r) {
		    super(x, y, r);
		    this.nCtl = 7;
		  }
		public void act(boolean level,int clockState) {
		    if (level) {
			      //clockState++; // incrémente l'état à chaque coup d'horloge
			      setIdle();     
			           if (clockState==1){ //1//isInActive(0) ){
			              //system.out.println("CTL 7 : input OP1 active");
			         	  setBusy();
			        	  RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
			              int op= receive(0);
			              int ra= receive(1);
			              int rb= receive(2);
			              int rc= receive(3);

			              int op2= receive(4);
			              int rt2= receive(5);

			              int OUTrt,OUTop;
			              int Pstall=0;

			              if (op==5 || op==6)    OUTrt=0;   //sw beq   -> pas de WB
			              else                   OUTrt=ra;
			              OUTop=op;
			              if (op2==4 && (rt2==rb ||rt2==rc)){  // LW  STALL == ne pas latcher , masi garder valeur > prochain ( FETCH DECODE wait)
			                  Pstall=1;
			                  OUTrt=0;OUTop=0;
			                  changeData(0, Pstall);
			              }


			             out1=OUTop;
			             out2=OUTrt;
			            }
			            if (clockState==5) {
				         	 // setBusy();
				        	 // RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
			            	changeDataReset(0);//cf CTL3
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
