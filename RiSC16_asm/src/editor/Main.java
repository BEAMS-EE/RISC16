package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.io.OutputStream;
import java.io.PrintStream;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;


/**
 *
 * @author ENGLEBIN Laurent
 */
public class Main extends JFrame implements ActionListener{

	private MemProg memprog;
	private EditorTab editor;
	Architecture arch;
	private Registers registers;
	private Memoire ram;
	private Debugger debugger;
	private JRadioButtonMenuItem preset1;
	private JRadioButtonMenuItem preset2;
	private JRadioButtonMenuItem preset3;
	private JRadioButtonMenuItem preset4;
	private JRadioButtonMenuItem is1;
	private JRadioButtonMenuItem is2;
	private JRadioButtonMenuItem is3;
	private JRadioButtonMenuItem regMenu1;
	private JRadioButtonMenuItem regMenu3;
	private JRadioButtonMenuItem regMenu2;
	private JRadioButtonMenuItem regMenu4;

	private int regnum=8;
	private int isa=0;
	private int instruSize=16;
	private JMenuItem hConsole;
	private JMenuItem hAbout;
	private JInternalFrame consoleFrame;
	private JRadioButtonMenuItem sarithmetic;
	private JRadioButtonMenuItem usarithmetic;
	private boolean isSigned=true;

	private int immRRRSize=4;
	private int immRRISize=7;
	private int immRISize=10;
	private int opSize=3;
	private int regSize;
	private JMenuItem sizes;
	private Dimension screenSize;
	private JDesktopPane desktop;
	private JInternalFrame regframe;
	private JInternalFrame debugFrame;
	private JButton simButton;
	private JButton simPButton;
	private JMenuItem fexit;
	private String[] instructionSet=Architecture.DEFAULTINSTRUCTIONSET[0];
	private JRadioButtonMenuItem isOtherEnable;
	private JMenuItem isOtherConfiguration;
	private String[] instructionSetOther=Architecture.DEFAULTINSTRUCTIONSET[0];
	private JMenuItem hHelp;
	private boolean dialogHelpVisible=false;
	private JDialog dialogHelp;
	private boolean boolDebug=false;
	private JInternalFrame editorFrame;



	public Main() {
		super("RiSC 16   ---   Instruction Set Simulator   ---   ULB-BEAMS 2009");
		//screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenSize = new Dimension(gd.getDisplayMode().getWidth(),gd.getDisplayMode().getHeight());

		System.out.println(screenSize.getWidth());
		System.out.println(screenSize.getHeight());


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 700);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		this.setJMenuBar(createMenuBar());
		add(createToolBar(), BorderLayout.NORTH);

		arch=new Architecture("default",Architecture.DEFAULTINSTRUCTIONSET[0],8,16, Architecture.DEFAULTBRANCHONCARRY[0],false);

		/* MISE EN PLACE DU DESKTOP ET DE SES FRAMES*/
		desktop=new JDesktopPane();
		desktop.setBackground(Color.DARK_GRAY);

		/* EDITOR FRAME*/
		editorFrame = new JInternalFrame("editor",true,false,true,true);
		editorFrame.setSize(2*(screenSize.width-20)/3,(screenSize.height-120)/2);
		editorFrame.setMinimumSize(new Dimension(400,200));
		editorFrame.setVisible(true);
		editorFrame.setLocation(0,0);
		setEditor(new EditorTab());
		editorFrame.add(editor);
		desktop.add(editorFrame);

		/* MEMPROG FRAME*/
		String RomColumnNames[] = {"Address", "Content", "ASM","","label"};
		memprog=new MemProg("Prog Mem",RomColumnNames,arch);
		desktop.add(memprog);
		memprog.setVisible(false);

		/* MEMDATA FRAME*/
		ram=new Memoire("Data Mem");
		desktop.add(ram);
		ram.setVisible(false);

		/* REGISTERS FRAME*/
		registers=new Registers(regnum);
		regframe=new JInternalFrame("Registers",true,false,false,true);
		regframe.setBackground(new Color(220, 220, 220));
		regframe.setLayout(new BorderLayout ());
		desktop.add(regframe);
		regframe.setVisible(false);

		/* DEBUG FRAME*/
		debugFrame = new JInternalFrame("debug",true,true,true,true);
		debugFrame.setVisible(false);
		desktop.add(debugFrame);
		debugFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		debugFrame.addInternalFrameListener(new InternalFrameListener() {

			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {
				closeDebug();
			}

			public void internalFrameDeactivated(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameOpened(InternalFrameEvent e) {}
			});

		/* CONSOLE FRAME*/
		desktop.add(createConsole());

		desktop.setVisible(true);
		this.getContentPane().add(desktop);
		this.setVisible(true);
	}


