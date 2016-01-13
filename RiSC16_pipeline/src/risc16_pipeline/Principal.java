package risc16_pipeline;
import java.awt.event.*;

public class Principal {

	public static void main(String[] args) {
		Fenetre f;
		if (args.length>0)
				f=new Fenetre(args[0]);
		else
				f=new Fenetre();

		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);}});
	}
}
