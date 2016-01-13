package risc16_pipeline;


import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

import javax.swing.*;

public class TimingDiagram extends JPanel{

	private JList instructions;		// Liste des instructions effectuées et en cours
	private DefaultListModel dlm;

	private JPanel pane,paneTOP,paneLEFT; // Layout

	private JLabel label[][];
	private JLabel cycle[];		// ligne du temps
	private JLabel event[];
	private JLabel CPI;			// affichage CPI

	private String ins[]={"","","","","","NOP"};
	//private int pc[];
	private int X,Y;
	private int nbrinstructions,instructionsfinies,Xcycle;

	// CONSTANTES
	private int XX=682,YY=Toolkit.getDefaultToolkit().getScreenSize().height-250;
	private int xlim=100,ylim=100; // nbr d'instructions/cycles max
	private String txtType[]={"IF","ID","EX","MEM","WB","NOP"};
	private Color color[]={Color.yellow,Color.cyan,Color.green,Color.orange,Color.magenta,Color.darkGray};
	private JScrollBar h;
	private JScrollBar v;


	public TimingDiagram(){

		setBackground(new Color(255,255,255));
		setLayout(new BorderLayout());
		
		// Instructions - Ordonnée
		dlm = new DefaultListModel();
		instructions = new JList(dlm);
		paneLEFT=new JPanel(new BorderLayout());
		paneLEFT.add("West",instructions);	//alignement à gauche
		paneLEFT.setPreferredSize (new Dimension(120,YY));
		instructions.setBackground(paneLEFT.getBackground());
		instructions.setSelectionBackground(paneLEFT.getBackground());
		
		// Ligne du temps - Abscisse
		paneTOP=new JPanel();
		paneTOP.setPreferredSize (new Dimension(XX,22));
		paneTOP.setLayout(null);

		// Panneau principal
		pane=new JPanel();
		pane.setPreferredSize (new Dimension(XX,YY));
		pane.setLayout(null);
		JScrollPane scroll=new JScrollPane(pane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(XX,YY));
		scroll.setColumnHeaderView(paneTOP);
		scroll.setRowHeaderView(paneLEFT);
		add(scroll);
		
		// Initialisation des variables
		//pc=new int[5];
		label=new JLabel[xlim][ylim];
		cycle=new JLabel[xlim];
		event=new JLabel[xlim];
		for (int x=0;x<xlim;x++){
			cycle[x]=new JLabel("",JLabel.CENTER);
			event[x]=new JLabel("",JLabel.CENTER);
			for(int y=0;y<ylim;y++)
				label[x][y]=new JLabel("",JLabel.CENTER);
		}

		// CPI
		CPI=new JLabel("CPI =           ", JLabel.CENTER);
		pane.add(CPI);
		CPI.setPreferredSize(new Dimension(60,30));
		CPI.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		
		// SCROLLBAR
		h = scroll.getHorizontalScrollBar();		
		v = scroll.getVerticalScrollBar();
		v.addAdjustmentListener(new AdjustmentListener(){
			public void adjustmentValueChanged(AdjustmentEvent e)
			{	   
				CPI.setBounds(h.getValue()+500,v.getValue()+30,100,30);
			}
		});
		h.addAdjustmentListener(new AdjustmentListener(){
			public void adjustmentValueChanged(AdjustmentEvent e)
			{	   
				CPI.setBounds(h.getValue()+500,v.getValue()+30,100,30);
			}
		});
		
		// Initialisation des variables
		reset();
		
	}
	
	// crée et place une case dans le diagramme
	public void addElem(int x,int y,int type,String Text){
		if (x<xlim && y<ylim){
			label[x-1][y-1].setText(Text);
			label[x-1][y-1].setBackground(color[type]);
			label[x-1][y-1].setOpaque(true);
			label[x-1][y-1].setVisible(true);
			pane.add(label[x-1][y-1]);
			label[x-1][y-1].setPreferredSize(new Dimension(50,18));
			label[x-1][y-1].setBounds((x-1)*50, (y-1)*18 ,
					50, 18);
			label[x-1][y-1].setToolTipText(ins[type] +"Cycle n°: "+x);
			if (Text=="WB") {
				instructionsfinies++;
			}
			if (x>Xcycle){
				Xcycle=x;
				paneTOP.add(cycle[x-1]);
				cycle[x-1].setText(Integer.toString(Xcycle));
				cycle[x-1].setVisible(true);
				cycle[x-1].setPreferredSize(new Dimension(50,18));
				cycle[x-1].setBounds((x-1)*50 , 1 ,	50, 20);
				cycle[x-1].setBorder(BorderFactory.createLineBorder(Color.black, 1));
			}
			if ((y+1)*18>Y){
				Y=Y+18;
				pane.setPreferredSize (new Dimension(X,Y));
				paneLEFT.setPreferredSize(new Dimension(120,Y));
			}
			if ((x+1)*50>X){
				X=X+50;
				pane.setPreferredSize (new Dimension(X,Y));
				paneTOP.setPreferredSize (new Dimension(X,22));			
			}
		
		}
	}
	// lors d'un STOMP, on ajoute ce type de bloc
	public void addNop(int x,int y){	
		label[x-1][y-1].setBackground(color[5]);
		label[x-1][y-1].setOpaque(true);
		label[x-1][y-1].setVisible(true);
		pane.add(label[x-1][y-1]);
		label[x-1][y-1].setPreferredSize(new Dimension(50,18));
		label[x-1][y-1].setBounds((x-1)*50, (y-1)*18 ,50, 18);
	}
	
