
package seq_final;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;






public class Fenetre  extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4548624000237069379L;

	static final String DEFAULTROMPATH="rom.txt";

	private JTextField msg;
	private DessinSeq d;  


	//internal frame!

	private Slider slider;
	private JDialog frameInstructions;
	private boolean instructionVisible=false;
	private JSplitPane splitPane;


	public Fenetre(){
		this(DEFAULTROMPATH);
	} 


	public Fenetre(String path) {

		

		int scrx = 850, scry = 990;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		scry = screenSize.height-55;
		scrx = screenSize.width;

		Toolkit toolkit = Toolkit.getDefaultToolkit(); 

		//    On récupère la taille de l'écran par défaut :
		Dimension dim = toolkit.getScreenSize();
		//    On récupère la configuration par défaut de l'écran par défaut :
		GraphicsConfiguration gconf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		//    On récupère les 'marges' de l'écran :
		Insets insets = toolkit.getScreenInsets(gconf); //variables publiques!
		setSize(scrx, scry);
		scry = dim.height-insets.bottom;
		scrx = dim.width;
		slider = new Slider(0,19,0);//au lieu de transférer le slider partout on aurait pu le prendre de d directement
		d = new DessinSeq(path, slider);
		d.setPreferredSize (new Dimension(720,610));
		JScrollPane scrollpaneD = new JScrollPane(d);
		scrollpaneD.setPreferredSize(new Dimension(740,screenSize.height-100));
		scrollpaneD.setVisible(true);
		slider.setDessinSeq(d);

		//-----------------------------------------------------------
		//icone
		Image img = Toolkit.getDefaultToolkit().getImage("signepolytech.png");
		this.setIconImage(img);
		//------------------------------------------------------------
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("RiSC 16 Simulator   ---   Sequential Implementation   ---   ULB-BEAMS 2009"); //Simulation d'un µP RISC-16




		Container c = getContentPane();
		c.add(d);

		//======================================
		//====  creation des panneaux
		//======================================

		JPanel f = new JPanel(new GridLayout(0, 5));
		JPanel n = new JPanel(new GridLayout(0, 1));   
		JPanel t = new JPanel(new GridLayout(0, 2));
		JPanel p = new JPanel(new GridLayout(2, 0));
		t.setBackground(new Color(220, 220, 220));

		//===== Big panel

		p.add(n);
		p.add(f);

		//======== North Sub-Panel
		msg = new JTextField("");
		n.add(msg);

		//======== South Sub-Panel

		JButton button1 = new JButton("RESET");
		JButton button2 = new JButton("½ Clock");
		JButton button3 = new JButton("-½  Clock");
		JButton button4 = new JButton("Instruction");
		JButton button5=  new JButton("RUN");

		button1.addActionListener(new Traitement(2));
		button2.addActionListener(new Traitement(0));
		button3.addActionListener(new Traitement(3));
		button4.addActionListener(new Traitement(5));
		button5.addActionListener(new Traitement(1));
		f.add(button1);
		f.add(button5);
		f.add(button2);
		f.add(button3);
		f.add(button4);
		//======================================



		//======================================
		//====  creation de la barre de menu
		//======================================

		JMenu file = new JMenu("  File  ");
		JMenuItem fopenrom = new JMenuItem("Import ROM");
		JMenuItem fopenram = new JMenuItem("Import RAM");
		JMenuItem fsaverom = new JMenuItem("Save ROM");
		JMenuItem fsaveram = new JMenuItem("Save RAM");
		JMenuItem fexit = new JMenuItem("Exit");
		fopenrom.addActionListener(new Traitement(10));
		fopenram.addActionListener(new Traitement(11));
		fsaverom.addActionListener(new Traitement(12));
		fsaveram.addActionListener(new Traitement(13));
		fexit.addActionListener(new Traitement(4));
		file.add(fopenrom);
		file.add(fopenram);
		file.addSeparator();
		file.add(fsaverom);
		file.add(fsaveram);
		file.addSeparator();
		file.add(fexit);

		JMenu disp = new JMenu("  Display  ");
		JMenuItem disp1 = new JMenuItem("Decimal");
		JMenuItem disp2 = new JMenuItem("Signed-decimal");
		JMenuItem disp3 = new JMenuItem("Hexadecimal");
		JMenuItem disp4 = new JMenuItem("Binary");
		disp1.addActionListener(new Traitement(21));
		disp2.addActionListener(new Traitement(22));
		disp3.addActionListener(new Traitement(20));
		disp4.addActionListener(new Traitement(23));
		disp.add(disp1);
		disp.add(disp2);
		disp.add(disp3);
		disp.add(disp4);
		/*disp.addSeparator();

    JMenuItem disp5 = new JMenuItem("Color");
    disp5.addActionListener(new Traitement(25));
    disp.add(disp5);*/

		JMenu help = new JMenu("  Help  ");
		JMenuItem help1 = new JMenuItem("Help");
		//JMenuItem help3 = new JMenuItem("Machine cycle");
		JMenuItem help2 = new JMenuItem("About");
		help.add(help1);
		//help.add(help3);
		help.addSeparator();
		help.add(help2);
		help1.addActionListener(new Traitement(31,this));
		help2.addActionListener(new Traitement(32));
		//help3.addActionListener(new Traitement(33));


		JMenuBar maBarre = new JMenuBar();
		maBarre.add(file);
		maBarre.add(disp);
		maBarre.add(help);
		setJMenuBar(maBarre);
		//======================================

		d.setTextField(msg);
		//Let the scroll pane know to update itself and its scroll bars.   d.revalidate();/
		this.setVisible(true);



		
		JInternalFrame frameM = new JInternalFrame("Memories",true,false,false,false);
		JTabbedPane tabbedPaneMem = new JTabbedPane();  
		tabbedPaneMem.addTab("Prog Mem",d.getRom().getContentPane());
		tabbedPaneMem.setMnemonicAt(0, KeyEvent.VK_P);
		tabbedPaneMem.addTab("Data Mem",d.getRam().getContentPane());
		tabbedPaneMem.setMnemonicAt(1, KeyEvent.VK_D);
		RegTable regtable=new RegTable(d.getRegBank());
		tabbedPaneMem.addTab("Reg Bank",new JScrollPane(regtable));
		tabbedPaneMem.setMnemonicAt(2, KeyEvent.VK_R);

		frameM.add(tabbedPaneMem);


		frameM.setSize(new Dimension(screenSize.width-750,screenSize.height/2));
		frameM.setMinimumSize(new Dimension(250,250));;
		frameM.setLocation(730, 0);
		frameM.setVisible(true);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,d, frameM.getContentPane());
		splitPane.setDividerLocation(750+splitPane.getInsets().left);


		JPanel txtbouton=new JPanel(new GridLayout(2,0));
		JPanel southpannel=new JPanel(new GridLayout(0,2));
		//JInternalFrame all =new JInternalFrame();

		txtbouton.add(n);
		txtbouton.add(f);
		southpannel.add(txtbouton);
		southpannel.add(slider);
