package risc16_pipeline;

public class Ctl4 extends Ctl {

	public Ctl4(int x, int y, int r) {
		super(x, y, r);
		this.nCtl = 4;
	}
	public void act(boolean level,int clockState) {
		if (level) {
			//clockState++; // incrémente l'état à chaque coup d'horloge
			setIdle();     
			if (clockState==3){
				if (isInActive(0)) {
					//system.out.println("CTL 4 : input active");
					setBusy();
					RecOp=true; // Pour lors de la réception de Opcode, ne pas passer ds le if plus bas
					opcode = receive(0);
				}

				int MUX = 0;

				if (opcode == 1 || opcode == 3 || opcode == 4 || opcode == 5) {
					MUX = 0; //  imm -- op0
				}
				if (opcode == 7) {
					MUX = 1; // JALR ---  PC+1
				}
				if (opcode == 0 || opcode == 2 || opcode==6) {
					MUX = 2; // ADD / NAND   -- alu2

				}
				changeData(0, MUX);
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
