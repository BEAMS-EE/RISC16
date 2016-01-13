package risc16_pipeline;

public class Ctl2 extends Ctl {

	public Ctl2(int x, int y, int r) {
		super(x, y, r);
		this.nCtl = 2;
	}
	public void act(boolean level,int clockState) {
		if (level) {
			//clockState++; // incrémente l'état à chaque coup d'horloge
			setIdle();     
			if(clockState==1){
				if (isInActive(0)) {	//clkState=1
					//system.out.println("CTL 2 : input active: OP3");
					setBusy();
					RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
					opcode = receive(0);
					int WE = 0;

					if (opcode == 5) {
						WE = 1; // SW
					}

					changeData(0, WE);

				}}
			if (clockState==5){
				setBusy();
				RecOp=true;
				int MUX=1;
				if (opcode == 4) {
					MUX = 0; // LW  - data
				}
				changeData(1, MUX);

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
		RecOp = false;}

}