	private void setDebugger(Debugger debugger) {
		this.debugger = debugger;
	}

	private void closeDebug() {
		boolDebug=false;
		debugFrame.setVisible(false);
		regframe.setVisible(false);
		ram.dispose();
	}

	public MemProg getMemprog() {
		return memprog;
	}
	public void setMemprog(MemProg memprog) {
		this.memprog = memprog;
	}
	public void setEditor(EditorTab editor) {
		this.editor = editor;
	}
	public EditorTab getEditor() {
		return editor;
	}

	private boolean assemble(){
		//screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenSize = new Dimension(gd.getDisplayMode().getWidth(),gd.getDisplayMode().getHeight());
		if (boolDebug){
			editor.assembler();
			return true;
		}
		else{

		try {
			if (isa==-1)
				arch=new Architecture("archi",instructionSet,regnum,instruSize,true,isSigned);
			else
				arch=new Architecture("archi",Architecture.DEFAULTINSTRUCTIONSET[isa],regnum,instruSize, Architecture.DEFAULTBRANCHONCARRY[isa],isSigned);

			memprog.dispose();

			String RomColumnNames[] = {"Address", "Content", "ASM","","label"};
			memprog=new MemProg("Prog Mem",RomColumnNames,arch);
			memprog.setSize((screenSize.width-20)/3,(screenSize.height-120)/2);
			memprog.setLocation(2*(screenSize.width-20)/3,0);
			memprog.setMinimumSize(new Dimension(200,200));
			desktop.add(memprog);
			memprog.setVisible(true);
			memprog.moveToFront();
			editor.setMemprog(memprog);
			editor.assembler();


			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		}
	}

	private void runSimulation(){
		boolDebug=false;
		if(assemble());
		{
			regframe.getContentPane().removeAll();
			registers=new Registers(arch.getNumberOfRegister());
			regframe.add(registers);
			regframe.setSize((screenSize.width-20)/6,(screenSize.height-120)/2);
			regframe.setLocation(2*(screenSize.width-20)/3,(screenSize.height-120)/2);
			regframe.setVisible(true);
			regframe.moveToFront();

			ram.dispose();
			ram=new Memoire("Data Mem");
			ram.setSize((screenSize.width-20)/6,(screenSize.height-120)/2);
			ram.setLocation(5*(screenSize.width-20)/6,(screenSize.height-120)/2);
			ram.setMinimumSize(new Dimension(100,200));
			desktop.add(ram);
			ram.setVisible(true);
			ram.moveToFront();

			debugFrame.getContentPane().removeAll();
			setDebugger(new Debugger(arch,memprog,ram,registers));
			debugFrame.add(debugger);
			debugFrame.setSize(2*(screenSize.width-20)/3,(screenSize.height-120)/2);
			debugFrame.setMinimumSize(new Dimension(400,200));
			debugFrame.setLocation(0,(screenSize.height-120)/2);
			debugFrame.setVisible(true);
			debugFrame.moveToFront();

			editorFrame.setSize(2*(screenSize.width-20)/3,(screenSize.height-120)/2);

			boolDebug=true;
		}
	}


	/**
	 * Console
	 */
	private JInternalFrame createConsole(){

		final JTextArea ta = new JTextArea(10,10);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setBackground(Color.BLACK);
		ta.setForeground(Color.YELLOW);
		JScrollPane scrollConsole=new JScrollPane(ta);
		consoleFrame=new JInternalFrame("console",true,true,true,true);
		consoleFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		consoleFrame.setSize((screenSize.width-20)/2,(screenSize.height-120)/2);
		consoleFrame.setLocation(0,(screenSize.height-120)/2);
		consoleFrame.setMinimumSize(new Dimension(100,100));
		consoleFrame.add(scrollConsole);
		consoleFrame.setVisible(false);
		JButton clearConsole=new JButton("CLEAR");
		consoleFrame.getContentPane().add("South",clearConsole);
		clearConsole.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ta.setText("");
			}});
		PrintStream ps = new PrintStream(new TextAreaOutputStream(ta));
		System.setOut(ps);
		System.setErr(ps);

		return consoleFrame;
	}

	class TextAreaOutputStream extends OutputStream {

		private JTextArea ta;

		public TextAreaOutputStream(JTextArea ta) {
			this.ta = ta;
		}

		public synchronized void write(int b) throws IOException {
			ta.append(String.valueOf((char) b));
			ta.setCaretPosition(ta.getText().length() - 1);
		}
	}

	/**
	 * Barre de Menu
	 */
	private JMenuBar createMenuBar() {
		JMenuBar maBarre = new JMenuBar();
		maBarre.add(getJMenuFile());
		maBarre.add(getJMenuArchitecture());
		maBarre.add(getJMenuHelp());
		return maBarre;
	}


	private JMenu getJMenuFile() {
		JMenu file = new JMenu("  File  ");
		fexit = new JMenuItem("Exit");
		file.add(fexit);
		fexit.addActionListener(this);
		return file;
	}

	private JMenu getJMenuArchitecture() {
		JMenu menuArchitecture = new JMenu("  Architecture  ");

		JMenu ISApreset = new JMenu("Preset");
		preset1 = new JRadioButtonMenuItem("RiSC16 original");
		preset1.setSelected(true);
		preset2 = new JRadioButtonMenuItem("Special IS[1] - 8 reg - Instruction 17 bits");
		preset3 = new JRadioButtonMenuItem("Special IS[1] - 16 reg - Instruction 24 bits");
		preset4 = new JRadioButtonMenuItem("Special IS[2] - 8 reg - Instruction 17 bits");
		ButtonGroup bgPreset = new ButtonGroup();
		bgPreset.add(preset1);bgPreset.add(preset2);bgPreset.add(preset3);bgPreset.add(preset4);
		preset1.addActionListener(this);preset2.addActionListener(this);preset3.addActionListener(this);preset4.addActionListener(this);
		ISApreset.add(preset1);ISApreset.add(preset2);ISApreset.add(preset3);ISApreset.add(preset4);
		JMenu instructionSet = new JMenu("Instruction Set");
		is1 = new JRadioButtonMenuItem("RiSC16 original");
		is1.setSelected(true);
		is2 = new JRadioButtonMenuItem("Special IS[1]");
		is3 = new JRadioButtonMenuItem("Special IS[2]");
		ButtonGroup bgISA = new ButtonGroup();
		bgISA.add(is1);bgISA.add(is2);bgISA.add(is3);
		instructionSet.add(is1);instructionSet.add(is2);instructionSet.add(is3);
		is1.addActionListener(this);is2.addActionListener(this);is3.addActionListener(this);
		JMenu instructionSetOther=new JMenu("Other");
		isOtherEnable=new JRadioButtonMenuItem("Enabled");
		bgISA.add(isOtherEnable);
		isOtherConfiguration=new JMenuItem("Configuration");
		instructionSetOther.add(isOtherEnable);
		instructionSetOther.addSeparator();
		instructionSetOther.add(isOtherConfiguration);
		isOtherEnable.addActionListener(this);
		isOtherConfiguration.addActionListener(this);
		instructionSet.addSeparator();
		instructionSet.add(instructionSetOther);
		JMenu regMenu = new JMenu("Registers");
		regMenu1 = new JRadioButtonMenuItem("8");
		regMenu1.setSelected(true);
		regMenu2 = new JRadioButtonMenuItem("16");
		regMenu3 = new JRadioButtonMenuItem("32");
		regMenu4 = new JRadioButtonMenuItem("64");
		ButtonGroup bgReg = new ButtonGroup();
		bgReg.add(regMenu1);bgReg.add(regMenu2);bgReg.add(regMenu3);bgReg.add(regMenu4);
		regMenu1.addActionListener(this);regMenu2.addActionListener(this);regMenu3.addActionListener(this);regMenu4.addActionListener(this);
		regMenu.add(regMenu1);regMenu.add(regMenu2);regMenu.add(regMenu3);regMenu.add(regMenu4);

		sizes = new JMenuItem("Imm & Instru Sizes");
		sizes.addActionListener(this);
		JMenu arithmetic=new JMenu("Signed or UnSigned");
		sarithmetic = new JRadioButtonMenuItem("signed");
		sarithmetic.setSelected(true);
		usarithmetic = new JRadioButtonMenuItem("unsigned");
		ButtonGroup bgSign = new ButtonGroup();
		bgSign.add(sarithmetic);bgSign.add(usarithmetic);
		arithmetic.add(sarithmetic);arithmetic.add(usarithmetic);
		sarithmetic.addActionListener(this);usarithmetic.addActionListener(this);

		menuArchitecture.add(ISApreset);
		menuArchitecture.addSeparator();
		menuArchitecture.add(instructionSet);
		menuArchitecture.add(regMenu);
		menuArchitecture.add(sizes);
		menuArchitecture.add(arithmetic);
		return menuArchitecture;
	}

	private JMenu getJMenuHelp() {
		JMenu help = new JMenu("  Help  ");
		hHelp=new JMenuItem("Help");
		hConsole = new JMenuItem("Console");
		hAbout = new JMenuItem("About");
		hHelp.addActionListener(this);
		hConsole.addActionListener(this);
		hAbout.addActionListener(this);
		help.add(hHelp);
		help.addSeparator();
		help.add(hConsole);
		help.addSeparator();
		help.add(hAbout);

		return help;
	}

