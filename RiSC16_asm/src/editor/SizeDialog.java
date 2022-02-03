package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author ENGLEBIN Laurent
 */
public class SizeDialog extends JDialog implements ChangeListener {

	private int instruSize,immRISize,immRRISize,immRRRSize ;
	private int oldinstruSize,oldimmRISize,oldimmRRISize,oldimmRRRSize ;
	private SpinnerNumberModel instruModel;
	private SpinnerNumberModel immRIModel;
	private SpinnerNumberModel immRRIModel;
	private SpinnerNumberModel immRRRModel;
	private JLabel opcode1,opcode2,opcode3,regA1,regA2,regA3,regB1,regB2,regC1;
	private JLabel immRIlabel,immRRIlabel,immRRRlabel;
	private JSpinner spinnerInstruction;
	private JSpinner spinnerRI;
	private JSpinner spinnerRRI;
	private JSpinner spinnerRRR;
	private int oldregSize;
	private int oldopSize;
	JLabel bitslabels[]=new JLabel[10];

	// http://java.sun.com/docs/books/tutorial/uiswing/components/spinner.html
	public SizeDialog(JFrame f,int instruSize,int immRISize,int immRRISize,int immRRRSize,int opSize,int regSize) {

		super(f,"Immediate & Instruction's Size",true);
		oldinstruSize=instruSize;
		oldimmRISize=immRISize;
		oldimmRRISize=immRRISize;
		oldimmRRRSize=immRRRSize;
		oldregSize=regSize;
		oldopSize=opSize;


		JPanel jPanel1 = new JPanel();
		JPanel jPanel2 = new JPanel();
		JButton jButton1 = new JButton();
		JButton jButton2 = new JButton();

		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonOK_actionPerformed(e);
			}
		});
		jButton2.setText("Cancel");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonCANCEL_actionPerformed(e);
			}
		});

		this.setPreferredSize(new Dimension(500,250));
		this.setLocation(250,150);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);

		this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
		jPanel2.add(jButton1, null);
		jPanel2.add(jButton2, null);
		this.getContentPane().add(jPanel1,  BorderLayout.CENTER);


		instruModel = new SpinnerNumberModel(instruSize,4+opSize+3*regSize,16+opSize+regSize,1);
		spinnerInstruction = addLabeledSpinner(jPanel1, " Instruction Size ", instruModel);
		spinnerInstruction.setEditor(new JSpinner.NumberEditor(spinnerInstruction, "#"));
		spinnerInstruction.addChangeListener(this);

		JPanel jPanel11 = new JPanel();
		jPanel1.add(jPanel11);


		jPanel11.add(new JLabel("Immediate Values :"));


		//int min=Math.max(4+2*regSize,10);
		immRIModel = new SpinnerNumberModel(immRISize,4+2*regSize,16,1);
		spinnerRI = addLabeledSpinner(jPanel11, " RI-type ", immRIModel);
		spinnerRI.setEditor(new JSpinner.NumberEditor(spinnerRI, "#"));
		spinnerRI.addChangeListener(this);

		immRRIModel = new SpinnerNumberModel(immRRISize,4+regSize,16-regSize,1);
		spinnerRRI = addLabeledSpinner(jPanel11, " RRI-type ", immRRIModel);
		spinnerRRI.setEditor(new JSpinner.NumberEditor(spinnerRRI, "#"));
		spinnerRRI.addChangeListener(this);
		immRRRModel = new SpinnerNumberModel(immRRRSize,4,16-2*regSize,1);
		spinnerRRR = addLabeledSpinner(jPanel11, " RRR-type ", immRRRModel);
		spinnerRRR.setEditor(new JSpinner.NumberEditor(spinnerRRR, "#"));
		spinnerRRR.addChangeListener(this);

		JPanel jPanel111= new JPanel();
		jPanel111.setPreferredSize(new Dimension(500,150));
		jPanel111.setLayout(null);
		jPanel1.add(jPanel111);
		int offsetH=10;

		opcode1=new JLabel(" opcode ",JLabel.CENTER);
		opcode2=new JLabel(" opcode ",JLabel.CENTER);
		opcode3=new JLabel(" opcode ",JLabel.CENTER);
		regA1=new JLabel(" reg A ",JLabel.CENTER);
		regA2=new JLabel(" reg A ",JLabel.CENTER);
		regA3=new JLabel(" reg A ",JLabel.CENTER);
		regB1=new JLabel(" reg B ",JLabel.CENTER);
		regB2=new JLabel(" reg B ",JLabel.CENTER);
		immRIlabel = new JLabel("imm RI",JLabel.CENTER);
		immRRRlabel = new JLabel("imm RRR",JLabel.CENTER);
		immRRIlabel = new JLabel("imm RRI",JLabel.CENTER);
		regC1=new JLabel (" reg C ",JLabel.CENTER);

		opcode1.setBounds(offsetH+90,20,60,20);
		opcode2.setBounds(offsetH+90,50,60,20);
		opcode3.setBounds(offsetH+90,80,60,20);
		regA1.setBounds(offsetH+150,20,60,20);
		regA2.setBounds(offsetH+150,50,60,20);
		regA3.setBounds(offsetH+150,80,60,20);
		regB1.setBounds(offsetH+210,20,60,20);
		regB2.setBounds(offsetH+210,50,60,20);
		regC1.setBounds(offsetH+350,20,60,20);
		immRIlabel.setBounds(offsetH+210,80,200,20);
		immRRRlabel.setBounds(offsetH+270,20,80,20);
		immRRIlabel.setBounds(offsetH+270,50,140,20);

		bitslabels[0]=new JLabel(Integer.toString(instruSize-1),JLabel.CENTER);
		bitslabels[0].setBounds(offsetH+90,0,20,20);
		bitslabels[1]=new JLabel(Integer.toString(instruSize-opSize),JLabel.CENTER);
		bitslabels[1].setBounds(offsetH+130,0,20,20);
		bitslabels[2]=new JLabel(Integer.toString(instruSize-opSize-1),JLabel.CENTER);
		bitslabels[2].setBounds(offsetH+150,0,20,20);
		bitslabels[3]=new JLabel(Integer.toString(instruSize-opSize-regSize),JLabel.CENTER);
		bitslabels[3].setBounds(offsetH+190,0,20,20);
		bitslabels[4]=new JLabel(Integer.toString(instruSize-opSize-regSize-1),JLabel.CENTER);
		bitslabels[4].setBounds(offsetH+210,0,20,20);
		bitslabels[5]=new JLabel(Integer.toString(instruSize-opSize-2*regSize),JLabel.CENTER);
		bitslabels[5].setBounds(offsetH+250,0,20,20);
		bitslabels[6]=new JLabel(Integer.toString(instruSize-opSize-2*regSize-1),JLabel.CENTER);
		bitslabels[6].setBounds(offsetH+270,0,20,20);
		bitslabels[7]=new JLabel(Integer.toString(regSize),JLabel.CENTER);
		bitslabels[7].setBounds(offsetH+330,0,20,20);
		bitslabels[8]=new JLabel(Integer.toString(regSize-1),JLabel.CENTER);
		bitslabels[8].setBounds(offsetH+350,0,20,20);
		bitslabels[9]=new JLabel("0",JLabel.CENTER);
		bitslabels[9].setBounds(offsetH+390,0,20,20);

		JLabel RRRlabel=new JLabel("RRR-type",JLabel.LEFT);
		RRRlabel.setBounds(20,20,60,20);
		jPanel111.add(RRRlabel);
		RRRlabel.setOpaque(true);
		RRRlabel.setVisible(true);
		JLabel RRIlabel=new JLabel("RRI-type",JLabel.LEFT);
		RRIlabel.setBounds(20,50,60,20);
		jPanel111.add(RRIlabel);
		RRIlabel.setOpaque(true);
		RRIlabel.setVisible(true);
		JLabel RIlabel=new JLabel("RI-type",JLabel.LEFT);
		RIlabel.setBounds(20,80,60,20);
		jPanel111.add(RIlabel);
		RIlabel.setOpaque(true);
		RIlabel.setVisible(true);

		JLabel labels[]={opcode1,opcode2,opcode3,regA1,regA2,regA3,regB1,regB2,regC1,immRIlabel,immRRIlabel,immRRRlabel};
		for (int i=0;i<labels.length;i++){
			labels[i].setBorder(BorderFactory.createLineBorder(Color.black, 1));
			jPanel111.add(labels[i]);
			labels[i].setOpaque(true);
			labels[i].setVisible(true);
		}
		for (int i=0;i<bitslabels.length;i++){
			//labels[i].setBorder(BorderFactory.createLineBorder(Color.black, 1));
			jPanel111.add(bitslabels[i]);
			bitslabels[i].setOpaque(true);
			bitslabels[i].setVisible(true);
		}
	}

	public int [] getDonnees(){
		return new int[]{instruSize,immRISize,immRRISize,immRRRSize};
	}

	void jButtonOK_actionPerformed(ActionEvent e) {
		instruSize=instruModel.getNumber().intValue();
		immRISize=immRIModel.getNumber().intValue();
		immRRISize=immRRIModel.getNumber().intValue();
		immRRRSize=immRRRModel.getNumber().intValue();
		setVisible(false);
		//dispose();

	}
	void jButtonCANCEL_actionPerformed(ActionEvent e) {
		instruSize=oldinstruSize;
		immRISize=oldimmRISize;
		immRRISize=oldimmRRISize;
		immRRRSize=oldimmRRRSize;
		setVisible(false);
		//dispose();
	}

	private void updateBitsValues(int isize,int osize,int rsize){

		bitslabels[0].setText(Integer.toString(isize-1));
		bitslabels[1].setText(Integer.toString(isize-osize));
		bitslabels[2].setText(Integer.toString(isize-osize-1));
		bitslabels[3].setText(Integer.toString(isize-osize-rsize));
		bitslabels[4].setText(Integer.toString(isize-osize-rsize-1));
		bitslabels[5].setText(Integer.toString(isize-osize-2*rsize));
		bitslabels[6].setText(Integer.toString(isize-osize-2*rsize-1));
		bitslabels[7].setText(Integer.toString(rsize));
		bitslabels[8].setText(Integer.toString(rsize-1));
		bitslabels[9].setText("0");

	}

	protected JSpinner addLabeledSpinner(Container c,String label,SpinnerModel model) {
		JLabel l = new JLabel(label);
		c.add(l);
		JSpinner spinner = new JSpinner(model);
		l.setLabelFor(spinner);
		c.add(spinner);
		return spinner;
	}

	public void stateChanged(ChangeEvent e) {
		int instru=0,rri=0,rrr=0,ri=0;
		JSpinner mySpinner = (JSpinner)(e.getSource());
		if (mySpinner==spinnerInstruction){
			instru=instruModel.getNumber().intValue();
			ri=instru-oldregSize-oldopSize;
			rri=ri-oldregSize;
			rrr=rri-oldregSize;

		} else if (mySpinner==spinnerRI){
			ri=immRIModel.getNumber().intValue();
			instru=ri+oldregSize+oldopSize;
			rri=ri-oldregSize;
			rrr=rri-oldregSize;

		} else if (mySpinner==spinnerRRI){
			rri=immRRIModel.getNumber().intValue();
			ri=rri+oldregSize;
			instru=ri+oldregSize+oldopSize;
			rrr=rri-oldregSize;

		} else if (mySpinner==spinnerRRR){
			rrr=immRRRModel.getNumber().intValue();
			rri=rrr+oldregSize;
			ri=rri+oldregSize;
			instru=ri+oldregSize+oldopSize;

		}

		instruModel.setValue(instru);
		immRIModel.setValue(ri);
		immRRIModel.setValue(rri);
		immRRRModel.setValue(rrr);
		updateBitsValues(instru,oldopSize,oldregSize);
	}
}