	// défini quel type de bloc il faut ajouter
	public void addElem2(int cycle,int type,int addr,String instr){
		if (cycle<xlim ){
		String text;
		
		text=txtType[type];
		if (type==0 && event[cycle-1].getText()!="STALL"){
			nbrinstructions++;
			dlm.addElement("<html>"+nbrinstructions+". <FONT color=\"#0000FF\">"+instr+"</font> <FONT color=\"#FF0000\">["+addr+"]</font></html>");
		}

		this.ins[type]="Instr n°: "+(nbrinstructions-type)+" \u25AA "+ instr+" ["+addr+"] \u25AA " ;	

		
		switch (type){
		case 0: // IF
			if(event[cycle-1].getText()=="STALL") text="";// prolonge IF
			addElem(cycle,nbrinstructions-type,type,text);
			break;
		case 1: // ID
			if(event[cycle-1].getText()=="STALL") {	//prolonge ID
				text="";
				addElem(cycle,nbrinstructions-type,type,text);
			}
			else if (event[cycle-1].getText()=="STOMP") addNop(cycle,nbrinstructions-type); //add black
			else addElem(cycle,nbrinstructions-type,type,text);
			break;
		case 2: // EX
			if(event[cycle-1].getText()=="STALL") ;//no add
			else if (event[cycle-2].getText()=="STOMP" || event[cycle-1].getText()=="STOMP")addNop(cycle,nbrinstructions-type); //add black
			else addElem(cycle,nbrinstructions-type,type,text);
			break;
		case 3: // MEM
			if(event[cycle-2].getText()=="STALL") ;//no add
			else if(event[cycle-1].getText()=="STALL" )addElem(cycle,nbrinstructions-type+1,type,text);			
			else if (event[cycle-3].getText()=="STOMP" || event[cycle-2].getText()=="STOMP")addNop(cycle,nbrinstructions-type); //add black
			else addElem(cycle,nbrinstructions-type,type,text);
			break;
		case 4: // WB
			if(event[cycle-3].getText()=="STALL") ;//no add
			else if(event[cycle-1].getText()=="STALL" || event[cycle-2].getText()=="STALL")addElem(cycle,nbrinstructions-type+1,type,text);
			else if (event[cycle-4].getText()=="STOMP" || event[cycle-3].getText()=="STOMP")addNop(cycle,nbrinstructions-type); //add black
			else addElem(cycle,nbrinstructions-type,type,text);
			break;
		}
		
		// mis à jour du CPI
		CPI.setText("CPI = "+getCPIstring());
		h.setValue((int) (h.getMinimum()+(h.getMaximum()-h.getMinimum())*0.9));
		v.setValue((int) (v.getMinimum()+(v.getMaximum()-v.getMinimum())*0.9));
	}}

	public void reset(){
		
		for (int x=0;x<xlim;x++){
			for(int y=0;y<ylim;y++){
				label[x][y].setVisible(false);
				label[x][y].setText(null);
			}
			cycle[x].setVisible(false);
			cycle[x].setText(null);
			event[x].setVisible(false);
			event[x].setText(null);
		}
		dlm.clear();
		nbrinstructions=0;
		instructionsfinies=0;
		Xcycle=0;
		X=XX;
		Y=YY;
		pane.setPreferredSize (new Dimension(X,Y));
		paneTOP.setPreferredSize (new Dimension(X,22));
		CPI.setText("CPI =           ");
		for (int i=0;i<5;i++){
			this.ins[i]="";
		
		}
		
	}

	public void previous(int c){
		
			for(int y=0;y<ylim;y++){
				if (label[c][y].getText()=="IF") {
					dlm.removeElement(dlm.lastElement());
					nbrinstructions--;
				}
				else if (label[c][y].getText()=="WB") {
					instructionsfinies--;
				}
				label[c][y].setText(null);
				label[c][y].setVisible(false);

			}
			cycle[c].setVisible(false);
			cycle[c].setText(null);
			event[c].setVisible(false);
			event[c].setText(null);
		
		X=(c+1)*50;
		Y=(nbrinstructions+1)*18;
		pane.setPreferredSize (new Dimension(X,Y));
		paneTOP.setPreferredSize (new Dimension(X,22));
		paneLEFT.setPreferredSize (new Dimension(120,Y));
		
		Xcycle=c;

		CPI.setText("CPI = "+getCPIstring());
	}
	
	
	public void setEvent(String ev){
		event[Xcycle].setText(ev);
	}

	public double getCPI(){
		double cycle=Xcycle;
		double nbinstr=instructionsfinies;
		if (nbinstr!=0)return cycle/nbinstr;
		return 0;
	}
	public String getCPIstring() {
		String shortString = "          ";
		DecimalFormat threeDec = new DecimalFormat("0.000");
		if (instructionsfinies !=0) shortString = (threeDec.format(getCPI()));
		return shortString;
	}

	public int getXcycle() {
		return Xcycle;
	}

}