/** TOOLBAR **/
	private JToolBar createToolBar(){
		JToolBar toolBar = new JToolBar();


		final JButton assembButton = new JButton("ASM");
		JButton runButton = new JButton("RUN");
		simButton = new JButton("SIM");
		simPButton = new JButton("SIM P");


		assembButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				assemble();
			}});
		runButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				runSimulation();
			}});
		simButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				runExtSimulation(false);
				assembButton.doClick();
			}});
		simPButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				runExtSimulation(true);
				assembButton.doClick();
			}});

		toolBar.add(assembButton);
		toolBar.add(runButton);
		toolBar.addSeparator();
		toolBar.add(simButton);
		toolBar.add(simPButton);

		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		return toolBar;
	}

	private void runExtSimulation(boolean isPipeline) {

		// si souhait de changer path, voir comment g�rer les Properties
		// http://java.sun.com/docs/books/tutorial/essential/environment/properties.html
		String jarpath;
		if (isPipeline){
			jarpath="risc16pipeline.jar";
		}
		else{
			jarpath="risc16.jar";
		}
		String pathfile=editor.saveAndGetPathFile();
		String command = "java -jar " + jarpath +" \""+ pathfile+"\"";
		System.out.println(command);
		try {
			// creation du processus
			Process p = Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			System.out.println("\n" + command + ": commande inconnu ");
		}
	}


	public static void main(String[] args) {
		new Main();
	}


