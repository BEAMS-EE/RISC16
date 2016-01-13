package risc16_pipeline;

public class Ctl1 extends Ctl {

	public Ctl1(int x, int y, int r) {
		super(x, y, r);
		this.nCtl = 1;
	}
	public void act(boolean level,int clockState) {
		if (level) {
			//clockState++; // incrémente l'état à chaque coup d'horloge
			setIdle();     
			if(clockState==3){
				if (isInActive(0)) {
					//system.out.println("CTL 1 : input active : RT4");
					setBusy();
					RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
					int RT = receive(0);
					int WE = 0;

					if (RT != 0) {   //  ni SW ni BEQ  (pas de WB)
						WE = 1; // si RT4 = O --> WE =0 sinon 1

					}
					changeData(0, WE);
					//changeData(1, WE);
				}}
			// else if (clockState==3)             changeData(0, 0);
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
