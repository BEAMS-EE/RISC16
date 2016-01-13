package risc16_pipeline;

import java.awt.*;
import java.awt.event.*;

import java.net.MalformedURLException;
import java.net.URL;


import javax.swing.*;








public class Fenetre  extends JFrame {


	static final String DEFAULTROMPATH="rom.txt";

	private Dessin d;
	private JSplitPane splitPane;
	private JCheckBoxMenuItem alertOnDataForward;
	private JCheckBoxMenuItem alertOnStallEvent;
	private JCheckBoxMenuItem alertOnStompEvent;

	////////CONSTRUCTEUR//////////////////////////////////////////////////////////////////

	public Fenetre(){
		this(DEFAULTROMPATH);
	}

	public Fenetre(String path) {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("RiSC 16 Simulator   ---   Pipelined Implementation   ---   ULB-BEAMS 2009"); //Simulation d'un µP RISC-16
		setBackground(new Color(220, 220, 220));
		setExtendedState(MAXIMIZED_BOTH);
		setSize(1000, 600);
		setLayout (new BorderLayout ());	
		this.setJMenuBar(createMenuBar());// Sets the menubar for this frame

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();



		d = new Dessin(path);
		d.setPreferredSize (new Dimension(802,960));
		JScrollPane scrollpaneD = new JScrollPane(d);
		scrollpaneD.setPreferredSize(new Dimension(840,screenSize.height-100));
		JScrollBar jsb = scrollpaneD.getVerticalScrollBar();
		TimingDiagram cyclinstr = new TimingDiagram();
		JTabbedPane tabbedPaneSim = new JTabbedPane();
		tabbedPaneSim.addTab("Flow Diagram",scrollpaneD);
		tabbedPaneSim.addTab("Timing Diagram (Instructions)",cyclinstr);
		TimeStageDiagram cyclestage = new TimeStageDiagram();
		tabbedPaneSim.addTab("Timing Diagram (Stage)",cyclestage);
		
		JPanel frameM = new JPanel(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		StageView stageview = new StageView();
		JTabbedPane tabbedPaneMem = new JTabbedPane();  
		tabbedPaneMem.addTab("Prog Mem",d.getRom().getContentPane());
		tabbedPaneMem.setMnemonicAt(0, KeyEvent.VK_P);
		tabbedPaneMem.addTab("Data Mem",d.getRam().getContentPane());
		tabbedPaneMem.setMnemonicAt(1, KeyEvent.VK_D);
		RegTable regtable=new RegTable(d.getRegBank());
		tabbedPaneMem.addTab("Reg Bank",new JScrollPane(regtable));
		tabbedPaneMem.setMnemonicAt(2, KeyEvent.VK_R);
		frameM.setLayout(new GridLayout(2, 0));
		frameM.add(tabbedPaneMem);
		frameM.add(stageview);

		frameM.setVisible(true);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,tabbedPaneSim, frameM/*.getContentPane()*/);
		splitPane.setDividerLocation(842+splitPane.getInsets().left);

		tabbedPaneSim.setMinimumSize(new Dimension(700,600));

		add("Center",splitPane);
		add("South",getControlPanel());	// ajout des boutons

		setVisible(true);


		// communication dessin <--> ...
		d.setTabbedPane(tabbedPaneMem); 
		d.setScrollBar(jsb);
		d.setTimingDiagram(cyclinstr);
		d.setTimeStageDiagram(cyclestage);
		d.setStageView(stageview);
		stageview.setDessin(d);
		d.getRegBank().setRegTable(regtable);
		regtable.setDessin(d);



		alertOnDataForward.setSelected(true);
		alertOnStallEvent.setSelected(true);
		alertOnStompEvent.setSelected(true);

	}

	////////PANNEAU DE CONTROLE/////////////////////////////////////////////////////////////

	/**	
	 * Panneau de contrôle
	 */
	private JPanel getControlPanel(){
		JPanel controlpanel = new JPanel(new GridLayout(2, 0));		
		controlpanel.add(getConsole());
		controlpanel.add(getButtons());
		return controlpanel;
	}
	/**	
	 * Panneau contenant les zones de texte d'information
	 */
	private JPanel getConsole() {
		JPanel n = new JPanel(new GridLayout(0, 2)); // text
		JTextField clk = new JTextField("");
		clk.setEditable(false);
		JTextField[] msg = new JTextField[5];		
		n.add(clk);
		for(int i=0;i<1;i++) {
			msg[i]=new JTextField("");msg[i].setEditable(false);
			n.add(msg[i]);}
		d.setTextField(clk, msg[0]);
		return n;
	}
	/**	
	 * Panneau contenant les boutons
	 */
	private JPanel getButtons() {
		JPanel f = new JPanel(new GridLayout(0, 6)); // buttons
		JButton button1 = new JButton("RESET");
		JButton button2 = new JButton("+½ Clock");
		JButton button3 = new JButton("-½ Clock");
		JButton button4 = new JButton("+1 Cycle");
		JButton button5 = new JButton("-1 Cycle");
		JButton button6=  new JButton("RUN");
		button1.addActionListener(new Traitement(2));
		button2.addActionListener(new Traitement(0));
		button3.addActionListener(new Traitement(3));
		button4.addActionListener(new Traitement(5));
		button5.addActionListener(new Traitement(4));
		button6.addActionListener(new Traitement(1));
		f.add(button1);
		f.add(button6);
		f.add(button2);
		f.add(button3);
		f.add(button4);
		f.add(button5);
		return f;
	}
	////////////////////////////////////////////////////////////////////////////////////////

