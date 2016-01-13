//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
package seq_final;
import java.awt.*;
import java.io.File;

import javax.swing.*;


public class DessinSeq extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1460854940293728819L;

	private JTextField msg;

	private int etape=0;//compte le nombre de fois qu'on fais un step
	private Color laCouleur;
	//====Internal Frame====
	private Slider slider;

	// ==== composants ====
	private Add Add;
	private Alu Alu;
	private Clock Clock;
	private Ctl CtlSeq;
	private Incrementeur Inc;
	private RegistreBank RegFile;
	private Memoire Ram;
	private MemProg Rom;
	private SignExt Sign,Sign2;
	private LeftShift Shift;
	private InstrReg IR;
	private Multiplexeur MuxPC, MuxRf, MuxTGT,MuxAlu1,MuxAlu2;
	private Register PC0;  

	//==== Bus ====
	private Bus   b00,b01,b02,b03,b04,b05,b06,b07,b08,b09;
	private Bus   b10,b11,b12,b13,b14;
	private Bus   c00,c01,c02,c03,c04,c05,c06;
	private CtlSignal  MUXpc,MUXalu1,MUXalu2,MUXrf,MUXtgt;
	private CtlSignal  WErf,WEdmem,FUNCalu,PC0Read, PSEN;

	//==== regroupement dans des vecteurs -> paint & reset façilement ====
	private Bus   Bus16[],Bus16h[],Busx[];  // bus de 16bits |  h = hidden  |  x = -de 16bit
	private CtlSignal Busctl[];
	private Multiplexeur Mux[];

	private JTabbedPane tabbedPane;

	private int[][] tabReg;

	private String[][] IRsave;

	private int[][] tabBus;

	private int retour;



	private File[] ramTemp;

	private int nbretour;

	//**************************************************************************************
	//   CONSTRUCTOR
	//**************************************************************************************
	public DessinSeq(String path, Slider slider) {
		setBackground(new Color(220, 220, 220));
		this.slider=slider;
		laCouleur = new Color(0,255,255);
		// TRES IMPORTANT de d aboprd definir TOUS lmes bus
		/////// BUS ///////////////////////////////////////////////////////////
		/////// 16-bits ///////////////////////////////////////////////////////
		int x00[]={70,70,70,100,70,200,200,0};
		int y00[]={120,145,200,200,135,135,120,0};
		b00=new Bus(fillMat(x00,2,4),fillMat(y00,2,4));
		int x01[]={150,150};
		int y01[]={250,280};
		b01=new Bus(x01,y01);
		int x02[]={200,200,90,90,    220,480,480,0,  201,201,250,250 };
		int y02[]={100,50,50,70,     90,90,140,0,    90,90,90,60 };
		b02=new Bus(fillMat(x02,3,4),fillMat(y02,3,4));
		int x03[]={275,275};
		int y03[]={200,60};
		b03=new Bus(x03,y03);
		int x04[]={260,260,70,70};
		int y04[]={40,30,30,70};
		b04=new Bus(x04,y04);
		int[] x05={390,520,520};
		int[] y05={470,470,520};
		b05=new Bus(x05,y05);
		int[] x06={390,440,440};
		int[] y06={500,500,520};
		b06=new Bus(x06,y06);
		int x07[]={460,460,0,  461,620,620};
		int y07[]={401,520,0,  420,420,370};
		b07=new Bus(fillMat(x07,2,3),fillMat(y07,2,3));
		int x08[]={540,540};
		int y08[]={401,520};
		b08=new Bus(x08,y08);
		int x09[]={450,450};
		int y09[]={530,550};
		b09=new Bus(x09,y09);
		int x10[]={530,530};
		int y10[]={530,550};
		b10=new Bus(x10,y10);
		int x11[]={490,490,720,720,50,50,    500,500,0,0,0,0, 720, 680,680,0,0,0};
		int y11[]={600,610,610,10,10,70,     11,140,0,0,0,0,  390, 390,370,0,0,0};
		b11=new Bus(fillMat(x11,3,6),fillMat(y11,3,6));
		int x12[]={650,650,520,520};
		int y12[]={270,120,120,140};
		b12=new Bus(x12,y12);
		int x13[]={68,68};
		int y13[]={90,100};
		b13=new Bus(x13,y13);
		int x14[]={500,500};
		int y14[]={160,190};
		b14=new Bus(x14,y14);

		Bus16 = new Bus[15];
		Bus16[0]=b00;  Bus16[1]=b01;  Bus16[2]=b02;  Bus16[3]=b03;  Bus16[4]=b04;  Bus16[5]=b05;
		Bus16[6]=b06;  Bus16[7]=b07;  Bus16[8]=b08; Bus16[9]=b09;   Bus16[10]=b10; Bus16[11]=b11;
		Bus16[12]=b12; Bus16[13]=b13; Bus16[14]=b14; 
		Bus16h = new Bus[0];//    Bus16h[0]=b03;  Bus16h[1]=b25;   Bus16h[2]=b50; Bus16h[3]=b63;  Bus16h[4]=b64;

		/////// BUS ///////////////////////////////////////////////////////////
		/////// x-bits ///////////////////////////////////////////////////////
		int[] xc00={160,160,275,275,275,300,   275,300,0,0,0,0,   275,275,0,0,0,0};
		int[] yc00={321,330,330,420,500,500,   470,470,0,0,0,0,   330,220,0,0,0,0 };
		c00=new Bus(fillMat(xc00,3,6),fillMat(yc00,3,6),2);
		int xc01[]={172,172,115,115};//rc
		int yc01[]={321,380,380,400};
		c01=new Bus(xc01,yc01,2);
		int xc02[]={96,96,0,   96,390,0};//ra
		int yc02[]={321,400,0, 360,360,0};
		c02=new Bus(fillMat(xc02,2,3),fillMat(yc02,2,3),2);
		int xc03[]={120,120,390};//rb
		int yc03[]={321,340,340};
		c03=new Bus(xc03,yc03,2);
		int xc04[]={105,105,230,230,390};
		int yc04[]={410,425,425,380,380};
		c04=new Bus(xc04,yc04,2);
		int xc05[]={72,72};
		int yc05[]={321,500};
		c05=new Bus(xc05,yc05,2);
		int xc06[]={436,350,350,105};             //EQ!
		int yc06[]={570,570,530,530};
		c06=new Bus(xc06,yc06,2);

		Busx = new Bus[7];
		Busx[0]=c00;  Busx[1]=c01;  Busx[2]=c02;  Busx[3]=c03; Busx[4]=c04;  Busx[5]=c05;  Busx[6]=c06;

		/////// BUS //////////////////////////////////////////////////////////////////
		/////// signaux Controle ///////////////////////////////////////////////////////
		int xd1[]={40,10,10,35};
		int yd1[]={530,530,80,80};
		int xd2[]={100,200,650,650};
		int yd2[]={560,610,610,370};
		int xd3[]={41,5,5,20};
		int yd3[]={540,540,110,110};
		int xd4[]={104,490,490,510};
		int yd4[]={515,515,525,525};
		int xd5[]={40,10,10,100};//PAS SUR!
		int yd5[]={530,530,170,170};//PAS SUR!

		MUXpc    = new CtlSignal(xd1,yd1,         	"MUXpc");
		MUXalu1  = new CtlSignal(xd4,yd4, 			"MUXalu1");
		MUXalu2  = new CtlSignal(104,525,430,525, 	"MUXalu2");
		MUXrf    = new CtlSignal(90,500,90,425, 	"MUXrf");
		MUXtgt   = new CtlSignal(100,500,465,150,	"MUXtgt");
		WErf     = new CtlSignal(100,500,390,320, 	"WErf");
		WEdmem   = new CtlSignal(xd2,yd2,         	"WEdmem");
		FUNCalu  = new CtlSignal(105,530,440,570, 	"FUNCalu");
		PC0Read  = new CtlSignal(xd3,yd3,			"WE_PC0");
		PSEN	  = new CtlSignal(xd5,yd5,       	"PSEN");

		MUXpc.setStringPos(15,240);    
		MUXalu1.setStringPos(180,505);  
		MUXalu2.setStringPos(230, 545);  
		MUXrf.setStringPos(100, 470);    
		MUXtgt.setStringPos(310,300);   
		WErf.setStringPos(180, 460);     
		WEdmem.setStringPos(240,605);   
		FUNCalu.setStringPos(265,570);  
		PC0Read.setStringPos(15,240);
		PSEN.setStringPos(15,380);

		Busctl = new CtlSignal[10]; //  ATTENTION : ordre important (cf CTL plus bas)
		Busctl[1]=MUXalu1; Busctl[2]=MUXalu2; Busctl[3]=MUXpc; Busctl[4]=MUXrf; Busctl[5]=MUXtgt;
		Busctl[0]=FUNCalu; Busctl[6]=WErf;    Busctl[7]=WEdmem; Busctl[8]=PC0Read; Busctl[9]=PSEN;

		////////////////////////////////////////////////////////////////////////////////////
		/////// COMPOSANTS //////////////////////////////////////////////////////////////////
		Bus[] IRout= new Bus[5];
		IRout[0]=c05; IRout[1]=c02; IRout[2]=c03;   IRout[3]=c01;  IRout[4]=c00;
		//String RomColumnNames[] = {"Address", "Content", "ASM"};
		String RomColumnNames[] = {"Address", "Content", "ASM","",""};
		Add     = new Add(230, 40, 60, 20, b04,   b02,b03);
		Alu     = new Alu(430, 550, 120, 50, b11, c06, b10,b09); // OUT eq  | s1 s2
		Clock   = new Clock(10,580, 130, 20);
		Inc     = new Incrementeur(190,100,25,20, b02,b00);
		IR      = new InstrReg(60,300,125,20,b01,IRout);
		Ram     = new MemData("RiSC16 - Data Memory", 600, 270, 100, 100, b12, b11, b07);
		Rom     = new MemProg("RiSC16 - Program Memory", 100, 150, 100, 100,RomColumnNames,b01,b00);
		RegFile = new RegistreBank(390,190,180,210, b08,b07,   b14,c03,c04,c02);
		Shift   = new LeftShift(300,460,90,20, b05,c00);
		Sign    = new SignExt (300,490,90,20, b06,c00);
		Sign2   = new SignExt (230,200,90,20, b03,c00);

		RegFile.setCtlBus(WErf);
		Alu.setCtlBus(FUNCalu);
		Ram.setCtlBus(WEdmem,false);
		Rom.setCtlBus(PSEN,true);
		////////////////////////////////////////////////////////////////////////
		/////// CTL  ///////////////////////////////////////////////////////////

		Bus[] Bustmp = new Bus[2];    Bustmp[0]=c05;  Bustmp[1]=c06;

		CtlSeq = new Ctl(42,500,60);       CtlSeq.setInput(Bustmp);     // OP & EQ
		CtlSeq.setSignal(Busctl); // nb : défini plus haut (donne les signaux ctlsignal)


		////////////////////////////////////////////////////////////////////////
		/////// REGISTRER (LATCHES) ////////////////////////////////////////////
		PC0  = new Register("PC0", 20,100,100,20, b00, b13);
		PC0.setCtlRead(PC0Read);
		PC0.PC0();


		////////////////////////////////////////////////////////////////////////////
		////////// MULTIPLEXER /////////////////////////////////////////////////////
		MuxPC   = new Multiplexeur(3,30,70,80,20,0,   b13,  b11,b04,b02);
		MuxAlu1 = new Multiplexeur(2,510,520,40,10,0, b10,  b05,b08);
		MuxAlu2 = new Multiplexeur(2,430,520,40,10,0, b09,  b06,b07);
		MuxRf   = new Multiplexeur(2,85,400,40,10,0,  c04,  c02,c01);
		MuxTGT  = new Multiplexeur(3,460,140,80,20,0, b14,  b02,b11,b12);

		MuxPC.setCtlBus(MUXpc);
		MuxAlu1.setCtlBus(MUXalu1);
		MuxAlu2.setCtlBus(MUXalu2);
		MuxRf.setCtlBus(MUXrf);
		MuxTGT.setCtlBus(MUXtgt);

		Mux = new Multiplexeur[5];
		Mux[0]=MuxPC;   Mux[1]=MuxAlu1;   Mux[2]=MuxAlu2; Mux[3]=MuxRf; Mux[4]=MuxTGT;

		////////////////////////////////////////////////////////////////////////
		///// > couleur< ///////////////////////////////////////////////////////////////////////////
		for (int i = 0; i < Mux.length; i++)       Mux[i].setColorDefault(laCouleur);
		Rom.setColorDefault(laCouleur);
		Ram.setColorDefault(laCouleur);
		Add.setColorDefault(laCouleur);
		Alu.setColorDefault(laCouleur);
		Inc.setColorDefault(laCouleur);
		RegFile.setColorDefault(laCouleur);
		Sign.setColorDefault(laCouleur);
		Shift.setColorDefault(laCouleur);
		Sign2.setColorDefault(laCouleur);
		CtlSeq.setColorDefault(laCouleur);


		///// > INITIALISATION < ///////////////////////////////////////////////////////////////////
		Rom.fileopen(path);              // load the default memory content
		init();
	}
	//**************************************************************************************
	//**************************************************************************************
	//**************************************************************************************
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g = (Graphics2D) g;             //permet l'utilisation de API java2D
		g.setFont(new Font("Monospaced", Font.PLAIN, g.getFont().getSize()));

		/////// **** ///////////////////////////////////////////////////////////
		Clock.paint(g);    Rom.dessine(g);   Ram.dessine(g);
		Add.paint(g);      Alu.paint(g);     Inc.paint(g);
		RegFile.paint(g);  IR.paint(g);      Sign.paint(g);    Shift.paint(g);
		PC0.paint(g);      Sign2.paint(g);	//DLatch.paint(g);
		///////  CTL --- REGISTRER --- MULTIPLEXER /////////////////////////////////////
		CtlSeq.paint(g);
		//for (int i = 0; i < Ctlx.length; i++)      Ctlx[i].paint(g);
		for (int i = 0; i < Mux.length; i++)       Mux[i].paint(g);
		/////// BUS ///////////////////////////////////////////////////////
		for (int i = 0; i < Bus16.length; i++)     Bus16[i].paint( (Graphics2D) g);
		for (int i = 0; i < Busx.length; i++)      Busx[i].paint( (Graphics2D) g);
		for (int i = 0; i < Busctl.length; i++)    Busctl[i].paint( (Graphics2D) g);

		g.setColor(Color.black);
		g.drawString("SRC1",350,338);
		g.drawString("TGT", 350,358);
		g.drawString("SRC2",350,378);
	}
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	public void step(boolean paint,boolean nouveau) {
		Graphics g = this.getGraphics();
		g.setFont(new Font("Monospaced", Font.PLAIN, g.getFont().getSize()));
		Clock.inc();

		++etape;//conte les étapes pour pouvoir revenir en arrière

		CtlSeq.incrState();

		int state=CtlSeq.getState();

		slider.setStep(state);

		if (state==8){
			setInactive();
		}

		if (state==1){
			setActive();
		}


		if(paint){//permet de ne pas devoir afficher tous les états lorsqu'on fait -1/2 clock
			Clock.paint(g);
		}
		System.out.println("======= CLOCK :  " + Clock.getTime()+ " |  "+Clock.getLevel()+" ==============");
		/***********************************************************************************************/
		/***********************************************************************************************/

		switch(state)
		{

		case 1:
			msg.setText("STATE n° "+(state)+"\t\u25AA INSTRUCTION FETCH \u25AA\t PSEN > Program Memory");
			break;
		case 2:
		case 3:
			msg.setText("STATE n° "+(state)+"\t\u25AA INSTRUCTION FETCH \u25AA\t Program Memory");
			break;
		case 4:
		case 5:
			msg.setText("STATE n° "+(state)+"\t\u25AA INSTRUCTION FETCH \u25AA\t IR");
			break;
		case 6:
			msg.setText("STATE n° "+(state)+"\t\u25AA INSTRUCTION DECODE/REGISTER FILE \u25AA\t Ctl");
			//msg.setText("    \u25AA ID/RF \u25AA\t MUXrf & MUX alu 1 & MUX alu 2");
			break;
		case 7:
		case 8:
			msg.setText("STATE n° "+(state)+"\t\u25AA INSTRUCTION DECODE/REGISTER FILE \u25AA\t Ctl / RF(SRC1)");
			//msg.setText("    \u25AA ID/RF \u25AA\t MUXrf & MUX alu 1 & MUX alu 2");
			break;
		case 9:
			msg.setText("STATE n° "+(state)+"\t\u25AA INSTRUCTION DECODE/REGISTER FILE \u25AA\t Mux_RF");
			//msg.setText("    \u25AA ID/RF \u25AA\t MUXrf & MUX alu 1 & MUX alu 2");
			break;
		case 10:
		case 11:
		case 12:
			msg.setText("STATE n° "+(state)+"\t\u25AA INSTRUCTION DECODE/REGISTER FILE \u25AA\t RF(SRC2)");
			break;
		case 13:
		case 14:
			msg.setText("STATE n° "+(state)+"\t\u25AA EXECUTION \u25AA\t 	Fonction ALU");

			break;

		case 15:
		case 16:
		case 17:
			msg.setText("STATE n° "+(state)+"\t\u25AA WRITEBACK \u25AA\t Memory Access");

			break;

		case 18:
		case 19:
			msg.setText("STATE n° "+(state)+"\t\u25AA WRITEBACK \u25AA\t Register WE");
			break;
		case 20:
			msg.setText("STATE n° "+(state)+"\t\u25AA WRITEBACK \u25AA\t Register WE & PC");
			break;


		}
		/***************************************************************************************************/    
		/***************************************************************************************************/
		//----------------déroulement------------------------------------------


		if(state==6 || state==7 || state==8 || state==9 || state== 10|| state==11 || state==12 || state==13 || state==19 || state==20 || state==1)
			RegFile.act();



		if (state==15 || state==16 || state==17 || state==18){
			Ram.act();
		}
		if (state==18)
			Ram.setIdle();//a mettre?

		CtlSeq.act();

		if(state==18)
			RegFile.act();


		if (state==19 || state ==20 ||state==1)
			PC0.act();   

		if (state==1 || state==2 || state==3 || state==4)
		{
			Rom.act();
			Inc.act();
		}

		if (state==4 || state==5 || state==6)
			IR.act();

		if (state ==6 || state==7 || state==8){
			Shift.act();
			Sign2.act();
			Sign.act();
		}

		if(state==8 || state==9 || state==10){
			Add.act();
		}
		if(state==9){
			Add.receive();
		}

		if(state==13||state==14||state==15){//(state==9 || state==10|| state==13||state==14||state==15){//(state==13||state==14||state==15){
			Alu.act();
		}

		if (state == 8 || state == 9)
			Alu.checkInput();

		/****/
		//Mux[0]=MuxPC;   Mux[1]=MuxAlu1;   Mux[2]=MuxAlu2; Mux[3]=MuxRf; Mux[4]=MuxTGT;    
		Mux[0].act();
		Mux[1].act();
		Mux[2].act();
		Mux[3].act();
		Mux[4].act();

		if(state==16){
			Mux[2].receive();//sinon prob pour SW, le mux a déjà fais un receive et un latch, et il faut donc désactiver le bus... 
		}
		if(state==19){
			Mux[4].receive();//sinon prob pour JALR
		}
		if(state == 17){
			String op = Rom.getCase(PC0.getData(),2);
			if (op.indexOf("lw") != -1) {
				b12.enable();//sinon il s'éteind immédiatement
			}   	  
		}else if(state == 18){
			b12.disable();
		}

		String op = null;
		op = Rom.getCase(PC0.getData(),2);
		
		// on surligne l'adresse où une donnée sera lue ou écrite dans la Data Mem
		if ((op != null) && (op.indexOf("sw") != -1 || op.indexOf("lw") != -1) && (state >= 15) && (state <= 19))
		{
			tabbedPane.setSelectedIndex(1);
			Ram.highlight(b11.readData());
		}
		else tabbedPane.setSelectedIndex(0);



		// on surligne l'instruction dans la fenêtre Prog Mem
		if(state!=20)//PCO change avant la fin de l'instruction...
			Rom.highlight(PC0.getData());

	
		if(paint){
			repaint();
		}
		
		// sauvegarde utilisée pour le retour en arrière
		if (state==20 && nouveau) sauvegarde2();
		
		
	}
	///////////////////////////////////////////////////////
	public void reset() {	  	

		etape=0;
		setActive();

		//msg.setText("\u25AA\u25AA\u25AA  RESET  \u25AA\u25AA\u25AA");
		System.out.println("--------RESET----------");


		Add.reset();     Alu.reset();    Clock.reset();
		Inc.reset();     IR.reset();     RegFile.reset();
		Rom.reset();  Ram.reset();     Sign.reset();    
		Shift.reset();	slider.reset();//DLatch.reset();

		PC0.reset();
		PC0.PC0();
		Sign2.reset();

		for (int i = 0; i < Bus16.length; i++)      Bus16[i].reset();
		for (int i = 0; i < Bus16h.length; i++)     Bus16h[i].reset();
		for (int i = 0; i < Busx.length; i++)       Busx[i].reset();
		for (int i = 0; i < Busctl.length; i++)     Busctl[i].reset();
		for (int i = 0; i < Mux.length; i++)        Mux[i].reset();
		CtlSeq.reset();

		init();
		Rom.highlight(PC0.getData());
		repaint();
	}
	///////////////////////////////////////////////////////
	//  init - à executer au démarrage ou apres un reset
	public void init() {
		PC0.setBusy();
		PC0.act();
		PC0.act();	
		//System.out.println("--------START----------");
		
		nbretour=10;
		tabReg=new int[nbretour][12];
		IRsave=new String[nbretour][2];
		tabBus=new int[nbretour][44];
		
		for(int i=0;i<nbretour;i++){
			IRsave[i][0]="0000000000000000";
			IRsave[i][1]="0000000000000000";
			for(int j=0;j<tabReg[i].length;j++)
				tabReg[i][j]=0;
			for(int k=0;k<tabBus[i].length;k++)
				tabBus[i][k]=0;
		}
		retour=0;

		ramTemp = new File[nbretour];
		for(int i=0;i<nbretour;i++){
			ramTemp[i]=new File("ramseq_temp"+i+".txt");	
			ramTemp[i].deleteOnExit(); // permet de supprimer le fichier quand on quitte le programme
		}
		
	}
	///////////////////////////////////////////////////////
	// avance jusqu'a la fin de l instr en cours
	public void step_instr() {
		if (CtlSeq.getState()==0 )
			step (false,true);
		if (CtlSeq.getState()==1){
			step(false,true);
			step(true,true);
		}
		while (CtlSeq.getState()!=1)
			step(true,true);

		repaint();
	}
	
	private void sauvegarde2(){
		int cycle=Clock.getCycle();
		cycle=cycle%tabReg.length;

		tabReg[cycle]= new int[]{RegFile.getReg(0).getData(),RegFile.getReg(1).getData(),RegFile.getReg(2).getData(),RegFile.getReg(3).getData(),RegFile.getReg(4).getData(),RegFile.getReg(5).getData(),RegFile.getReg(6).getData(),RegFile.getReg(7).getData(),
				PC0.getData(),Clock.getCycle(),Clock.getTime(),Clock.getState()};
		tabBus[cycle]=new int[]{b00.readData(),b01.readData(),b02.readData(),b03.readData(),b04.readData(),b05.readData(),b06.readData(),b07.readData(),b08.readData(),b09.readData(),b10.readData(),b11.readData(),b12.readData(),b13.readData(),b14.readData(),
				c00.readData(),c01.readData(),c02.readData(),c03.readData(),c04.readData(),c05.readData(),c06.readData(),
				FUNCalu.readData(),MUXalu1.readData(),MUXalu2.readData(),MUXpc.readData(),MUXrf.readData(),MUXtgt.readData(),WErf.readData(),WEdmem.readData(),PC0Read.readData(),PSEN.readData()};

		IRsave[cycle]=new String[]{IR.getWord(),b01.getWord()};

		if (retour<tabReg.length) retour ++;
		System.out.println("retour s :"+retour);


		Ram.tempsave(ramTemp[cycle]);

	}

	private void restauration2(int cycle_restore,boolean half){

		cycle_restore=cycle_restore%tabReg.length;

		if (half) {cycle_restore=cycle_restore-1;}
		else {cycle_restore=cycle_restore-2;retour=retour-1;}
		System.out.println("retour r :"+retour);
		if (cycle_restore <0) cycle_restore+=tabReg.length;

		RegFile.getReg(0).setData(tabReg[cycle_restore][0]);RegFile.getReg(1).setData(tabReg[cycle_restore][1]);RegFile.getReg(2).setData(tabReg[cycle_restore][2]);RegFile.getReg(3).setData(tabReg[cycle_restore][3]);
		RegFile.getReg(4).setData(tabReg[cycle_restore][4]);RegFile.getReg(5).setData(tabReg[cycle_restore][5]);RegFile.getReg(6).setData(tabReg[cycle_restore][6]);RegFile.getReg(7).setData(tabReg[cycle_restore][7]);
		PC0.setData(tabReg[cycle_restore][8]);
		Clock.setCycle(tabReg[cycle_restore][9]);Clock.setTime(tabReg[cycle_restore][10]);Clock.setState(tabReg[cycle_restore][11]);

		IR.setWord(IRsave[cycle_restore][0]);
		b01.setWord(IRsave[cycle_restore][1]);

		for(int i=0;i<15;i++) Bus16[i].setData(tabBus[cycle_restore][i]);
		for(int i=0;i<7;i++) Busx[i].setData(tabBus[cycle_restore][i+15]);
		for(int i=0;i<10;i++) Busctl[i].setData(tabBus[cycle_restore][i+22]);

		Ram.fillColumn(1,"0");
		Ram.tempload(ramTemp[cycle_restore]);
		RegFile.restoreRegTable();

	}

	///////////////////////////////////////////////////////
	// Choix de couleur
	/*public void changeCouleur(){


		Color laCouleurPre = laCouleur;
		laCouleur = JColorChooser.showDialog(this, "Choose the colour of elements", laCouleur);

		if(laCouleur == null){
			laCouleur = laCouleurPre;
		}

		for (int i = 0; i < Mux.length; i++)       Mux[i].setColorDefault(laCouleur);
		Rom.setColorDefault(laCouleur);
		Ram.setColorDefault(laCouleur);
		Add.setColorDefault(laCouleur);
		Alu.setColorDefault(laCouleur);
		Inc.setColorDefault(laCouleur);
		RegFile.setColorDefault(laCouleur);
		Sign.setColorDefault(laCouleur);
		Shift.setColorDefault(laCouleur);
		Sign2.setColorDefault(laCouleur);
		CtlSeq.setColorDefault(laCouleur);
		repaint();
	}*/

	///////////////////////////////////////////////////////
	// communication fenetre.java <--> dessin.java
	public void setTextField(JTextField msg) {
		this.msg = msg;
		//msg.setText("\u25AA\u25AA\u25AA  START  \u25AA\u25AA\u25AA");
		msg.setEditable(false);
		msg.setBackground(Color.white);
	}
	public void setTabbedPane(JTabbedPane tabbedPane){
		this.tabbedPane=tabbedPane;
	}

	///////////////////////////////////////////////////////
	// menu display
	public void display(int format)  {
		PC0.setFormat(format);
		Ram.setFormat(format);
		RegFile.setFormat(format);
		repaint();
	}
	///////////////////////////////////////////////////////
	// menu fichier
	public void fichier(int action) {
		switch (action) {
		case 0:
			Rom.fileopen();
			tabbedPane.setSelectedIndex(0);
			break;
		case 1:
			Ram.fileopen();
			tabbedPane.setSelectedIndex(1);
			break;
		case 2:
			Rom.filesave();
			break;
		case 3:
			Ram.filesave();
			break;
		}
	}
	///////////////////////////////////////////////////////
	// function utils à la construction des bus
	private int[][] fillMat(int vect[], int l, int c) {
		int mat[][] = new int[l][c];
		for (int i = 0; i < l; i++) {
			for (int j = 0; j < c; j++) {
				mat[i][j] = vect[i * c + j];
			}
		}
		return mat; 
	}
	//////////////////////////////////////////////////////

	public void previousstep(){//-1/2 étape, reset puis ++step jusqu'a l'état précédent
		int etape2=etape-1;
		this.reset();

		for (int i=0;i<etape2-1;++i){
			step(false,false);		
		}
		step(true,false);	
	}
	
	public void previousstep2(){

		setActive();
		int micro=Clock.getState();
		int cy=Clock.getCycle();


		if (cy==0) {
			reset();
			step(true,true);
		}
		else if(cy==1){
			reset();
			//step_instr(true);
			while(Clock.getState()!=micro) step(false,true);
			//step(true,true);

		}
		else {
			if (retour<=1){
				JOptionPane.showMessageDialog(null, "Return to the past is not possible ! \nBack to The Future", "Time Paradox",JOptionPane.ERROR_MESSAGE);
				cy++;
			}
			else{
				while(Clock.getState()!=20) step(false,false); 
				restauration2(cy,false);
				//restauration();
				while(Clock.getState()!=micro) step(false,true);
				//step(true,true);
			}
		}
		//step(true,true);


		//clk.setText(Clock.getText());
		repaint();

	}
	
	public void previoushalfclock(int micro){
		if(Clock.getCycle()==0) reset();
		else{
			System.out.println(retour);
			if (Clock.getCycle()!=1 && retour<1){
				JOptionPane.showMessageDialog(null, "Return to the past is not possible ! \nBack to The Future", "Time Paradox",JOptionPane.ERROR_MESSAGE);
				return;
			}
			while(Clock.getState()!=20) step(false,false);
			restauration2(Clock.getCycle(),true);
		}
		while(Clock.getState()!=micro) step(false,true);
		if(micro==0) step(true,true);
		if(micro==20) retour--;
		//step(true,true);
		repaint();
		//clk.setText(Clock.getText());
		//stageview.setStep(Clock.getState());
	}
	
	public void previoushalfclock(){
		int micro=Clock.getState()-1;
		if (micro!=-1){
			if (micro==0 && Clock.getCycle()!=0) {micro=20;}
			previoushalfclock(micro);}
	}
	
	//////////////////////////////////////////////////////
	public JInternalFrame getRom(){
		return Rom;
	}
	public JInternalFrame getRam(){
		return Ram;
	}
	///////////////////////////////////////////////////////
	public void setActive(){
		//commencer par toutes les activer!
		Add.setActive();
		Ram.setActive();
		MuxRf.setActive();
		Sign2.setActive();
		Sign.setActive();
		Shift.setActive();
		MuxTGT.setActive();
		MuxAlu2.setActive();	
	}


	public void setInactive(){//fonction permettant de griser les chips qui ne servent a rien pour l'instruction
		//suivant l'opérande on change la couleur des chips non utilisées en gris
		String op = Rom.getCase(PC0.getData(),2);//colonne de droite de la Jtable

		setActive();
		//désactiver les chip suivant l'instruction

		if (op.indexOf("addi") != -1) { // il faut d abord mettre addi avant add !!!
			Add.setInactive();
			Shift.setInactive();
			Sign2.setInactive();
			Ram.setInactive();
			MuxRf.setInactive();
		}
		else
			if (op.indexOf("add") != -1 || op.indexOf("nop") != -1) {//! nop = add 0,0,0
				Add.setInactive();
				Sign.setInactive();
				Sign2.setInactive();
				Shift.setInactive();
				Ram.setInactive();

			}
			else
				if (op.indexOf("nand") != -1) {
					Add.setInactive();
					Sign.setInactive();
					Sign2.setInactive();
					Shift.setInactive();
					Ram.setInactive();

				}
				else
					if (op.indexOf("lui") != -1) {

						Add.setInactive();
						Sign2.setInactive();
						Sign.setInactive();
						MuxAlu2.setInactive();
						MuxRf.setInactive();
						Ram.setInactive();
					}
					else
						if (op.indexOf("sw") != -1) {
							Add.setInactive();
							Sign2.setInactive();
							Shift.setInactive();
							MuxTGT.setInactive();

						}
						else
							if (op.indexOf("lw") != -1) {
								Add.setInactive();
								Sign2.setInactive();
								Shift.setInactive();
								MuxRf.setInactive();
							}
							else
								if (op.indexOf("beq") != -1) {
									Sign.setInactive();
									Shift.setInactive();
									Ram.setInactive();
									MuxTGT.setInactive();
								}
								else
									if (op.indexOf("jalr") != -1 || op.indexOf("reset") != -1) {
										Add.setInactive();
										Sign.setInactive();
										Sign2.setInactive();
										Shift.setInactive();
										Ram.setInactive();
										MuxAlu2.setInactive();
										MuxRf.setInactive();
									}	  
	}
	public RegistreBank getRegBank() {
		// TODO Auto-generated method stub
		return RegFile;
	}
	public void run() {
		int startAdress=PC0.getData();
		int i=0; // garde-fou pour eviter les boucles	
		// s'arrete quand rencontre un (nop), un breakpoint, un halt, ou i devient trop grand
		while(/*Integer.decode(Rom.getCase(PC0.getData(),1))!=0 &&*/ !Rom.getCaseB(PC0.getData(),3) && Integer.decode(Rom.getCase(PC0.getData(),1))!=57471  && i<100 || PC0.getData()==startAdress ){	
			step_instr();
			i++;
			startAdress=-1;
		}
		if (Rom.getCaseB(PC0.getData(),3)){ 
			//Rom.setCaseB(false,PC0.getData(),3); // on retire le breakpoint
			startAdress=PC0.getData();
		}
		if(i==100){
			int reponse = JOptionPane.showConfirmDialog(
				    null, "\u25AA  100 instructions have elapsed \n \u25AA Do you want to continue ?", "100 instructions elapsed",
				    JOptionPane.YES_NO_OPTION);
			if (reponse==JOptionPane.YES_OPTION){
				run();
			}
		}
		Rom.highlight(PC0.getData()); // on surligne	
	}









}