package risc16_pipeline;
import java.awt.*;
import java.io.*;

import javax.swing.*;

public class Dessin extends JPanel {
	private JTextField msg,clk;
	private JTabbedPane tabbedPane;
	private StageView stageview;
	private JScrollBar jsb;

	private int yy0=60,yy1=yy0+90,yy2=yy1+260,yy3=yy2+250,yy4=yy3+210,yy5 = yy4+50; //= 970


	private boolean stomp=false,stall=false,stallw=false,stompw=false;

	// ==== composants ====
	private Add Add;
	private Alu Alu;
	private Clock Clock;
	private Ctl Ctl1,Ctl2,Ctl3,Ctl4,Ctl5,Ctl6,Ctl7;
	private Incrementeur Inc1,Inc2;
	private RegistreBank RegFile;
	private MemData Ram;
	private MemProg Rom;
	private SignExt Sign;
	private LeftShift Shift;
	private InstrReg IR;
	private Multiplexeur MuxPC, MuxS2, MuxOp0, MuxAlu1, MuxAlu2, MuxImm, MuxOut;
	private Register PC0,PC1,PC2,PC3;
	private Integer PC4;
	private Register OPE0,OPE1,OPE2,SD,AO,RF4,RF5;
	private Register OP2,RT2,S1,S2,OP3,RT3,RT4,RT5;

	//==== Bus ====
	private Bus   b00,b01,b02,b03;                         // FETCH STAGE
	private Bus   b20,b21,b22,b23,b24,b25;                 // DECODE STAGE
	private Bus   b40,b41,b42,b43,b45,b46,b47,b48,b49,b50; // EXECUTE STAGE
	private Bus   b60,b61,b62,b63,b64;                     // MEMORY STAGE
	private Bus   b80,b81;                                 // WB STAGE
	private Bus   c00,c01,c02,c03,c04,c05 , c06,c07;
	private Bus   c20,c21,c22,c23,c24;
	private Bus   c30,c31,c32,c33;
	private CtlSignal  MUXo,MUXpc,MUXa1,MUXa2,MUXs2,MUXop,MUXim;
	private CtlSignal  WEr,WEm,FUNCalu,Psta,Psto;

	//==== regroupement dans des vecteurs -> paint & reset façilement ====
	private Bus   Bus16[],Bus16h[],Busx[];  // bus de 16bits |  h = hidden  |  x = -de 16bit
	private CtlSignal Busctl[];
	private Multiplexeur Mux[];
	private Register Reg0[],Reg1[],Reg2[],Reg3[],Reg4[],Reg5[];
	private Ctl Ctlx[];




	private int tabReg[][];
	private int tabBus[][];
	private String IRsave[][];
	private boolean stompstall[][];
	private int retour;
	private TimingDiagram cyclinstr;
	private CtlSignal WEr2;
	private File[] ramTemp;
	private boolean alertOnDataForward=true;
	private boolean alertOnStallEvent=true;
	private boolean alertOnStompEvent=true;
	private TimeStageDiagram cyclestage;