add(splitPane,"Center");
add(southpannel,"South");


		getContentPane().setBackground(new Color(220, 220, 220));

		setSize(scrx, scry);
		setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);



		d.setTabbedPane(tabbedPaneMem); 
		d.getRegBank().setRegTable(regtable);
		regtable.setDessin(d);


		d.step(true,true);

	}


	//////////////////////[*]
	//-------------------------- LOOK & FEEL
	//      JFrame.setDefaultLookAndFeelDecorated(true);
	//        lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
	//        try {
	//            UIManager.setLookAndFeel(lookAndFeel);
	//        } catch (Exception e) { }
	//     Create and show the GUI...
	///////////////////////////////////////////////////////////////////////////////////////////
	public class Traitement implements ActionListener { //classe interne qui gère les action des boutons

		private int mode;
		JFrame frame;
		

		

		/////////////////////////////////
		public Traitement(int mode) {
			this.mode = mode;
		}
		public Traitement(int mode,JFrame frame) {
			this.mode = mode;
			this.frame=frame;
		}
		///////////////////////////////////
		public void actionPerformed(ActionEvent e) {

			switch (mode) {
			case 0:
				splitPane.setDividerLocation(750+splitPane.getInsets().left);
				d.step(true,true);             //   un demi cycle d'horloge
				break;
			case 1:
				splitPane.setDividerLocation(750+splitPane.getInsets().left);
				d.run();
				break;
			case 2:
				splitPane.setDividerLocation(750+splitPane.getInsets().left);
				d.reset();           //le reset
				d.step(true,true);//permet au reset de revenir au même état que celui de départ
				break;
			case 3:
				splitPane.setDividerLocation(750+splitPane.getInsets().left);
				//d.previousstep();           // - un demi cycle d'horloge
				d.previoushalfclock();
				break;

			case 4:
				System.exit(0);        // EXIT
				break;
			case 5:
				splitPane.setDividerLocation(750+splitPane.getInsets().left);
				d.step_instr();       //  jump fin de l'instruction
				break;
				/*case 25 :
        	d.changeCouleur();
        	break;*/
			case 31:       //  HELP			
				if(!instructionVisible){
					String s=System.getProperty("user.dir" )
			        + System.getProperty("file.separator" )
			        +"help"
						+ System.getProperty("file.separator" )
						+ "index.html";
					System.out.println(s);
					URL index;// =ClassLoader.getSystemResource(s);

					try {
						index = new URL("file:///"+s);
						frameInstructions=new JDialog(frame,false);
						frameInstructions.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
						frameInstructions.getContentPane().add(new JScrollPane(new HelpPane(index)));
						frameInstructions.setSize(new Dimension(640,480));
						frameInstructions.setLocation(300,200);
						frameInstructions.setVisible(true);


						frameInstructions.toFront();
						instructionVisible=true;
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					

					
				}else{
					frameInstructions.setVisible(true);
					frameInstructions.setLocation(300,200);
					frameInstructions.toFront();
				}

				break;

			case 32:       //  ABOUT
				String text2 = new String();
				text2 = 	"\n<html><font size=+2>RiSC-16 Simulation Program</font></html>"+
				" \n\n<html>RiSC-16 published by <font color=blue> Prof. Bruce JACOB</font></html>"+
				"\nhttp://www.engr.umd.edu/~blj/RiSC "+
				"\n\nSimulator created by : " +
				"\n<html><font color=blue>ULB - BEAMS</font>"+
				" (http://beams.ulb.ac.be/)</html>"+
				"\nDavid CROSS" +
				"\nLaurent ENGLEBIN" +
				"\nMichaël HUYSMAN"+
				"\nMarc JAUMAIN" +
				"\nPierre MATHYS" +
				"\nQuentin MONNEAUX" +
				"\nMichel OSEE\n";

				JOptionPane.showMessageDialog(null, text2 ,"ABOUT", JOptionPane.INFORMATION_MESSAGE);

				break;


			default:
				if (mode<20)          d.fichier(mode-10);     // 10-11-12-13 = open/save memory
				else                  d.display(mode-20);     // 20-23 = change number display
			break;
			}
		}
	}
}