/** LISTENERS **/
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(preset1)){
			instruSize=16;
			isa=0;
			regnum=8;
			opSize=3;
			updateImmSize();
			is1.setSelected(true);
			regMenu1.setSelected(true);
			editor.getEditor().setIsa(isa);
		} else if (e.getSource().equals(preset2)){
			instruSize=17;
			isa=1;
			regnum=8;
			opSize=4;
			updateImmSize();
			is2.setSelected(true);
			regMenu1.setSelected(true);
			editor.getEditor().setIsa(isa);
		} else if (e.getSource().equals(preset3)) {
			instruSize=24;
			isa=1;
			regnum=16;
			opSize=4;
			updateImmSize();
			is2.setSelected(true);
			regMenu2.setSelected(true);
			editor.getEditor().setIsa(isa);
		} else if (e.getSource().equals(preset4)) {
			instruSize=17;
			isa=2;
			regnum=8;
			opSize=4;
			updateImmSize();
			is3.setSelected(true);
			regMenu2.setSelected(true);
			editor.getEditor().setIsa(isa);
		} else if (e.getSource().equals(is1)) {
			isa=0;
			opSize=3;
			updateImmSize();
			editor.getEditor().setIsa(isa);

		} else if (e.getSource().equals(is2)) {
			isa=1;
			opSize=4;
			updateImmSize();
			editor.getEditor().setIsa(isa);
		} else if (e.getSource().equals(is3)) {
			isa=2;
			opSize=4;
			updateImmSize();
			editor.getEditor().setIsa(isa);
		} else if (e.getSource().equals(isOtherEnable)) {
			isa=-1;
			instructionSet=instructionSetOther;
			opSize=(int) Math.ceil((Math.log(instructionSet.length)/Math.log(2.0)));
			updateImmSize();
			editor.getEditor().setRegex(instructionSet);


		} else if (e.getSource().equals(regMenu1)) {
			regnum=8;
			updateImmSize();

		} else if (e.getSource().equals(regMenu2)) {
			regnum=16;
			updateImmSize();

		} else if (e.getSource().equals(regMenu3)) {
			regnum=32;
			updateImmSize();

		} else if (e.getSource().equals(regMenu4)) {
			regnum=64;
			updateImmSize();

		} else if (e.getSource().equals(sarithmetic)) {
			isSigned=true;

		}  else if (e.getSource().equals(usarithmetic)) {
			isSigned=false;

		} else if (e.getSource().equals(isOtherConfiguration)) {


			InstructionSetDialog instructionSetDialog=new InstructionSetDialog(this,instructionSet);
			instructionSetDialog.pack();
			instructionSetDialog.setVisible(true);
			instructionSetOther=instructionSetDialog.getDonnees();

			if (isOtherEnable.isSelected()){
				instructionSet=instructionSetOther;
				opSize=(int) Math.ceil((Math.log(instructionSet.length)/Math.log(2.0)));
				updateImmSize();
				editor.getEditor().setRegex(instructionSet);
			}
		} else if (e.getSource().equals(sizes)) {

			SizeDialog sizeDialog = new SizeDialog(this, instruSize, immRISize, immRRISize, immRRRSize,opSize, (int) (Math.log(regnum)/Math.log(2.0)));
			sizeDialog.pack();
			sizeDialog.setVisible(true);

			int [] retour = sizeDialog.getDonnees();
			instruSize=retour[0];
			immRISize=retour[1];
			immRRISize=retour[2];
			immRRRSize=retour[3];

		} else if (e.getSource().equals(hHelp)){
			if(!dialogHelpVisible){
//				String s= "help"
//					+ System.getProperty("file.separator" )
//					+ "index2.html";
//				System.out.println(s);
//				URL index = ClassLoader.getSystemResource(s);
//				System.out.println(index);
				String s=System.getProperty("user.dir" )
		        + System.getProperty("file.separator" )
		        +"help"
					+ System.getProperty("file.separator" )
					+ "index.html";
				System.out.println(s);
				URL index;

				try {
					index = new URL("file:///"+s);
					dialogHelp=new JDialog(this,false);
					dialogHelp.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					dialogHelp.getContentPane().add(new JScrollPane(new HelpPane(index)));
					dialogHelp.setSize(new Dimension(640,480));
					dialogHelp.setLocation(300,200);
					dialogHelp.setVisible(true);


					dialogHelp.toFront();
					dialogHelpVisible=true;
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}


			}else{
				dialogHelp.setVisible(true);
				dialogHelp.setLocation(300,200);
				dialogHelp.toFront();
			}

		} else if (e.getSource().equals(hConsole)){
			consoleFrame.setVisible(true);
			consoleFrame.moveToFront();

		} else if (e.getSource().equals(hAbout)){

			String text = new String();
			text = 	"\n<html><font size=+2>RiSC-16 Instruction Set Simulator</font></html>"+
			" \n\n<html>RiSC-16 published by <font color=blue> Prof. Bruce JACOB</font></html>"+
			"\n http://www.engr.umd.edu/~blj/RiSC"+
			"\n\nSimulator created by : " +
			"\n<html><font color=blue>ULB - BEAMS</font>"+
			" (http://beams.ulb.ac.be/)</html>"+
			"\nLaurent ENGLEBIN"+
			"\nMarc JAUMAIN" +
			"\nPierre MATHYS" +
			"\nMichel OSEE" +
			"\nAli�nor RICHARD\n";

			JOptionPane.showMessageDialog(null, text ,"ABOUT", JOptionPane.INFORMATION_MESSAGE);

		} else if(e.getSource().equals(fexit)){
			System.exit(0);
		}
		updateButStatus();
	}


/** AUTRES FONCTIONS UTILES POUR MAJ de L'ARCHITECTURE **/

	private void updateButStatus() {
		if (isa!=0 && regnum!=8){
			simButton.setEnabled(false);
			simPButton.setEnabled(false);
		}
		else{
			simButton.setEnabled(true);
			simPButton.setEnabled(true);
		}
	}

	private void updateImmSize() {
		int regsize=(int) (Math.log(regnum)/Math.log(2.0));

		for(;!instructionSizeIsOk(opSize,regsize);instruSize++);
		for(;!immRISizeIsOk(opSize,regsize);instruSize--);
		immRISize=instruSize-opSize-regsize;
		immRRISize=immRISize-regsize;
		immRRRSize=immRRISize-regsize;
	}

	private boolean instructionSizeIsOk(int osize,int rsize){
		return (osize+3*rsize+4 <= instruSize) &&
		(osize+2*rsize+7 <= instruSize) &&
		(osize+rsize+10 <= instruSize);
	}
	private boolean immRISizeIsOk(int osize,int rsize){
		return (instruSize-osize-rsize <=16) ;
	}
}
