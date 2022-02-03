package editor;
// http://www.iconarchive.com/category/application/play-stop-pause-icons-by-icons-land.html
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

import javax.swing.*;

/**
 *
 * @author ENGLEBIN Laurent
 */
public class Debugger extends JPanel implements Runnable {


	int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	String pathfile = "";


	JLabel modified = new JLabel("");

	JToolBar toolBar;


	JTextArea trace;



	private int pc=0;
	private double cpi=0;
	private int pipesize=5;
	private int stall=0;
	private int stomp=0;
	private int dataforward=0;
	private int instructionexecutee=0;
	private JLabel CPI;
	private JButton runButton;
	private JButton stopButton;
	private JButton nextButton;
	private JButton resetButton;
	private JButton saveButton;
	private Architecture arch;
	private MemProg rom;
	private Registers registres;
	private Memoire ram;
	private JTextField CPIField;
	private JTextField stallField;
	private JTextField stompField;
	private JTextField instruexecField;
	private JTextField pcField;

	private int RUNMAX=1000;
	private int runcount;

	private JTextField speedUpField;

	private JTextField speedUpClockField;
	private Thread thread;
	private int oldpc;
	private JFileChooser chooser;


	public Debugger(Architecture arch,final MemProg rom,Memoire ram,Registers registres) {
		super();
		this.setLayout(new BorderLayout());



		this.arch=arch;
		this.rom=rom;
		this.ram=ram;
		this.registres=registres;

		runButton = new JButton();
		stopButton = new JButton();
		nextButton = new JButton();
		resetButton = new JButton();
		saveButton = new JButton();

		runButton.setIcon(new ImageIcon(this.getClass().getResource("icons/Run.png")));
		runButton.setPreferredSize(new Dimension(30, 30));
		runButton.setToolTipText("RUN");
		stopButton.setIcon(new ImageIcon(this.getClass().getResource("icons/Stop.png")));
		stopButton.setPreferredSize(new Dimension(30, 30));
		stopButton.setToolTipText("STOP");
		nextButton.setIcon(new ImageIcon(this.getClass().getResource("icons/Next.png")));
		nextButton.setPreferredSize(new Dimension(30, 30));
		nextButton.setToolTipText("NEXT");
		resetButton.setIcon(new ImageIcon(this.getClass().getResource("icons/Reset.png")));
		resetButton.setPreferredSize(new Dimension(30, 30));
		resetButton.setToolTipText("RESET");
		saveButton.setIcon(new ImageIcon(this.getClass().getResource("icons/Save.png")));
		saveButton.setPreferredSize(new Dimension(30, 30));
		saveButton.setToolTipText("SAVE");


		JButton [] buttons = {runButton,stopButton,nextButton,resetButton,saveButton};
		toolBar = new JToolBar();
		for (int i=0;i<buttons.length;i++){
			buttons[i].addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource().equals(runButton)){
						runcount=0;
						updateButtons(true);
						start();
					}else if (e.getSource().equals(stopButton)){
						updateButtons(false);
						stop();
					}else if (e.getSource().equals(resetButton)) {
						reset();
					} else if (e.getSource().equals(saveButton)) {
						saveTrace();
					} else if (e.getSource().equals(nextButton)) {
						runcount=0;
						step();
						updateStatistics();
						rom.highlight(oldpc);
						trace.setCaretPosition(trace.getText().length() - 1);
					}
				}});
		}
		toolBar.add(runButton);
		toolBar.add(stopButton);
		toolBar.addSeparator();
		toolBar.add(nextButton);
		toolBar.addSeparator();
		toolBar.add(resetButton);
		toolBar.add(saveButton);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);



		trace=new JTextArea();
		trace.setEditable(false);
		trace.setLineWrap(true);
		trace.setWrapStyleWord(true);
		trace.setBackground(Color.BLACK);
		trace.setForeground(Color.WHITE);
		JScrollPane scrollTrace=new JScrollPane(trace);


		scrollTrace
		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollTrace
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		//JPanel pane = new JPanel(new GridLayout(0,2));
		//	JPanel statpane = new JPanel(new GridLayout(6,2,5,5));

		JSplitPane split= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,createStatisticsPane(),scrollTrace);

		split.setDividerLocation(200+split.getInsets().left);
		this.add(split);
		this.add(toolBar, BorderLayout.NORTH);
		updateButtons(false);



		String directory = System.getProperty("user.dir");
		chooser = new JFileChooser(directory);
		//chooser.removeChoosableFileFilter(chooser.getFileFilter());
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter () {
            public boolean accept(File f) {
            	return f.isDirectory()
				|| f.getName().endsWith(".txt");
            }
            public String getDescription() {
                return ".txt";
            }
        });


	}


	public void updateButtons(boolean isRun) {
		runButton.setEnabled(!isRun);
		stopButton.setEnabled(isRun);
		nextButton.setEnabled(!isRun);
		resetButton.setEnabled(!isRun);
		saveButton.setEnabled(!isRun);
	}




	private double getCPI(){
		double nbinstr=instructionexecutee;
		int penalties = stall;
		if (stomp!=-1)	penalties+=2*stomp;

		if (nbinstr!=0)return ((pipesize-1.0)+nbinstr+penalties)/nbinstr;
		return 0;

	}

	public String getCPIstring() {
		String shortString = "          ";
		DecimalFormat threeDec = new DecimalFormat("0.00");
		if (instructionexecutee !=0) shortString = (threeDec.format(getCPI()));
		return shortString;
	}
	public String getSpeedUpString() {
		String shortString = "          ";
		DecimalFormat threeDec = new DecimalFormat("0.00");
		if (instructionexecutee !=0) shortString = (threeDec.format(5/getCPI()));
		return shortString;
	}


	public String getSpeedUpClockString() {
		String shortString = "          ";
		DecimalFormat threeDec = new DecimalFormat("0.00");
		if (instructionexecutee !=0) shortString = (threeDec.format(20/(getCPI()*14)));
		return shortString;
	}


	public boolean step() {

		 oldpc=pc;
		String tr;
		tr="PC : "+pc+" \t "+rom.getCase(pc,1)+"   "+rom.getCase(pc,2)+" \t ";
		String s=arch.calculate(rom.getCase(pc,1), registres, ram, pc)+"\n";

		tr+=s;
		trace.append(tr);
		//trace.setCaretPosition(trace.getText().length() - 1);


		if(arch.isLastInstructionStall()) stall++;
		pc=arch.getNewpc();
		if (!s.equals("halt")){
			pc++;
			instructionexecutee++;
		} /*else {
			tr="-#-#-#-#-#-#-#-#-#-\nMachine Halted\nTotal of "+instructionexecutee+" instructions executed\n";
			tr+="PC = "+pc+"\n";
			tr+="CPI = "+getCPIstring()+ " | RAW Stall = "+stall+" | RAW Stall = "+stomp;
			tr+="-#-#-#-#-#-#-#-#-#-\n";
			trace.append(tr);
		}*/
		//updateStatistics();
		// increment stomp after updatestatistics because a stompevent
		if(arch.isLastInstructionBranch()) stomp++;
		runcount++;



		if(Integer.decode(rom.getCase(pc,1))==0 || rom.getCaseB(pc,3) || runcount >= RUNMAX || s.equals("halt")){
			//rom.setCaseB(false,pc,3);
			rom.highlight(pc);
			updateStatistics();
			trace.setCaretPosition(trace.getText().length() - 1);
			updateButtons(false);
			return false;
		}
		else {
			return true;
		}


	}
	public void prev() {
		//saveas();
	}
	public void run(){

		boolean continueRun=true;
		while(thread!=null && continueRun){
			continueRun=step();
		}
		if (!continueRun && thread!=null){
			thread=null;
		}
	}

	public void start() {
		thread = new Thread(this);
		thread.setName("RUN");
		thread.start();
	}

	public void stop() {
		thread = null;
		updateStatistics();
		trace.setCaretPosition(trace.getText().length() - 1);
		rom.highlight(oldpc);
	}

	public void reset() {
		trace.setText("");
		pc=0;
		cpi=0;
		pipesize=5;
		stall=0;
		stomp=0;
		dataforward=0;
		instructionexecutee=0;
		rom.highlight(0);
		updateStatistics();
	}

	public void save(String pathfile) {
		try {
			//String text = trace.getText(0, trace.getLength());
			//text = text.replaceAll("\\n", "\r\n");
			File file = new File(pathfile);
			FileWriter fileWriter = new FileWriter(file);
			trace.write(fileWriter);
			//fileWriter.write(text);
			fileWriter.close();
			//modified.setText("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveTrace() {
		//String directory = System.getProperty("user.dir");
		//JFileChooser chooser = new JFileChooser(directory);
		//chooser.removeChoosableFileFilter(chooser.getFileFilter());
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			pathfile = chooser.getSelectedFile().getAbsolutePath();
			if (!pathfile.endsWith(".txt"))
				pathfile += ".txt";
			save(pathfile);
		}
	}

	public void updateStatistics(){
		CPIField.setText(getCPIstring());
		stallField.setText(((Integer)stall).toString());
		stompField.setText(((Integer)stomp).toString());
		instruexecField.setText(((Integer)instructionexecutee).toString());
		pcField.setText(((Integer)pc).toString());
		speedUpField.setText(getSpeedUpString());
		speedUpClockField.setText(getSpeedUpClockString());
	}




	private JPanel createStatisticsPane() {


		CPIField = new JTextField(10);
		CPIField.setEditable(false);

		stallField = new JTextField(10);
		stallField.setEditable(false);

		stompField = new JTextField(10);
		stompField.setEditable(false);

		instruexecField = new JTextField(10);
		instruexecField.setEditable(false);

		pcField = new JTextField(10);
		pcField.setEditable(false);

		speedUpField=new JTextField(10);
		speedUpField.setEditable(false);

		speedUpClockField=new JTextField(10);
		speedUpClockField.setEditable(false);

		JTextField nullField1 = new JTextField(10);
		nullField1.setEditable(false);
		nullField1.setText("");
		JTextField nullField2 = new JTextField(10);
		nullField2.setEditable(false);
		nullField2.setText("");

		//Create some labels for the fields.
		JLabel CPIFieldLabel = new JLabel(" CPI : ");
		CPIFieldLabel.setLabelFor(CPIField);
		JLabel stallFieldLabel = new JLabel(" RAW Stall : ");
		stallFieldLabel.setLabelFor(stallField);
		JLabel stompFieldLabel = new JLabel(" Branch Stall : ");
		stompFieldLabel.setLabelFor(stompField);
		JLabel pcFieldLabel = new JLabel(" next PC : ");
		pcFieldLabel.setLabelFor(pcField);
		JLabel instruexecFieldLabel = new JLabel(" Instructions : ");
		instruexecFieldLabel.setLabelFor(instruexecField);
		JLabel speedUpFieldLabel = new JLabel(" Speedup : ");
		instruexecFieldLabel.setLabelFor(speedUpField);
		JLabel speedUpClockFieldLabel = new JLabel(" Speedup (clock) : ");
		instruexecFieldLabel.setLabelFor(speedUpClockField);



		//Lay out the text controls and the labels.
		JPanel textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		textControlsPane.setLayout(gridbag);


		JLabel executionTitleLabel=new JLabel("   EXECUTION : ");
		executionTitleLabel.setForeground(Color.RED);
		JLabel pipelineTitleLabel=new JLabel("   Pipeline Statistics : ");
		pipelineTitleLabel.setForeground(Color.RED);

		JLabel[] labels = {executionTitleLabel,pcFieldLabel,instruexecFieldLabel,pipelineTitleLabel,CPIFieldLabel, stallFieldLabel, stompFieldLabel,speedUpFieldLabel,speedUpClockFieldLabel};
		JTextField[] textFields = {nullField1,pcField,instruexecField,nullField2,CPIField, stallField, stompField,speedUpField,speedUpClockField};
		addLabelTextRows(labels, textFields, gridbag, textControlsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 1.0;


		return textControlsPane;



	}


	private void addLabelTextRows(JLabel[] labels,
			JTextField[] textFields,
			GridBagLayout gridbag,
			Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST; //EAST
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
			c.fill = GridBagConstraints.NONE;      //reset to default
			c.weightx = 0.0;                       //reset to default
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER;     //end row
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			textFields[i].setBorder(null);
			textFields[i].setEditable(false);
			container.add(textFields[i], c);
		}
	}
}