	//**************************************************************************************
	//**************************************************************************************
	//**************************************************************************************
	public Dessin(String path) {
		setBackground(new Color(220, 220, 220));

		cyclinstr=new TimingDiagram();
		/////// BUS ///////////////////////////////////////////////////////////
		/////// 16-bits ///////////////////////////////////////////////////////
		int x00[] = {      275,   275,   276,   375,      274,175};
		int y00[] = {      yy0+20,yy1,   yy0+50,yy0+50,   yy0+50,yy0+50};
		b00 = new Bus(fillMat(x00, 3, 2), fillMat(y00, 3, 2));   // 3 vecteurs de dim 2
		int x01[] = {      400,    420,    420,  300,300};
		int y01[] = {      yy0+50, yy0+50, yy0-45,  yy0-45 , yy0-30};
		b01 = new Bus(x01,y01);
		b02 = new Bus(125,yy0+60,125,yy1-15);
		b20 = new Bus(275,yy1+20,275,yy2);
		int x21[] = { 390,390,410,410};
		int y21[] = { yy1+120,yy1+130,yy1+130,yy2-30};
		b21 = new Bus(x21,y21);
		int x22[] = { 490,490,440,440};
		int y22[] = { yy1+120,yy1+130,yy1+130,yy2-30};
		b22 = new Bus(x22,y22);
		int x23[] = { 605,   605, 575,575};
		int y23[] = { yy1+20+210,yy2-18,     yy2-18,yy2};
		b23 = new Bus(x23,y23);
		int x24[] = { 695,695,    725,725 };
		int y24[] = { yy1+20+210,yy2-18,     yy2-18,yy2};
		b24 = new Bus(x24,y24);
		int x40[] = { 275, 275,   276, 350};
		int y40[] = { yy2+20,yy3, yy3-120,yy3-120};
		b40 = new Bus(fillMat(x40, 2, 2),fillMat(y40, 2, 2));
		int x41[] = { 425,   425,0,          426,           590,590      };
		int y41[] = { yy2+20,yy3-80,0,          yy2+120, yy2+120,yy3-100};
		b41 = new Bus(fillMat(x41, 2, 3),fillMat(y41, 2, 3));
		b42 = new Bus(575,yy2+20,575,yy2+80);
		b43 = new Bus(725,yy2+20,725,yy2+80);

		int x45[] = { 375,610,610,             387,387,0};
		int y45[] = { yy3-120,yy3-120,yy3-100, yy3-119,yy3-80,0};
		b45 = new Bus(fillMat(x45, 2, 3),fillMat(y45, 2, 3));
		int x46[] = { 405,405,      10,    10,260,260};
		int y46[] = { yy3-60,yy3-50,yy3-50, yy0-55,yy0-55,yy0-30};
		b46 = new Bus(x46,y46);
		int x47[] = { 607,607,    630,630,              607,475,475,0};
		int y47[] = { yy2+100,yy2+110,yy2+110,yy3-100,  yy2+110,yy2+110,yy3,0};
		b47 = new Bus(fillMat(x47, 2, 4),fillMat(y47, 2, 4));
		int x48[] = { 697, 697,0,0,0,        698,800,800,280,280};
		int y48[] = { yy2+100, yy3-70,0,0,0, yy2+110,yy2+110,yy0-55,yy0-55,yy0-30 };
		b48 = new Bus(fillMat(x48, 2, 5),fillMat(y48, 2, 5));
		b49 = new Bus(650,yy3-20,650,yy3);
		b60 = new Bus(475,yy3+20,475,yy3+50);
		int x61[] = { 650,    650,   0,0,0,     649,   525,   525,   0,0,   651,   740,   740,    635,635,        665,665,0,0,0};
		int y61[] = { yy3+20, yy4-30,0,0,0,     yy3+30,yy3+30,yy3+50,0,0,   yy3+30,yy3+30,yy2+50,yy2+50,yy2+80,  yy2+51,yy2+80,0,0,0};
		b61 = new Bus(fillMat(x61, 4, 5),fillMat(y61, 4, 5));
		int x62[] = {      500,    500,       620,  620};
		int y62[] = {      yy3+150,yy3+160, yy3+160,  yy4-30};
		b62 = new Bus(x62,y62);
		int x80[] = { 637,  637,  0,0,0,    638,   790,   790,650,650,            750,750,615,615,0,                685,685,0,0,0};
		int y80[] = { yy4+20,yy5,0,0,0,     yy4+30,yy4+30,yy1,yy1,yy1+20,   yy4+30,yy2+40,yy2+40,yy2+80,0,    yy2+41,yy2+80,0,0,0};
		b80 = new Bus(fillMat(x80, 4, 5),fillMat(y80, 4, 5));
		int x81[] = { 637,  637,     760,   760,    595,595,          705,705,0,0,0,0};
		int y81[] = { yy5+20,yy5+30, yy5+30, yy2+30,yy2+30,yy2+80,   yy2+31,yy2+80,0,0,0,0};
		b81 = new Bus(fillMat(x81, 2, 6),fillMat(y81, 2, 6));

		// Hidden buses  (end of multiplexer)
		b03 = new Bus(276,yy0-10,276,yy0);      //  MUXpc-PC
		b25 = new Bus(427,yy2-10,427,yy2);      //  MUXop0 - OPE0
		b50 = new Bus(612,yy3-80,612,yy3-70);   //  MuxImm - alu
		b63 = new Bus(636,yy4-10,636,yy4);      //  MUXout - RF4
		b64 = new Bus(275,yy3+20,275,yy3+40);   //  PC3

		Bus16 = new Bus[22];
		Bus16[0]=b00;  Bus16[1]=b01;  Bus16[2]=b02;  Bus16[3]=b81;
		Bus16[4]=b20;  Bus16[5]=b21;  Bus16[6]=b22;  Bus16[7]=b23;  Bus16[8]=b24; Bus16[9]=b80;
		Bus16[10]=b40; Bus16[11]=b41; Bus16[12]=b42; Bus16[13]=b43;
		Bus16[14]=b61; Bus16[15]=b45; Bus16[16]=b46; Bus16[17]=b47; Bus16[18]=b48; Bus16[19]=b49; Bus16[20]=b62;
		Bus16[21]=b60;

		Bus16h = new Bus[5];
		Bus16h[0]=b03;  Bus16h[1]=b25;   Bus16h[2]=b50; Bus16h[3]=b63;  Bus16h[4]=b64;

		/////// BUS ///////////////////////////////////////////////////////////
		/////// x-bits ///////////////////////////////////////////////////////
		//Imm
		int xc00[]={170,170,     490,490,          390,390,0,0};
		int yc00[]={yy1+35,yy1+45,yy1+45,yy1+100,  yy1+45,yy1+100,0,0};
		c00=new Bus(fillMat(xc00,2,4),fillMat(yc00,2,4),2);

		//rC
		int xc01[]={188,188,0,0 ,188,150,150,115};
		int yc01[]={yy1+25,yy1+70,0,0 ,yy1+50,yy1+50,yy1+105,yy1+105};
		c01=new Bus(fillMat(xc01,2,4),fillMat(yc01,2,4),2);
		//rA
		int xc02[]={112,112,0,     112,170,170};
		int yc02[]={yy1+25,yy1+102,0,  yy1+55,yy1+55,yy1+70};
		c02=new Bus(fillMat(xc02,2,3),fillMat(yc02,2,3),2);
		//rB
		int xc03[]={137,137,     137,575,        137,120};
		int yc03[]={yy1+25,yy2,  yy1+150,yy1+150, yy1+115,yy1+115 };
		c03=new Bus(fillMat(xc03,3,2),fillMat(yc03,3,2),2);
		// MUXs2
		int xc04[]={180,180,       575,         190,190,0};
		int yc04[]={yy1+80,yy1+175,  yy1+175,   yy1+175,yy2,0};
		c04=new Bus(fillMat(xc04,2,3),fillMat(yc04,2,3),2);
		// op
		int xc05[]={87,87};
		int yc05[]={yy1+25,yy1+102};
		c05=new Bus(fillMat(xc05,1,2),fillMat(yc05,1,2),2);

		// CTL-7  out
		int xc06[]={87,87,     87,208};
		int yc06[]={yy1+138,yy2,  yy2-38,yy2-38};
		c06=new Bus(fillMat(xc06,2,2),fillMat(yc06,2,2),2);
		int xc07[]={112,112};
		int yc07[]={yy1+138,yy2};
		c07=new Bus(fillMat(xc07,1,2),fillMat(yc07,1,2),2);

		// EQ alu > CTL 3
		int xc20[]={590,500};
		int yc20[]={yy3-50,yy3-50};
		c20=new Bus(fillMat(xc20,1,2),fillMat(yc20,1,2),2);
		//142,yy2+120
		int xc21[]={87,87,0,0,      87,180,0,0,   87,142,0,0,      87,20,20,70};
		int yc21[]={yy2+21,yy3,0,0, yy2+110,yy2+110,0,0,     yy2+160,yy2+160,0,0,      yy2+40,yy2+40,yy1+110,yy1+110};
		c21=new Bus(fillMat(xc21,4,4),fillMat(yc21,4,4),2);
		int xc22[]={112,112,0,0,  112,24,24,70           };
		int yc22[]={yy2+21,yy3,0,0,        yy2+35,yy2+35,yy1+120,yy1+120};
		c22=new Bus(fillMat(xc22,2,4),fillMat(yc22,2,4),2);

		//S1 & S2
		int xc23[]={143,143}; //175//137
		int yc23[]={yy2+25,yy2+50};
		c23=new Bus(xc23,yc23,2);
		int xc24[]={181,181}; //175 //187
		int yc24[]={yy2+25,yy2+50};
		c24=new Bus(xc24,yc24,2);

		int xc30[]={87,87};
		int yc30[]={yy3+21,yy3+70};
		c30=new Bus(fillMat(xc30,1,2),fillMat(yc30,1,2),2);
		int xc31[]={112,112,0,0,0,	112,35,35,135,0};
		int yc31[]={yy3+21,yy4,0,0,0,	yy4-30,yy4-30,yy2+74,yy2+74,0};
		c31=new Bus(fillMat(xc31,2,5),fillMat(yc31,2,5),2);
		int xc32[]={112,112,0,0,0,     112,30, 30, 575,0,            112,30, 30, 135,0,	112,200,0,0,0};
		int yc32[]={yy4+21,yy5,0,0,0,  yy4+30, yy4+30, yy1+200,yy1+200 ,0,   yy4+30, yy4+30, yy2+67,yy2+67 ,0, yy4+30,yy4+30,0,0,0   };
		c32=new Bus(fillMat(xc32,4,5),fillMat(yc32,4,5),2);
		int xc33[]={112,112,0,0,0,	112,25,25,135,0};
		int yc33[]={yy5+21,yy5+40,0,0,0,	yy5+25,yy5+25,yy2+60,yy2+60,0};
		c33=new Bus(fillMat(xc33,2,5),fillMat(yc33,2,5),2);

		Busx = new Bus[17];
		Busx[0]=c00;  Busx[1]=c01;  Busx[2]=c02;  Busx[3]=c03; Busx[4]=c04;
		Busx[5]=c21;  Busx[6]=c22;  Busx[7]=c30;  Busx[8]=c31;  Busx[9]=c32;
		Busx[10]=c33; Busx[11]=c05; Busx[12]=c23; Busx[13]=c24; Busx[14]=c20;
		Busx[15]=c06; Busx[16]=c07;



		/////// BUS //////////////////////////////////////////////////////////////////
		/////// signaux Controle ///////////////////////////////////////////////////////
		//,142,yy2+48,40//,655,yy2+80,
		//,570,yy3-100,80,20,
		int ctlpcx[] = { 145,      20,    20,250};
		int ctlpcy[] = { yy3-70,yy3-70, yy0-25,yy0-25};
		int ctlalux[] = { 175, 175,     590    };
		int ctlaluy[] = { yy3-70, yy3-30,yy3-30 };
		int ctlmuxox[]={100,250,590}; // 130,yy4-25,590,yy4-25
		int ctlmuxoy[]={yy3+100,yy4-25,yy4-25};
		int werx[]={245,515,570}; //450,yy0+130,550,yy0+130
		int wery[]={yy4+25,yy1+220, yy1+220};
		MUXpc = new CtlSignal(ctlpcx,ctlpcy, "MUXpc"); MUXpc.setStringPos(130,yy0-30); 
		MUXo  = new CtlSignal(ctlmuxox,ctlmuxoy, "MUXout"); MUXo.setStringPos(500,yy4-30);
		MUXa1 = new CtlSignal(185,yy2+55,650,yy2+70, "MUXalu1"); MUXa1.setStringPos(420,yy2+60);
		MUXa2 = new CtlSignal(185,yy2+80,550,yy2+90, "MUXalu2"); MUXa2.setStringPos(370,yy2+85);
		MUXs2 = new CtlSignal(210,yy2-60,180,yy1+100, "MUXs2"); MUXs2.setStringPos(205,yy1+140);
		MUXop = new CtlSignal(220,yy2-40,390,yy2-30,"MUXop"); MUXop.setStringPos(320,yy2-35);
		MUXim = new CtlSignal(190,yy2+140,560,yy3-90,"MUXimm"); MUXim.setStringPos(497,yy3-100);

		WEr = new CtlSignal(werx,wery, "WEr"); WEr.setStringPos(260,yy4+30);
		//WEr2 = new CtlSignal(300,yy4+35,400,yy4+35, "WEr (*)"); WEr2.setStringPos(350,yy4+30);
		WEm = new CtlSignal(130,yy3+90,445,yy3+90, "WEm"); WEm.setStringPos(280, yy3+85);
		FUNCalu = new CtlSignal(ctlalux,ctlaluy,"FUNCalu"); FUNCalu.setStringPos(380,yy3-35);
		Psta = new CtlSignal(50,yy1+120,50,yy0+10,"Pstall"); Psta.setStringPos(5,yy1+50);
		Psto = new CtlSignal(55,yy2+200,55,yy1+10,"Pstomp"); Psto.setStringPos(5,yy2+100);

		Busctl = new CtlSignal[12];
		Busctl[0]=MUXo; Busctl[1]=MUXa1; Busctl[2]=MUXa2; Busctl[3]=MUXs2; Busctl[4]=MUXop; Busctl[5]=MUXim;
		Busctl[6]=WEr;  Busctl[7]=WEm;  Busctl[8]=FUNCalu;  Busctl[9]=Psta;  Busctl[10]=Psto;  Busctl[11]=MUXpc;
		//Busctl[12]=WEr2;

		// IMPORTANT de d aboprd definir lmes bus
		//**************************************************************************************
		//**************************************************************************************
		//**************************************************************************************

		Bus[] IRout= new Bus[5];
		IRout[0]=c05; IRout[1]=c02; IRout[2]=c03;   IRout[3]=c01;  IRout[4]=c00;
		String RomColumnNames[] = {"Address", "Content", "ASM","","label"};

		Rom = new MemProg("Risc 16 - Programme", 75, yy0, 100, 60, RomColumnNames,b02,b00);
		Ram = new MemData("Risc 16 - Data", 450, yy3+50, 100, 100,b62, b61,b60);
		Ram.setCtlBus(WEm, false);
		Alu = new Alu(570, yy3-70, 170, 50, b49, c20, b50,b48);
		Inc1 = new Incrementeur(375,yy0+35,25,25, b01,b00);
		Inc2 = new Incrementeur(350,yy3-130,25,25, b45,b40);
		Add = new Add(375, yy3-80, 60, 20, b46,   b45,b41);
		IR = new InstrReg(75,yy1,125,20,b02,IRout);
		RegFile = new RegistreBank(575,yy1+20,150,210,b24,b23,  b80,c03,c04,c32);
		Sign = new SignExt (445,yy1+100,90,20, b22,c00);
		Shift= new LeftShift(345,yy1+100,90,20, b21,c00);
		Clock = new Clock(600, yy0, 120, 20);

		//    private CtlSignal  WEr,WEm,FCal,Psta,Psto;
		RegFile.setCtlBus(WEr);
		Alu.setCtlBus(FUNCalu);
		//Ram.setCtlBus(WEm);

		////////////////////////////////////////////////////////////////////////
		/////// CTL  ///////////////////////////////////////////////////////////
		Bus[] inctl3= new Bus[2];
		inctl3[0]=c21;  inctl3[1]=c20;
		Bus[] inctl5= new Bus[5];
		inctl5[0]=c23;  inctl5[1]=c24;  inctl5[2]=c31;  inctl5[3]=c32;  inctl5[4]=c33;
		Bus[] inctl7= new Bus[6];
		inctl7[0]=c05;  inctl7[1]=c02;  inctl7[2]=c03;  inctl7[3]=c01;  inctl7[4]=c21;  inctl7[5]=c22;
		Bus[] outctl7=new Bus[2];  outctl7[0]=c06;    outctl7[1]=c07;

		/*  Ctl1 = new Ctl(1,200,yy4+15,40);     Ctl1.setInput(c32); // rT4		//*30
                                         Ctl1.setSignal(WEr);
    Ctl2 = new Ctl(2,66,yy3+70,40);     Ctl2.setInput(c30); // OP3
                                         Ctl2.setSignal(WEm,MUXo);
    Ctl3 = new Ctl(3,142,yy2+150,40);    Ctl3.setInput(inctl3); // OP2 EQ!
                                         Ctl3.setSignal(FCal,MUXpc,Psto);
    Ctl4 = new Ctl(4,180,yy2+100,40);    Ctl4.setInput(c21); // OP2
                                         Ctl4.setSignal(MUXim);
    Ctl5 = new Ctl(5,142,yy2+48,40);     Ctl5.setInput(inctl5); // S1,S2,RT3,RT4,RT5
                                         Ctl5.setSignal(MUXa1,MUXa2);
    Ctl6 = new Ctl(6,210,yy2-55,40);     Ctl6.setInput(c06); // OP 1
                                         Ctl6.setSignal(MUXop,MUXs2);
    Ctl7 = new Ctl(7,78,yy1+100,40);     Ctl7.setInput(inctl7); //  OP1 rA  rB rC OP2 rT2
                                         Ctl7.setSignal(Psta);
                                         Ctl7.setOutput(outctl7);
		 */
		Ctl1 = new Ctl1(200,yy4+15,40);     Ctl1.setInput(c32); // rT4		//*30
		Ctl1.setSignal(WEr);
		Ctl2 = new Ctl2(66,yy3+70,40);     Ctl2.setInput(c30); // OP3
		Ctl2.setSignal(WEm,MUXo);
		Ctl3 = new Ctl3(142,yy2+150,40);    Ctl3.setInput(inctl3); // OP2 EQ!
		Ctl3.setSignal(FUNCalu,MUXpc,Psto);
		Ctl4 = new Ctl4(180,yy2+100,40);    Ctl4.setInput(c21); // OP2
		Ctl4.setSignal(MUXim);
		Ctl5 = new Ctl5(142,yy2+48,40);     Ctl5.setInput(inctl5); // S1,S2,RT3,RT4,RT5
		Ctl5.setSignal(MUXa1,MUXa2);
		Ctl6 = new Ctl6(210,yy2-55,40);     Ctl6.setInput(c06); // OP 1
		Ctl6.setSignal(MUXop,MUXs2);
		Ctl7 = new Ctl7(78,yy1+100,40);     Ctl7.setInput(inctl7); //  OP1 rA  rB rC OP2 rT2
		Ctl7.setSignal(Psta);
		Ctl7.setOutput(outctl7);



		Ctlx = new Ctl[7];
		Ctlx[0]=Ctl1; Ctlx[1]=Ctl2; Ctlx[2]=Ctl3; Ctlx[3]=Ctl4; Ctlx[4]=Ctl5; Ctlx[5]=Ctl6; Ctlx[6]=Ctl7;

		//    private CtlSignal  MUXo,MUXa1,MUXa2,MUXs2,MUXop,MUXim; MUXpc
		//    private CtlSignal  WEr,WEm,FCal,Psta,Psto;


		/////// REGISTRER (LATCHES) ///////////////////////////////////////////
		PC0  = new Register("PC0", 225, yy0, 100, 20, b00, b03);
		PC1  = new Register("PC1", 225, yy1, 100, 20, b20, b00);
		PC2  = new Register("PC2", 225, yy2, 100, 20, b40, b20);
		PC3  = new Register("PC3", 225, yy3, 100, 20, b64, b40);
		OPE0 = new Register("OPE0",375, yy2, 100, 20, b41, b25);
		OPE2 = new Register("OPE2",525, yy2, 100, 20, b42,b23);
		OPE1 = new Register("OPE1",675, yy2, 100, 20, b43,b24);
		SD   = new Register("SD",  400, yy3, 100, 20, b60,b47);
		AO   = new Register("AD",  570, yy3, 100, 20, b61,b49);
		RF4  = new Register("RF4", 585, yy4, 100, 20, b80,b63);
		RF5  = new Register("RF5", 585, yy5, 100, 20, b81,b80);

		OP2  = new Register("OP2", 75,  yy2, 25, 20, c21,c06 ,true);
		OP3  = new Register("OP3", 75,  yy3, 25, 20, c30,c21 ,true);
		RT2  = new Register("RT2", 100, yy2, 25, 20, c22,c07);
		RT3  = new Register("RT3", 100, yy3, 25, 20, c31,c22);
		RT4  = new Register("RT4", 100, yy4, 25, 20, c32,c31);
		RT5  = new Register("RT5", 100, yy5, 25, 20, c33,c32);
		S1   = new Register("S1", 125,  yy2, 25, 20, c23,c03);
		S2   = new Register("S2", 175,  yy2, 25, 20, c24,c04);



		Reg0 = new Register[1];
		Reg1 = new Register[1];
		Reg2 = new Register[8];
		Reg3 = new Register[5];
		Reg4 = new Register[2];
		Reg5 = new Register[2];
		Reg0[0]=PC0;
		Reg1[0]=PC1;
		Reg2[0]=OP2; Reg2[1]=RT2; Reg2[2]=S1;  Reg2[3]=S2; Reg2[4]=PC2; Reg2[5]=OPE0; Reg2[6]=OPE1;   Reg2[7]=OPE2;
		Reg3[0]=OP3; Reg3[1]=RT3; Reg3[2]=PC3; Reg3[3]=SD; Reg3[4]=AO;
		Reg4[0]=RT4; Reg4[1]=RF4;
		Reg5[0]=RT5; Reg5[1]=RF5;



		/////// MULTIPLEXER /////////////////////////////////////////////////////
		MuxPC=   new Multiplexeur(3,234,yy0-30, 80,20,10, b03, b46,b48,b01);
		MuxS2 =  new Multiplexeur(2,160,yy1+70, 40,10,0,  c04, c02,c01);
		MuxOp0=  new Multiplexeur(2,395,yy2-30, 60,20,10, b25, b21,b22);
		MuxAlu1= new Multiplexeur(4,655,yy2+80, 80,20,0,  b48, b61,b80,b81,b43);
		MuxAlu2= new Multiplexeur(4,565,yy2+80, 80,20,0,  b47, b42,b81,b80,b61);
		MuxImm=  new Multiplexeur(3,570,yy3-100,80,20,10, b50, b41,b45,b47);
		MuxOut=  new Multiplexeur(2,595,yy4-30, 80,20,10, b63, b62,b61);

		MuxPC.setCtlBus(MUXpc);
		MuxS2.setCtlBus(MUXs2);
		MuxOp0.setCtlBus(MUXop);
		MuxAlu1.setCtlBus(MUXa1);
		MuxAlu2.setCtlBus(MUXa2);
		MuxImm.setCtlBus(MUXim);
		MuxOut.setCtlBus(MUXo);

		Mux = new Multiplexeur[7];
		Mux[0]=MuxPC;   Mux[1]=MuxS2;  Mux[2]=MuxOp0;  Mux[3]=MuxAlu1;
		Mux[4]=MuxAlu2; Mux[5]=MuxImm; Mux[6]=MuxOut;

		////// ------------ /////////////////////////////////////////////////////
		for (int i = 0; i < Reg0.length; i++)       Reg0[i].setCtlStall(Psta);
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].setCtlStall(Psta);
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].setCtlStomp(Psto);
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].setCtlStomp(Psto);
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].setCtlStall(Psta); //pour la bulle
		IR.setCtlStall(Psta);
		IR.setCtlStomp(Psto);

		for (int i = 0; i < Reg0.length; i++)       Reg0[i].setClocked();
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].setClocked();
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].setClocked();
		for (int i = 0; i < Reg3.length; i++)       Reg3[i].setClocked();
		for (int i = 0; i < Reg4.length; i++)       Reg4[i].setClocked();
		for (int i = 0; i < Reg5.length; i++)       Reg5[i].setClocked();
		IR.setClocked();

		////////////////////////////////////////////////////////////////////////
		///// > INITIALISATION < ///////////////////////////////////////////////////////////////////
		Rom.fileopen(path);     // load the default memory content


		init();
	}
	//**************************************************************************************
	//**************************************************************************************
	//**************************************************************************************
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g = (Graphics2D) g;             //permet l'utilisation de API java2D
		g.setFont(new Font("Monospaced", Font.PLAIN, g.getFont().getSize()));

		// ligne séparant les étages, possibilité de colorer tout le fond d'un étage mais flashy
		// limiter la coloration de l'étage quand l'instruction est sélectionnée ?
		g.setColor(Color.yellow);
		g.fillRect(30,yy0+10,800,3);
		g.setColor(Color.cyan);
		g.fillRect(30,yy1+10,800,3);
		g.setColor(Color.green);
		g.fillRect(30,yy2+10,800,3);
		g.setColor(Color.orange);
		g.fillRect(30,yy3+10,800,3);
		g.setColor(Color.magenta);
		g.fillRect(30,yy4+10,800,3);
		g.setColor(Color.gray);
		g.fillRect(30,yy5+10,800,3);

		switch (stageview.getSelect()){
		case 0:
			g.setColor(new Color(255,255,171));
			g.fillRect(30, yy0+13, 800, yy1-yy0-3);
			break;
		case 1:
			g.setColor(new Color(171,255,255));
			g.fillRect(30, yy1+13, 800, yy2-yy1-3);
			break;
		case 2:
			g.setColor(new Color(171,255,171));
			g.fillRect(30, yy2+13, 800, yy3-yy2-3);
			break;
		case 3:
			g.setColor(new Color(255,237,171));
			g.fillRect(30, yy3+13, 800, yy4-yy3-3);
			break;
		case 4:
			g.setColor(new Color(255,171,255));
			g.fillRect(30, yy4+13, 800, yy5-yy4-3);
		}

		g.setColor(Color.black);
		g.drawString("IF / ID",840,yy1+10);
		g.drawString("ID / EX",840,yy2+10);
		g.drawString("EX / MEM",840,yy3+10);
		g.drawString("MEM / WB",840,yy4+10);




		g.setColor(Color.black);
		/////// **** ///////////////////////////////////////////////////////////
		Clock.paint(g);    Rom.dessine(g);   Ram.dessine(g);
		Add.paint(g);      Alu.paint(g);     Inc1.paint(g);    Inc2.paint(g);
		RegFile.paint(g);  IR.paint(g);      Sign.paint(g);    Shift.paint(g);



		///////  CTL --- REGISTRER --- MULTIPLEXER /////////////////////////////////////
		for (int i = 0; i < Ctlx.length; i++)       Ctlx[i].paint(g);
		for (int i = 0; i < Reg0.length; i++)       Reg0[i].paint(g);
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].paint(g);
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].paint(g);
		for (int i = 0; i < Reg3.length; i++)       Reg3[i].paint(g);
		for (int i = 0; i < Reg4.length; i++)       Reg4[i].paint(g);
		for (int i = 0; i < Reg5.length; i++)       Reg5[i].paint(g);
		for (int i = 0; i < Mux.length; i++)        Mux[i].paint(g);
		/////// BUS ///////////////////////////////////////////////////////
		for (int i = 0; i < Bus16.length; i++)     Bus16[i].paint( (Graphics2D) g);
		for (int i = 0; i < Bus16h.length; i++)    Bus16h[i].paint( (Graphics2D) g);
		for (int i = 0; i < Busx.length; i++)      Busx[i].paint( (Graphics2D) g);
		for (int i = 0; i < Busctl.length; i++)    Busctl[i].paint( (Graphics2D) g);
		g.setColor(Color.black);
		g.drawString("SRC1",540,yy1+145);
		g.drawString("SRC2",540,yy1+170);
		g.drawString("TGT",540,yy1+195);
	}
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	public void step(boolean show,boolean nouveau){

		String temp=new String("");


		Add.act(); // avant inc
		Inc1.act();	// avant L()
		Inc2.act();	// avant L()
		Sign.act(); // avant L()
		Shift.act(); // avant L()
		Rom.act(); // avant L()
		RegFile.act(); // avant L()
		Ram.act();  // avant L()



		Clock.inc();
		stageview.setStep(Clock.getState());


		boolean  lvl=Clock.getLevel();

		int clkState=Clock.getState(); 

		if (clkState==13)  {	//L tous les 14 coups à 1 ou 14
			temp=" -- Clocked Register Sending Data -- ";
			if (!stallw) {
				for (int i = 0; i < Reg0.length; i++)       Reg0[i].L();
				for (int i = 0; i < Reg1.length; i++)       Reg1[i].L();
				IR.L();
			}
			else {
				if (alertOnStallEvent && nouveau) JOptionPane.showMessageDialog(null, "\u25AA \u25AA \u25AA STALL EVENT has occured during last cycle \n \u25AA On a pipeline STALL, the instructions in the FETCH and DECODE stages are held up \n and a NOP instruction is placed in the ID/EX register to fill the created hole", "Data Hazard",JOptionPane.INFORMATION_MESSAGE);
				stallw=false;
				IR.resetStall();
				for (int i = 0; i < Reg0.length; i++)       Reg0[i].resetStall();
				for (int i = 0; i < Reg1.length; i++)       Reg1[i].resetStall();

			}
			if(stompw ){
				if (alertOnStompEvent && nouveau) JOptionPane.showMessageDialog(null, "\u25AA \u25AA \u25AA STOMP EVENT has occured during last cycle \n\u25AA The contents of the IF/ID and ID/EX registers are overwritten with NOP instructions", "Control Hazard",JOptionPane.INFORMATION_MESSAGE);
				stompw=false;
			}

			for (int i = 0; i < Reg2.length; i++)       Reg2[i].L();
			for (int i = 0; i < Reg3.length; i++)       Reg3[i].L();
			for (int i = 0; i < Reg4.length; i++)       Reg4[i].L();
			for (int i = 0; i < Reg5.length; i++)       Reg5[i].L();


			int cy=Clock.getCycle();
			if (cy==0) cy=1;
			else if (cy%tabReg.length==0) cy=10;
			else cy=cy%tabReg.length;

			PC4=tabReg[cy-1][11]; 

		} 



		IR.act(lvl);
		for (int i = 0; i < Reg0.length; i++)       {Reg0[i].act(lvl);}
		for (int i = 0; i < Reg1.length; i++)       {Reg1[i].act(lvl);}
		for (int i = 0; i < Reg2.length; i++)       {Reg2[i].act(lvl);}
		for (int i = 0; i < Reg3.length; i++)       {Reg3[i].act(lvl);}
		for (int i = 0; i < Reg4.length; i++)       {Reg4[i].act(lvl);}
		for (int i = 0; i < Reg5.length; i++)       {Reg5[i].act(lvl);}



		Alu.act(lvl); // avant MUX
		for (int i = 0; i < Ctlx.length; i++)      Ctlx[i].act(lvl,clkState);

		for (int i = 0; i < Mux.length; i++)       Mux[i].act(lvl);






		stall=(Psta.getData()==1);
		stomp=(Psto.getData()==1);
		if (stall && nouveau) {
			stallw=true;
			temp = "  \t\u25AA "+"  STALL EVENT has occured"+"  \t\u25AA ";
			if(nouveau){
				cyclinstr.setEvent("STALL");
				cyclestage.setEvent("STALL");
			}
		}
		if (stomp && nouveau) {
			stompw=true;
			temp = "  \t\u25AA "+"  STOMP EVENT "+"  \t\u25AA ";
			if(nouveau){
				cyclinstr.setEvent("STOMP");
				cyclestage.setEvent("STOMP");
			}
		}

		msg.setText(temp);
		clk.setText(Clock.getText());
		//----------------------------------------------------------

		// mis à jour du tableau résumant le pipeline sur la droite
		if (PC4==0 && RT4.getData()==0) stageview.setText(4, PC4, null);
		else stageview.setText(4, PC4, Rom.getCase(PC4,2));
		if (PC3.getData()==0 && OP3.getData()==0 && RT3.getData()==0) stageview.setText(3, PC3.getData(), null);
		else stageview.setText(3, PC3.getData(), Rom.getCase(PC3.getData(),2));
		if (PC2.getData()==0 && OP2.getData()==0 && RT2.getData()==0 && S1.getData()==0 && S2.getData()==0) stageview.setText(2, PC2.getData(), null);
		else stageview.setText(2, PC2.getData(), Rom.getCase(PC2.getData(),2));
		if (PC1.getData()==0 && IR.getOpcode()=="NOP") stageview.setText(1, PC1.getData(), null);
		else stageview.setText(1, PC1.getData(), Rom.getCase(PC1.getData(),2));
		stageview.setText(0,PC0.getData(), Rom.getCase(PC0.getData(),2));

		// mis à jour du diagramme temps-instruction
		if (Clock.getState()==1 && nouveau){
			if(Clock.getCycle()+1>cyclinstr.getXcycle()){
			cyclinstr.addElem2(Clock.getCycle()+1,0, PC0.getData(), Rom.getCase(PC0.getData(),2));
			if(Clock.getCycle()>0) cyclinstr.addElem2(Clock.getCycle()+1,1, PC1.getData(), Rom.getCase(PC1.getData(),2));
			if(Clock.getCycle()>1) cyclinstr.addElem2(Clock.getCycle()+1,2, PC2.getData(), Rom.getCase(PC2.getData(),2));
			if(Clock.getCycle()>2) cyclinstr.addElem2(Clock.getCycle()+1,3, PC3.getData(), Rom.getCase(PC3.getData(),2));
			if(Clock.getCycle()>3) cyclinstr.addElem2(Clock.getCycle()+1,4, PC4, Rom.getCase(PC4,2));
			}

			cyclestage.addElem2(Clock.getCycle()+1,0, PC0.getData(), Rom.getCase(PC0.getData(),2));
			if(Clock.getCycle()>0) cyclestage.addElem2(Clock.getCycle()+1,1, PC1.getData(), Rom.getCase(PC1.getData(),2));
			if(Clock.getCycle()>1) cyclestage.addElem2(Clock.getCycle()+1,2, PC2.getData(), Rom.getCase(PC2.getData(),2));
			if(Clock.getCycle()>2) cyclestage.addElem2(Clock.getCycle()+1,3, PC3.getData(), Rom.getCase(PC3.getData(),2));
			if(Clock.getCycle()>3) cyclestage.addElem2(Clock.getCycle()+1,4, PC4, Rom.getCase(PC4,2));
		}

		// highlight dans la ROM
		Rom.highlightSet(PC4);
		if (PC4==0 && RT4.getData()==0) Rom.highlightRem(); 
		Rom.highlightAdd(PC3.getData());
		if (PC3.getData()==0 && OP3.getData()==0 && RT3.getData()==0) Rom.highlightRem();     
		Rom.highlightAdd(PC2.getData()); // ID/EX
		if (PC2.getData()==0 && OP2.getData()==0 && RT2.getData()==0 && S1.getData()==0 && S2.getData()==0) Rom.highlightRem();	    
		Rom.highlightAdd(PC1.getData()); // IF/ID
		if (PC1.getData()==0 && IR.getOpcode()=="NOP") Rom.highlightRem();	    
		Rom.highlightAdd(PC0.getData());

		// highlight dans la RAM lors d'une écriture/lecture
		if ((OP3.getData()==4 || OP3.getData()==5) && (clkState==4 || clkState==5)) {
			tabbedPane.setSelectedIndex(1);
			Ram.highlight(b61.readData());
		}
		else
			tabbedPane.setSelectedIndex(0);

		// on met à jour le dessin du flow diagram
		if (show) {
			repaint();
			if (stageview.getSelect()==0 || stageview.getSelect()==1) scroll(0);
			else if(stageview.getSelect()==2) scroll(0.2);
			else if(stageview.getSelect()>=3) scroll(0.4);
		}


		if (clkState==14 && nouveau) sauvegarde2(); // sauvegarde temporaire utilisée pour le retour en arrière
		if (clkState==14) setInactive();	// on grise les blocs qui seront inutilisés pour la prochaine instruction

	}



	public void reset() {
		msg.setText("\u25AA\u25AA\u25AA  RESET  \u25AA\u25AA\u25AA");


		stomp=false;
		stall=false;

		Clock.reset();
		Add.reset();     Alu.reset();
		Inc1.reset();    Inc2.reset();
		RegFile.reset();
		Ram.reset();      Rom.reset();
		Sign.reset();     Shift.reset();     IR.reset();

		stageview.reset();
		cyclinstr.reset();
		cyclestage.reset();

		for (int i = 0; i < Bus16.length; i++)      Bus16[i].reset();
		for (int i = 0; i < Bus16h.length; i++)     Bus16h[i].reset();
		for (int i = 0; i < Busx.length; i++)       Busx[i].reset();
		for (int i = 0; i < Busctl.length; i++)     Busctl[i].reset();
		for (int i = 0; i < Mux.length; i++)        Mux[i].reset();
		for (int i = 0; i < Ctlx.length; i++)       Ctlx[i].reset(); // envoie signaux aux mux & bus
		for (int i = 0; i < Reg0.length; i++)       Reg0[i].reset();
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].reset();
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].reset();
		for (int i = 0; i < Reg3.length; i++)       Reg3[i].reset();
		for (int i = 0; i < Reg4.length; i++)       Reg4[i].reset();
		for (int i = 0; i < Reg5.length; i++)       Reg5[i].reset();



		init();
		scroll(0);
		clk.setText(Clock.getText());
		repaint();
	}
	///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////

	///////////////////////////////////////////////////////
	//  init - à executer au démarrage ou apres un reset
	private void init()  {
		//system.out.println("--------START----------");

		tabReg=new int[10][31];
		IRsave=new String[10][2];
		tabBus=new int[10][44];
		stompstall=new boolean[10][2];
		for(int i=0;i<tabReg.length;i++){
			IRsave[i][0]="0000000000000000";
			IRsave[i][1]="0000000000000000";
			stompstall[i][0]=false;
			stompstall[i][1]=false;
			for(int j=0;j<tabReg[i].length;j++)
				tabReg[i][j]=0;
			for(int k=0;k<tabBus[i].length;k++)
				tabBus[i][k]=0;
		}
		retour=0;
		PC4=0;

		ramTemp = new File[10];
		for(int i=0;i<tabReg.length;i++){
			ramTemp[i]=new File("rampipe_temp"+i+".txt");	
			ramTemp[i].deleteOnExit(); // permet de supprimer le fichier quand on quitte le programme
		}
		//sauvegarde();



		//temp=" -- Clocked Register Sending Data -- ";
		for (int i = 0; i < Reg0.length; i++)       Reg0[i].L();
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].L();
		IR.L();
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].L();
		for (int i = 0; i < Reg3.length; i++)       Reg3[i].L();
		for (int i = 0; i < Reg4.length; i++)       Reg4[i].L();
		for (int i = 0; i < Reg5.length; i++)       Reg5[i].L();

		boolean lvl=true;		
		IR.act(lvl);
		for (int i = 0; i < Reg0.length; i++)       {Reg0[i].act(lvl);}
		for (int i = 0; i < Reg1.length; i++)       {Reg1[i].act(lvl);}
		for (int i = 0; i < Reg2.length; i++)       {Reg2[i].act(lvl);}
		for (int i = 0; i < Reg3.length; i++)       {Reg3[i].act(lvl);}
		for (int i = 0; i < Reg4.length; i++)       {Reg4[i].act(lvl);}
		for (int i = 0; i < Reg5.length; i++)       {Reg5[i].act(lvl);}
		lvl=false;
		IR.act(lvl);
		for (int i = 0; i < Reg0.length; i++)       Reg0[i].act(lvl);
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].act(lvl);
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].act(lvl);
		for (int i = 0; i < Reg3.length; i++)       Reg3[i].act(lvl);
		for (int i = 0; i < Reg4.length; i++)       Reg4[i].act(lvl);
		for (int i = 0; i < Reg5.length; i++)       Reg5[i].act(lvl);

	}
	///////////////////////////////////////////////////////
	// execute 1 coup d horlge = 2* step
	private void cycle() {
		step(false,true);
		step(true,true);
	}
	///////////////////////////////////////////////////////

	public void step_instr(boolean ret) {
		int micro=Clock.getState();
		if (Clock.getState()==1)
			cycle();
		while (Clock.getState()!=1)
			step(false,true);
		if(!ret && micro!=0 && micro!=1) 	
			do  
				step(false,true);
			while(Clock.getState()!=micro);
		repaint();
	}
	public void previousstep(){

		setActive();
		int micro=Clock.getState();
		int cy=Clock.getCycle();


		if (cy==0) {
			reset();
		}
		else if(cy==1){
			reset();
			//step_instr(true);
			while(Clock.getState()!=micro) step(false,true);

		}
		else {
			if (retour<=1){
				JOptionPane.showMessageDialog(null, "Return to the past is not possible ! \nBack to The Future", "Time Paradox",JOptionPane.ERROR_MESSAGE);
				cy++;
			}
			else{
				while(Clock.getState()!=14) step(false,false); 
				restauration2(cy,false);
				stageview.decrStage();
				while(Clock.getState()!=micro) step(false,false);
			}
		}
		//step(true,true);


		clk.setText(Clock.getText());
		cyclinstr.previous(cy);
		cyclestage.previous(cy);

		stageview.decrStage();
		stageview.setStep(Clock.getState());
		repaint();

	}
	public void previoushalfclock(int micro){
		if(Clock.getCycle()==0) reset();
		else{
			while(Clock.getState()!=14) step(false,false);
			stageview.decrStage();
			restauration2(Clock.getCycle(),true);
		}
		while(Clock.getState()!=micro) step(false,false);
		if(micro==14) retour--;
		repaint();
		clk.setText(Clock.getText());
		//stageview.setStep(Clock.getState());
	}
	public void previoushalfclock(){
		int micro=Clock.getState()-1;
		if (micro!=-1){
			if (micro==0 && Clock.getCycle()!=0) micro=14;
			previoushalfclock(micro);}
	}
	///////////////////////////////////////////////////////
	// communication fenetre.java <--> dessin.java
	public void setTextField(JTextField clk, JTextField msg) {
		this.msg = msg;
		this.clk = clk;
		msg.setText("\u25AA\u25AA\u25AA  START  \u25AA\u25AA\u25AA");
	}

	public void setTabbedPane(JTabbedPane tabbedPane){
		this.tabbedPane=tabbedPane;
	}
	public void setStageView(StageView stageview){
		this.stageview=stageview;

	}
	public void setScrollBar(JScrollBar jsb){
		this.jsb=jsb;

	}
	public void scroll(double position){

		jsb.setValue((int) (jsb.getMinimum()+(jsb.getMaximum()-jsb.getMinimum())*position));

	}
	public Memoire getRom(){
		return Rom;
	}
	public Memoire getRam(){
		return Ram;
	}
	public RegistreBank getRegBank(){
		return RegFile;
	}


	///////////////////////////////////////////////////////
	// menu display
	public void display(int format)  {
		RegFile.setFormat(format);
		Ram.setFormat(format);
		for (int i = 0; i < Reg0.length; i++)       Reg0[i].setFormat(format,true);
		for (int i = 0; i < Reg1.length; i++)       Reg1[i].setFormat(format,true);
		for (int i = 0; i < Reg2.length; i++)       Reg2[i].setFormat(format,false);
		for (int i = 0; i < Reg3.length; i++)       Reg3[i].setFormat(format,true);
		for (int i = 0; i < Reg4.length; i++)       Reg4[i].setFormat(format,true);
		for (int i = 0; i < Reg5.length; i++)       Reg5[i].setFormat(format,true);

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
		}}
	///////////////////////////////////////////////////////
	// function utils à la construction des bus
	private int[][] fillMat(int vect[], int l, int c) {
		int mat[][] = new int[l][c];
		for (int i = 0; i < l; i++) {
			for (int j = 0; j < c; j++) {
				mat[i][j] = vect[i * c + j];
			}
		}
		return mat; }
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void setActive(){
		//commencer par toutes les activer!
		Add.setActive();
		//Ram.setActive();
		Sign.setActive();
		Shift.setActive();
		MuxAlu1.setActive();
		Inc2.setActive();
		MuxS2.setActive();
		MuxOp0.setActive();
	}


	private void setInactive(){//fonction permettant de griser les chips qui ne servent a rien pour l'instruction
		//suivant l'opérande on change la couleur des chips non utilisées en gris
		//String op = Rom.getCase(PC0.getData(),2);//colonne de droite de la Jtable

		setActive();
		//désactiver les chip suivant l'instruction
		if (OP2.getData()<6){
			Inc2.setInactive();
			if (OP2.getData()!=7)Add.setInactive();
		}
		if(IR.getOp()==0 || IR.getOp()==2 || IR.getOp()==3 || IR.getOp()==7){ 
			Sign.setInactive();
			if (IR.getOp()!=3) MuxOp0.setInactive();
		}
		if (IR.getOp() !=3) 
			Shift.setInactive();
		if(IR.getOp()%2==1) MuxS2.setInactive();
		if(OP2.getData()==3 || OP2.getData()==7) MuxAlu1.setInactive();

		
	}




	private void sauvegarde2(){
		int cycle=Clock.getCycle();
		cycle=cycle%tabReg.length;

		tabReg[cycle]= new int[]{RegFile.getReg(0).getData(),RegFile.getReg(1).getData(),RegFile.getReg(2).getData(),RegFile.getReg(3).getData(),RegFile.getReg(4).getData(),RegFile.getReg(5).getData(),RegFile.getReg(6).getData(),RegFile.getReg(7).getData(),
				PC0.getData(),PC1.getData(),PC2.getData(),PC3.getData(),PC4,OPE0.getData(),OPE1.getData(),OPE2.getData(),
				SD.getData(),AO.getData(),RF4.getData(),RF5.getData(),OP2.getData(),RT2.getData(),S1.getData(),S2.getData(),
				OP3.getData(),RT3.getData(),RT4.getData(),RT5.getData(),Clock.getCycle(),Clock.getTime(),Clock.getState()};

		tabBus[cycle]=new int[]{b00.readData(),b01.readData(),b02.readData(),b81.readData(),b20.readData(),b21.readData(),b22.readData(),b23.readData(),b24.readData(),b80.readData(),b40.readData(),b41.readData(),b42.readData(),b43.readData(),b61.readData(),b45.readData(),b46.readData(),b47.readData(),b48.readData(),b49.readData(),b62.readData(),b60.readData(),b03.readData(),b25.readData(),b50.readData(),b63.readData(),b64.readData(),
				c00.readData(),c01.readData(),c02.readData(),c03.readData(),c04.readData(),c21.readData(),c22.readData(),c30.readData(),c31.readData(),c32.readData(),c33.readData(),c05.readData(),c23.readData(),c24.readData(),c20.readData(),c06.readData(),c07.readData(),
				MUXo.readData(),MUXa1.readData(),MUXa2.readData(),MUXs2.readData(), MUXop.readData(),MUXim.readData(),
				WEr.readData(),  WEm.readData(),  FUNCalu.readData(),  Psta.readData(),Psto.readData(),  MUXpc.readData()};

		IRsave[cycle]=new String[]{IR.getWord(),b02.getWord()};
		stompstall[cycle]=new boolean[]{stompw,stallw};

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
		PC0.setData(tabReg[cycle_restore][8]);PC1.setData(tabReg[cycle_restore][9]);PC2.setData(tabReg[cycle_restore][10]);PC3.setData(tabReg[cycle_restore][11]);PC4=tabReg[cycle_restore][12];
		OPE0.setData(tabReg[cycle_restore][13]);OPE1.setData(tabReg[cycle_restore][14]);OPE2.setData(tabReg[cycle_restore][15]);
		SD.setData(tabReg[cycle_restore][16]);AO.setData(tabReg[cycle_restore][17]);RF4.setData(tabReg[cycle_restore][18]);RF5.setData(tabReg[cycle_restore][19]);
		OP2.setData(tabReg[cycle_restore][20]);RT2.setData(tabReg[cycle_restore][21]);S1.setData(tabReg[cycle_restore][22]);S2.setData(tabReg[cycle_restore][23]);
		OP3.setData(tabReg[cycle_restore][24]);RT3.setData(tabReg[cycle_restore][25]);RT4.setData(tabReg[cycle_restore][26]);RT5.setData(tabReg[cycle_restore][27]);

		Clock.setCycle(tabReg[cycle_restore][28]);Clock.setTime(tabReg[cycle_restore][29]);Clock.setState(tabReg[cycle_restore][30]);

		stompw=stompstall[cycle_restore][0];
		stallw=stompstall[cycle_restore][1];
		IR.setWord(IRsave[cycle_restore][0]);
		b02.setWord(IRsave[cycle_restore][1]);

		for(int i=0;i<22;i++) Bus16[i].setData(tabBus[cycle_restore][i]);
		for(int i=0;i<5;i++)	Bus16h[i].setData(tabBus[cycle_restore][i+22]);
		for(int i=0;i<17;i++) Busx[i].setData(tabBus[cycle_restore][i+27]);
		for(int i=0;i<12;i++) Busctl[i].setData(tabBus[cycle_restore][i+44]);

		Ram.fillColumn(1,"0");
		Ram.tempload(ramTemp[cycle_restore]);
		RegFile.restoreRegTable();

	}



	public void setTimingDiagram(TimingDiagram cyclinstr) {
		this.cyclinstr=cyclinstr;
	}
	public void run() {
		int i=0; // garde-fou pour eviter les boucles	
		int startAdress=PC0.getData();
		// s'arrete quand rencontre un (nop), un breakpoint, un halt, ou i devient trop grand
		while(/*Integer.decode(Rom.getCase(PC3.getData(),1))!=0  &&*/ Integer.decode(Rom.getCase(PC4,1))!=57471 && !Rom.getCaseB(PC0.getData(),3) && i<100 || PC0.getData()==startAdress){	
			step_instr(false);
			i++;
			startAdress=-1;
		}
		if (Rom.getCaseB(PC0.getData(),3)) {
			// Rom.setCaseB(false,PC0.getData(),3); // on retire le breakpoint
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

		//highlight ROM
		Rom.highlightSet(PC4);
		if (PC4==0 && RT4.getData()==0) Rom.highlightRem(); 
		Rom.highlightAdd(PC3.getData());
		if (PC3.getData()==0 && OP3.getData()==0 && RT3.getData()==0) Rom.highlightRem();     
		Rom.highlightAdd(PC2.getData()); // ID/EX
		if (PC2.getData()==0 && OP2.getData()==0 && RT2.getData()==0 && S1.getData()==0 && S2.getData()==0) Rom.highlightRem();	    
		Rom.highlightAdd(PC1.getData()); // IF/ID
		if (PC1.getData()==0 && IR.getOpcode()=="NOP") Rom.highlightRem();	    
		Rom.highlightAdd(PC0.getData());

	}
	public void alertActivation(boolean dataForwardSelected, boolean stallEventSelected,
			boolean stompEventSelected) {
		Ctl.alertActivation(dataForwardSelected);
		alertOnStallEvent=stallEventSelected;
		alertOnStompEvent=stompEventSelected;

	}
	public void setTimeStageDiagram(TimeStageDiagram cyclestage) {
		this.cyclestage=cyclestage;

	}


}