	////////BARRE DE MENU///////////////////////////////////////////////////////////////////
	/**	
	 * Barre de Menu
	 */
	private JMenuBar createMenuBar() {	
		JMenuBar maBarre = new JMenuBar();
		maBarre.add(getJMenuFile());
		maBarre.add(getJMenuDisp());
		maBarre.add(getJMenuHelp());
		return maBarre;
	}
	/**	
	 * Menu "File"
	 */
	private JMenu getJMenuFile() {
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
		fexit.addActionListener(new Traitement(14));
		file.add(fopenrom);
		file.add(fopenram);
		file.addSeparator();
		file.add(fsaverom);
		file.add(fsaveram);
		file.addSeparator();
		file.add(fexit);
		return file;
	}
	/**	
	 * Menu "Display"
	 */
	private JMenu getJMenuDisp() {
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
		disp.addSeparator();
		JMenu alerts=new JMenu("Alerts");
		alertOnDataForward = new JCheckBoxMenuItem("Data Forwarding");
		alertOnStallEvent = new JCheckBoxMenuItem("Stall Event");
		alertOnStompEvent = new JCheckBoxMenuItem("Stomp Event");
		alertOnDataForward.addItemListener(new Traitement());
		alertOnStallEvent.addItemListener(new Traitement());
		alertOnStompEvent.addItemListener(new Traitement());
		alerts.add(alertOnDataForward);
		alerts.add(alertOnStallEvent);
		alerts.add(alertOnStompEvent);
		disp.add(alerts);

		return disp;
	}	
	/**	
	 * Menu "Help"
	 */
	private JMenu getJMenuHelp() {
		JMenu help = new JMenu("  Help  ");
		JMenuItem help1 = new JMenuItem("Help");
		JMenuItem help2 = new JMenuItem("About");
		help.add(help1);
		help.addSeparator();
		help.add(help2);
		help1.addActionListener(new Traitement(31,this));
		help2.addActionListener(new Traitement(32));
		return help;
	}
	////////////////////////////////////////////////////////////////////////////////////////

	////////TRAITEMENT/////////////////////////////////////////////////////////////////////	
	/**
	 * Gère les actions des différents Boutons et Menus
	 *
	 */
	public class Traitement implements ActionListener, ItemListener  {
		private int mode;
		private boolean instructionVisible;
		private JDialog frameInstructions;
		private Frame frame;

		public Traitement(int mode) {
			this.mode = mode;
		}

		public Traitement(int mode,JFrame frame) {
			this.mode = mode;
			this.frame=frame;
		}

		public Traitement(){}

		public void itemStateChanged(ItemEvent e) {
			d.alertActivation(alertOnDataForward.isSelected(),alertOnStallEvent.isSelected(),alertOnStompEvent.isSelected());

		}
		public void actionPerformed(ActionEvent e) {
			switch (mode) {
			case 0: // +1/2 clock
				splitPane.setDividerLocation(842+splitPane.getInsets().left);
				d.step(true,true);             //   un demi cycle d'horloge
				break;
			case 1: // RUN
				//long ping = System.currentTimeMillis();
				splitPane.setDividerLocation(842+splitPane.getInsets().left);
				d.run();
				//long pong = System.currentTimeMillis();
				//system.out.println("PING-PONG = "+(pong-ping)+"ms");
				break;
			case 2: // RESET
				//        	button.setBackground(Color.YELLOW);
				splitPane.setDividerLocation(842+splitPane.getInsets().left);
				d.reset();           //le reset
				break;
			case 3: // -1/2clock
				splitPane.setDividerLocation(842+splitPane.getInsets().left);
				d.previoushalfclock();           // un cycle d'horloge
				break;
			case 4: // -1 cycle
				splitPane.setDividerLocation(842+splitPane.getInsets().left);
				d.previousstep();
				break;
			case 14:
				System.exit(0);        // EXIT
				break;
			case 5: // +1 cycle
				splitPane.setDividerLocation(842+splitPane.getInsets().left);
				d.step_instr(false);       
				break;

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
				"\nLaurent ENGLEBIN"+
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
	////////////////////////////////////////////////////////////////////////////////////////


}