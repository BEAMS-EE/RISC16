package seq_final;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;
import java.util.Hashtable;


/**
 *
 * @author ENGLEBIN Laurent
 */
public class Slider extends JPanel implements ChangeListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;//?

	private JSlider slide;
	private DessinSeq d;
	private int step,init;//,max  init=valeur initiale


	public Slider(int min, int max, int init){


		this.init=init;


		slide=new JSlider(JSlider.HORIZONTAL,min,max,init);
		slide.setPaintTicks(true);
		slide.setMajorTickSpacing(1);
		slide.addChangeListener(this);
		//Create the label table.
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();


		labelTable.put(0, new JLabel("1"));
		labelTable.put(1, new JLabel(""));
		labelTable.put(2, new JLabel("3") );
		labelTable.put(3, new JLabel("") );
		labelTable.put(4, new JLabel("5") );
		labelTable.put(5, new JLabel("") );
		labelTable.put(6, new JLabel("7") );
		labelTable.put(7, new JLabel("") );
		labelTable.put(8, new JLabel("9") );
		labelTable.put(9, new JLabel("") );
		labelTable.put(1, new JLabel("11") );
		labelTable.put(1, new JLabel("") );
		labelTable.put(1, new JLabel("13") );
		labelTable.put(1, new JLabel("") );
		labelTable.put(1, new JLabel("15") );
		labelTable.put(1, new JLabel("") );
		labelTable.put(1, new JLabel("17") );
		labelTable.put(1, new JLabel("") );
		labelTable.put(1, new JLabel("19") );
		labelTable.put(1, new JLabel("") );
		labelTable.put(2, new JLabel("21") );


		slide.setSnapToTicks(true) ;
		slide.setLabelTable(labelTable);
		slide.setPaintLabels(true);


		setLayout(new GridBagLayout());
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.fill = GridBagConstraints.HORIZONTAL;


		JLabel IF = new JLabel("IF",JLabel.CENTER);
		JLabel ID = new JLabel("ID/RF",JLabel.CENTER);
		JLabel EX = new JLabel("EX",JLabel.CENTER);
		JLabel WB = new JLabel("WB",JLabel.CENTER);

		IF.setBackground(new Color(60,100,160));
		ID.setBackground(Color.gray);
		EX.setBackground(new Color(153,204,255));
		WB.setBackground(new Color(204,255,255));

		IF.setOpaque(true);
		ID.setOpaque(true);
		EX.setOpaque(true);
		WB.setOpaque(true);

		constraint.weightx =0.28;// 0.22;
		constraint.gridx = 1;
		constraint.gridy = 0;
		add(IF, constraint);
		constraint.weightx =0.35;// 0.33;
		constraint.gridx = 2;
		constraint.gridy = 0;
		add(ID, constraint);
		constraint.weightx =0.1;// 0.06;
		constraint.gridx = 3;
		constraint.gridy = 0;
		add(EX, constraint);
		constraint.weightx =0.3;// 0.33;
		constraint.gridx = 4;
		constraint.gridy = 0;
		add(WB, constraint);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.weightx = 2.0;
		constraint.gridwidth = 5;
		constraint.gridx = 0;
		constraint.gridy = 1;
		add(slide, constraint);

		setBackground(new Color(220, 220, 220));

		slide.setBackground(new Color(220, 220, 220));

		setVisible(true);
	}

	public void setDessinSeq(DessinSeq d){
		this.d=d;
	}


	/* Listen to the slider*/
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
			int fps = source.getValue();


			if (step < fps){
				int difference = fps-step;
				for(int i=0;i<difference;++i)
					d.step(true,true);

				//system.out.println("++STEP++    step="+step);
			}
			else if (step>fps){
				d.previoushalfclock(fps+1);
			}

		}
	}

	public void reset(){
		step=init;
		slide.setValue(step);
	}



	public void setStep(int step){
		this.step = step-1;
		slide.setValue(this.step);
	}
	public int getStep(){
		return step;
	}
}
