package risc16_pipeline;



import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


////////////// CONSTRUCTOR //////////

public class StageView extends JPanel implements ActionListener, ChangeListener {
	private JSlider slide;
	private JButton buttons[];
	private String ins[];
	private int stage=0,step=0;
	private Dessin dessin;
	private String txtType[]={"IF","ID","EX","MEM","WB","NOP"};
	private Border bord = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 4);
	private Border empty=BorderFactory.createEmptyBorder(4,4,4,4);

	public StageView(){
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		ins = new String[5];
		buttons=new JButton[5];


		add(Box.createRigidArea(new Dimension(0,5))); 
		for (int i=0;i<5;i++){
			buttons[i] = new JButton(txtType[i]);
			buttons[i].setOpaque(true);
			buttons[i].setMinimumSize(new Dimension(160, 40));
			buttons[i].setMaximumSize(new Dimension(250, 50));
			buttons[i].setBorder(null);
			add(buttons[i]);
			add(Box.createRigidArea(new Dimension(0,5)));
			buttons[i].addActionListener(this);
			buttons[i].setBorder(empty);
		}
		buttons[0].setBackground(Color.yellow);
		buttons[1].setBackground(Color.cyan);		 
		buttons[2].setBackground(Color.green);
		buttons[3].setBackground(Color.orange);
		buttons[4].setBackground(Color.magenta);
		
		
		
		stage=0;
		selectStage(0);







		slide=new JSlider(JSlider.HORIZONTAL,0,13,0);
		slide.setPaintTicks(true);
		slide.setMajorTickSpacing(1);
		//Create the label table.
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();

		labelTable.put(new Integer( 0 ),new JLabel("1"));
		labelTable.put(new Integer( 1 ),new JLabel(""));
		labelTable.put(new Integer( 2 ),new JLabel("3") );      
		labelTable.put(new Integer( 3 ),new JLabel("") );                    
		labelTable.put(new Integer( 4 ),new JLabel("5") );
		labelTable.put(new Integer( 5 ),new JLabel("") );                  
		labelTable.put(new Integer( 6 ),new JLabel("7") );             
		labelTable.put(new Integer( 7 ),new JLabel("") );                
		labelTable.put(new Integer( 8 ),new JLabel("9") );
		labelTable.put(new Integer( 9 ),new JLabel("") );
		labelTable.put(new Integer( 10 ),new JLabel("11") );
		labelTable.put(new Integer( 11 ),new JLabel("") );      
		labelTable.put(new Integer( 12 ),new JLabel("13") );                    
		labelTable.put(new Integer( 13 ),new JLabel("") );


		slide.setSnapToTicks(true) ;
		slide.setLabelTable(labelTable);
		slide.setPaintLabels(true);
		slide.addChangeListener(this);

		TitledBorder  title = BorderFactory.createTitledBorder("Micro-cycles");
		slide.setBorder(title);
		add(slide);

	}
	///////////////// METHODS ////////////////////



	public void setText(int i,int addr,String asm)
	{
		String text;
		if (asm==null) text=txtType[i];
		else text="<html><center>"+txtType[i]+"<br><FONT color=\"#0000FF\">"+asm+"</font> <FONT color=\"#6E6E6E\">["+addr+"]</font></center></html>";
		buttons[i].setText(text);
	}
	
	public void selectStage(int i){
		for (int j=0;j<5;j++)		buttons[j].setBorder(empty);
		if(i!=-1) buttons[i].setBorder(bord);
		stage=i;
	}
	public int getSelect(){
		return stage;
	}
	public void incrStage(){
		if(stage!=-1){
			stage++;
			if (stage==5) stage=0;
			selectStage(stage);}
	}
	public void decrStage(){
		if(stage!=-1){
			stage--;
			if (stage==-1) stage=4;
			selectStage(stage);}
	}

	public void setStep(int step){
		if (step==0) this.step=0;
		else this.step = step-1;
		slide.setValue(this.step);
		if(step==14) incrStage();
	}

	public void reset(){
		selectStage(0);
		step=0;
		slide.setValue(0);
		for (int j=0;j<5;j++)		{
			buttons[j].setText(txtType[j]);
		}
	}

	public void setDessin(Dessin dessin){
		this.dessin=dessin;
	}


	///////////////////// ACTIONS /////////////////////

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
			int fps = (int)source.getValue();

			if (step < fps){
				int difference = fps-step;
				for(int i=0;i<difference;++i)
					dessin.step(true,true);

				//system.out.println("++STEP++    step="+step);
			}
			else if (step>fps){
				dessin.previoushalfclock(fps+1);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttons[0]) {
			if(getSelect()==0) {selectStage(-1);}
			else {selectStage(0);dessin.scroll(0);}
			dessin.repaint();
		}
		else if (e.getSource() == buttons[1]) {
			if(getSelect()==1) {selectStage(-1);}
			else {selectStage(1);dessin.scroll(0);}
			dessin.repaint();
		}
		else if (e.getSource() == buttons[2]) {	
			if(getSelect()==2) {selectStage(-1);}
			else {selectStage(2);dessin.scroll(0.2);}
			dessin.repaint();
		}
		else if (e.getSource() == buttons[3]) {
			if(getSelect()==3) {selectStage(-1);}
			else {selectStage(3);dessin.scroll(0.4);}
			dessin.repaint();
		}
		else if (e.getSource() == buttons[4]) {
			if(getSelect()==4) {selectStage(-1);}
			else {selectStage(4);dessin.scroll(0.4);}
			dessin.repaint();
		}
	}

}
