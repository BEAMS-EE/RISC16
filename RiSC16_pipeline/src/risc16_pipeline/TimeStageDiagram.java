package risc16_pipeline;


import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

import javax.swing.*;

public class TimeStageDiagram extends JPanel{

	

	private JPanel pane,paneTOP,paneLEFT; // Layout

	private JLabel label[][];
	private JLabel cycle[];		// ligne du temps
	private JLabel stage[];
	private JLabel event[];

	private String ins[]={"","","","","","NOP"};

	private int X,Y;
	private int nbrinstructions,instructionsfinies,Xcycle;

	// CONSTANTES
	private int XX=682,YY=Toolkit.getDefaultToolkit().getScreenSize().height-250;
	private int xlim=100; // nbr cycles max
	private String txtType[]={"IF","ID","EX","MEM","WB","NOP"};
	private Color color[]={Color.yellow,Color.cyan,Color.green,Color.orange,Color.magenta,Color.darkGray};
	Color colorInstru[]={new Color(10045500),new Color(10064600),new Color(10081300),new Color(10072500),new Color(10063700)};
	private JScrollBar h;
	private JScrollBar v;


	public TimeStageDiagram(){

		setBackground(new Color(255,255,255));
		setLayout(new BorderLayout());
		
		// Instructions - Ordonnée
		paneLEFT=new JPanel(new BorderLayout());
		paneLEFT.setPreferredSize (new Dimension(120,YY));
		paneLEFT.setLayout(null);
		
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
		label=new JLabel[xlim][5];
		stage=new JLabel[5];
		cycle=new JLabel[xlim];
		event=new JLabel[xlim];
		for (int x=0;x<xlim;x++){
			cycle[x]=new JLabel("",JLabel.CENTER);
			event[x]=new JLabel("",JLabel.CENTER);
			for(int y=0;y<5;y++)
				label[x][y]=new JLabel("",JLabel.CENTER);
		}
		for (int i=0;i<5;i++){
			stage[i]=new JLabel(txtType[i],JLabel.CENTER);
			stage[i].setBackground(color[i]);
			stage[i].setOpaque(true);
			stage[i].setVisible(true);
			paneLEFT.add(stage[i]);
			stage[i].setPreferredSize(new Dimension(50,18));
			stage[i].setBounds(0, (i)*18+18 ,
					50, 18);
		}
		
		
	
		// SCROLLBAR
		h = scroll.getHorizontalScrollBar();		
		v = scroll.getVerticalScrollBar();

		
		// Initialisation des variables
		reset();
		
	}
	
	// crée et place une case dans le diagramme
	public void addElem(int x,int type,int addr,String Text){
		if (x<xlim){
			label[x-1][type].setText(Text);
			label[x-1][type].setBackground(colorInstru[addr%5]);
			label[x-1][type].setOpaque(true);
			label[x-1][type].setVisible(true);
			pane.add(label[x-1][type]);
			label[x-1][type].setPreferredSize(new Dimension(50,18));
			label[x-1][type].setBounds((x-1)*50, (type)*18+18 ,
					50, 18);
			label[x-1][type].setToolTipText(ins[type] +"Cycle n°: "+x);
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
				event[x-1].setVisible(true);
			}

			if ((x+1)*50>X){
				X=X+50;
				pane.setPreferredSize (new Dimension(X,Y));
				paneTOP.setPreferredSize (new Dimension(X,22));			
			}
		
		}
	}
	// lors d'un STOMP, on ajoute ce type de bloc
	public void addNop(int x,int type){	
		label[x-1][type].setBackground(color[5]);
		label[x-1][type].setOpaque(true);
		label[x-1][type].setVisible(true);
		pane.add(label[x-1][type]);
		label[x-1][type].setPreferredSize(new Dimension(50,18));
		label[x-1][type].setBounds((x-1)*50, (type)*18+18 ,50, 18);
	}
	
	// défini quel type de bloc il faut ajouter
	public void addElem2(int cycle,int type,int addr,String instr){
		if (cycle<xlim){
		String text;
		
		text="["+Integer.toString(addr)+"]";
		if (type==0 && event[cycle-1].getText()!="STALL"){
			nbrinstructions++;
		}

		this.ins[type]="Instr n°: "+(nbrinstructions-type)+" \u25AA "+ instr+" ["+addr+"] \u25AA " ;	

		
		switch (type){
		case 0: // IF
			if(event[cycle-1].getText()=="STALL") text="";// prolonge IF
			addElem(cycle,type,addr, text);
			break;
		case 1: // ID
			if(event[cycle-1].getText()=="STALL") {	//prolonge ID
				text="";
				addElem(cycle,type,addr, text);
			}
			else if (event[cycle-1].getText()=="STOMP") addNop(cycle,type); //add black
			else addElem(cycle,type,addr, text);
			break;
		case 2: // EX
			if(event[cycle-1].getText()=="STALL") addNop(cycle,type); //add black
			else if (event[cycle-2].getText()=="STOMP" || event[cycle-1].getText()=="STOMP")addNop(cycle,type); //add black
			else addElem(cycle,type,addr, text);
			break;
		case 3: // MEM
			if(event[cycle-2].getText()=="STALL") addNop(cycle,type); //add black
			else if(event[cycle-1].getText()=="STALL" )addElem(cycle,type,addr, text);			
			else if (event[cycle-3].getText()=="STOMP" || event[cycle-2].getText()=="STOMP")addNop(cycle,type); //add black
			else addElem(cycle,type,addr, text);
			break;
		case 4: // WB
			if(event[cycle-3].getText()=="STALL") addNop(cycle,type); //add black
			else if(event[cycle-1].getText()=="STALL" || event[cycle-2].getText()=="STALL")addElem(cycle,type,addr, text);
			else if (event[cycle-4].getText()=="STOMP" || event[cycle-3].getText()=="STOMP")addNop(cycle,type); //add black
			else {
				addElem(cycle,type,addr, text);
				instructionsfinies++;
			}
			break;
		}
		
		h.setValue((int) (h.getMinimum()+(h.getMaximum()-h.getMinimum())*0.9));
		v.setValue((int) (v.getMinimum()+(v.getMaximum()-v.getMinimum())*0.9));
	}}

	public void reset(){
		
		for (int x=0;x<xlim;x++){
			for(int y=0;y<5;y++){
				label[x][y].setVisible(false);
				label[x][y].setText(null);
			}
			cycle[x].setVisible(false);
			cycle[x].setText(null);
			event[x].setVisible(false);
			event[x].setText(null);
		}
		nbrinstructions=0;
		instructionsfinies=0;
		Xcycle=0;
		X=XX;
		Y=YY;
		pane.setPreferredSize (new Dimension(X,Y));
		paneTOP.setPreferredSize (new Dimension(X,22));

		for (int i=0;i<5;i++){
			this.ins[i]="";
		
		}
		
	}

	public void previous(int c){
		
			for(int y=0;y<5;y++){
				label[c][y].setText(null);
				label[c][y].setVisible(false);

			}
			cycle[c].setVisible(false);
			cycle[c].setText(null);
			event[c].setVisible(false);
			event[c].setText(null);
		
		X=(c+1)*50;
		paneTOP.setPreferredSize (new Dimension(X,22));
		Xcycle=c;

	}
	
	
	public void setEvent(String ev){
		event[Xcycle].setText(ev);
		event[Xcycle].setForeground(Color.red);
		//event[Xcycle].setVisible(true);
		pane.add(event[Xcycle]);
		event[Xcycle].setPreferredSize(new Dimension(50,18));
		event[Xcycle].setBounds((Xcycle)*50, 0 ,50, 18);
	}

}

