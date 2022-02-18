/*


/// nimporte quoi > adresse tjs pos >si on a un WE mais que adresse négative -)-> on perd le WE !!

*/
package seq_final;
import java.awt.*;

import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.table.TableColumn;



public abstract class Memoire extends JInternalFrame {
  protected JTable table;
  private MemTable model;
  private JButton assemblage;
  private JButton resetMem;


  private int x, y, lg, ht;
  private Color color=Color.gray;
  private Color colorIdle=Color.gray,colorBusy=Color.green, colorLatch=Color.orange, colorInactive=Color.gray;;

  protected boolean isActive=true;//true=> l'instruction uilise cette chip
  private int state=0;
  private Bus address,input,output;
  private int busidadd,busidin;
  private int delay=0;//compte le nbr de fois qu'on effectue act sur la ROM (on lit une donnée en 3microcycles)

  private int addressValue=-1,data=0; // on stocke ici l adresse courante & data
  protected int addressMax= 2048;

  private CtlSignal ctl;
  private boolean ROM=true, WE=false, hasData=false;
private Fich fi;

//================================================================================================
//   INITIALISATION
//================================================================================================


////////////////////////////////////
  public Memoire(String title, int x, int y, int lg, int ht, Color color,Bus output) {

    // ex: DATA MEMORY

	super(title,
	          true, //resizable
	          false, //closable
	          false, //maximizable
	          false);//iconifiable

    this.x = x;
    this.y = y;
    this.lg = lg;
    this.ht = ht;
    this.color = color;
    this.colorIdle = color;
    this.output = output;


    model = new MemTable(addressMax,false);
    table = new JTable(model);
    table.getColumnModel().getColumn(0).setPreferredWidth(6);
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    table.getColumnModel().getColumn(0).setMaxWidth(50);
    table.getColumnModel().getColumn(0).setMinWidth(50);
    table.getColumnModel().getColumn(0).setWidth(50);
    table.getColumnModel().getColumn(0).setResizable(false);
    table.getTableHeader().setReorderingAllowed(false);
    Container c = getContentPane();
    JPanel t =new JPanel(new GridLayout(0,1));
    c.add(new JScrollPane(table), "Center");
    resetMem = new JButton("Clear");
    t.add(resetMem);
    c.add(t,"South");

    setVisible(true);


    fi = new Fich();

  }

///////////////////////////////
  public Memoire(String title, int x, int y, int lg, int ht, Color color,String[] columnNames, Bus output) {//ROM
	 super(title,
	          true, //resizable
	          false, //closable
	          false, //maximizable
	          false);//iconifiable

	 this.x = x;
    this.y = y;
    this.lg = lg;
    this.ht = ht;
    this.color = color;
    this.colorIdle = color;

    model = new MemTable(addressMax,columnNames);
    table = new JTable(model) {

        //Implement table cell tool tips.
        public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            int realColumnIndex = convertColumnIndexToModel(colIndex);

            if (realColumnIndex == 0) {
				tip =""+  table.getModel().getValueAt(rowIndex,4) ;
			}
			else if (realColumnIndex==1){
				tip =Integer.toBinaryString(Integer.decode((String) table.getModel().getValueAt(rowIndex,1))) ;
				while (tip.length()<16){
					tip="0"+tip;
				}
			}
			return tip;
        }};

        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(4));
    /***/

    TableColumn colBut = table.getColumnModel().getColumn(0);
    colBut.setWidth(40);
    colBut.setResizable(true);
    colBut.setMaxWidth(50);
    colBut.setMinWidth(18);

    table.getColumnModel().getColumn(1).setWidth(50);
    table.getColumnModel().getColumn(1).setMaxWidth(200);
    table.getColumnModel().getColumn(1).setMinWidth(18);
    table.getColumnModel().getColumn(2).setWidth(80);
    table.getColumnModel().getColumn(2).setMaxWidth(400);
    table.getColumnModel().getColumn(2).setMinWidth(80);
    table.getColumnModel().getColumn(3).setMaxWidth(20);
    table.getColumnModel().getColumn(3).setResizable(false);
    table.getTableHeader().setReorderingAllowed(false);
    /***/

    Container c = getContentPane();
    JPanel t =new JPanel(new GridLayout(0,2));
    c.add(new JScrollPane(table), "Center");
    assemblage = new JButton("Assembly");
    t.add(assemblage);
    resetMem = new JButton("Clear");
    t.add(resetMem);
    c.add(t,"South");


    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    this.output = output;
    setVisible(true);

    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.setColumnSelectionAllowed(false);
    table.setRowSelectionAllowed(true);


    fi = new Fich();


  }


///////////////////////////////////////////////////////////////////////
  public void setBusAddr(Bus bus) {
    this.address=bus;
    busidadd=bus.getId();
    //system.out.println("MEMOIRE----busidadd="+busidadd);
    }//busidadd=2 pour la ROM car le bus va dans +1 aussi
/////////////////////////////////////////////////////////////////////
  public void setBusIn(Bus bus) {//juste pour la RAM
    this.input=bus;
    busidin=bus.getId();
    //system.out.println("MEMOIRE----busidin="+busidadd);
    }//busidin=1 et busidadd = 1 pour la RAM
/////////////////////////////////////////////////////////////////////
  public void setCtlBus(CtlSignal ctl,boolean ROM){this.ctl=ctl; ctl.getId(); this.ROM=ROM;} //init bus output (1 seule sortie > ok)
//================================================================================================
//   MEMOIRE
//================================================================================================

  public JButton getJButtonAss() {    return assemblage;  }
  public JButton getJButtonRM() { return resetMem; }
//////////////////////////////////////////////////////////////////
  public String getCase(int l) {     return getCase(l,1);  }
  //////////////////////////////////////////////////////////////////
  public int getAddressMax() {     return addressMax;  }
//////////////////////////////////////////////////////////////////
  public boolean getCaseB(int l,int c){return Boolean.TRUE.equals(table.getValueAt(l, c));
}
  public void setCaseB(boolean check,int l,int c){
	  table.setValueAt(check,l, c);
  }

  /**
   * Get something
   *
   * @param l Line, begins at 0
   * @param c Column, begins at 0
   * @return Something else
   */
	public String getCase(int l, int c) {

		//memory.setRowSelectionInterval(l, l);
		model.fireTableDataChanged();

		if(!ROM){

			if(table.getValueAt(l, c) == null){
				return null;
			}else{
				try{
					int entier = Integer.decode( (String) table.getValueAt(l, c));
					return (String) table.getValueAt(l, c);
				}
				catch(NumberFormatException e){
					warning("The information is not a decimal or hexadecimal number",l);
					table.setValueAt("0",l,c);
					return "0";
				}
			}

		}else{//ROM
			if (table.getValueAt(l, c)!=null)
				return ( (String) table.getValueAt(l, c));
			else
				return null;
		}
	}
//////////////////////////////////////////////////////////////////
  public void setCase(int l, int valeur) {
    table.setValueAt(Integer.toString(valeur), l, 1);
  }
//////////////////////////////////////////////////////////////////

  /**
   * Set the value of the specified cell to 'code'.
   *
   * <br />
   * Note: I guess "case" is the French version of "cell". It should probably be "setCell()".
   * @param code
   * @param l line
   * @param c column
   */
  public void setCase(String code, int l, int c) {
	  if(!ROM){
		  table.setValueAt(code, l, c);
	  }else{
		    table.setValueAt(code, l, c);
	  }

  }
//////////////////////[*]
  public void emptyColumn(int col) {
    model.emptyCol(col);
  }
 //////////////////////[*]
  public void fillColumn(int col,String str) {
    for (int i=0; i<addressMax ; i++)
          setCase(str, i, col);
  }
///////////////////////////////////////////////////////////////////////


//================================================================================================
//   CHIP
//================================================================================================
public int   getCurrentAddr() {  return addressValue; }
//public void setCurrentAdd(int add) {    this.addressValue = add;  }
public Bus getOutput() {    return output;  }


public void reset(){setIdle(); WE=false; hasData=false; addressValue=-1; delay=0;table.clearSelection();}


public boolean isAddrActive()  { return  address.isActive(busidadd);}
public boolean isInActive(){ return  input.isActive(busidin);}
/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////

public void setIdle(){
	if(isActive){
		color=colorIdle;
	}
	state=0;
}
public void setBusy(){
	if(isActive){
		color=colorBusy;
	}
	state=1;
}
public void setLatch(){
	if(isActive){
		color=colorLatch;
	}
	state=2;
}
public boolean isBusy(){return (state==1);}
public void setColorIdle(){
	if(isActive){
		color=colorIdle;
	}
}

public void setInactive(){color=colorInactive;isActive=false;}//si la chip n'est pas utilisée pour une instruction
public void setActive(){color=colorIdle;state=0;isActive=true;}

  public void setColorDefault(Color couleur){
		color =couleur;
		colorIdle =couleur;
	}
/////////////////////////////////////////////////////////////////
  public void act(){

	  ++delay;

	  if(!isActive){//attention, ça se fera qu'au moment où on devrait utiliser la Ram
		  this.data = input.getData(busidin);//permet d'éteindre le bus si on écrit pas dedans
		  this.addressValue = address.getData(busidadd);
	  }else{
		if (checkInput())  {        //  [verif si les entrees sont là]
			setBusy();
			receive();          //  [copie dans latch entrée]
			delay=1;
		}

		if (state==1){
			if(delay==2)ctl.disable();//ROM
			if(delay==3){

				setLatch();
				latch();

				delay=0;
			}
		}

		if (state==2 && delay==1)
			setIdle();

	  }

 }

  public void act(boolean level){  // 1= raising edge || 0= fallingedge
	    if (level)
	    {
	 //------------------------------------------------------------
	      if (state==2)     setIdle();
	      if (checkInput())  {        //  [verif si les entrees sont là]
	        setBusy();
	        receive();                //  [copie dans latch entrée]
	      }
	//------------------------------------------------------------
	  }else  {      //[pd l'état haut ===> ]  compute et prepare data sortie > latche out put
	//------------------------------------------------------------
	    if (state==1)                // si busy > latch
	     {
	       setLatch();
	       latch();
	     }
	//------------------------------------------------------------
	  }}
/////////////////////////////////////////////////////////////////
  public boolean checkInput(){
	    if (!ROM){//si c la RAM
	        if (ctl.isActive()) {//si WE est actif
	          WE = (ctl.getData() == 1);
	          System.out.println("RAM > \treceiving Ctl : WE = " + WE);
	        }
	        if (WE && isInActive())return true; // RAM  && WE && input dispo

	        return  isAddrActive();
	    }
	    else{//si c la ROM
	    	if (ctl.isActive()) {//psen est actif?
	    		return  isAddrActive();
	    	}

	    }
	    System.out.println("ROM OU RAM on demande de regarder les inputs mais il n'y a rien a l'entré");
	    return false;

	}

public void receive(){
  if (isAddrActive()) {//ram et rom
      this.addressValue = address.getData(busidadd);//pour la rom adress = b00
      if (addressValue >= addressMax) {
        addressValue=-1;
      }
      //if(!ROM) system.out.println("MEM DATA> \treceiving address =   " + addressValue);
      //if(ROM) system.out.println("MEM PROG> \treceiving address =   " + addressValue);
  }
  if (!ROM)//=ram
        if (WE && isInActive()) {
          this.data = input.getData(busidin);
          //system.out.println("MEM DATA> \treceiving data =   " + data);
          hasData=true;
        }
}

public void latch(){
  if (getCurrentAddr() >= 0) { // addresse valable ?
        if (!ROM && WE && hasData) { // WRITE in mem
          this.data = input.getData(busidin);

          setCase(Integer.toString(data), addressValue, 1); // DEC
          //system.out.println("MEM > \twriting data =   " + data + "  at " +  addressValue);
                    hasData=false; WE=false;
        }
        else { // RAM=>LATCH, pas ROM car latch est réécrite dans MemProg
          int data = 0;
          String temp = new String();
          temp = getCase(addressValue, 1);
          if (temp != null && temp.length() > 0)
            data = Integer.decode(temp); // mem decimale =col 1
          output.receive(data);
          //system.out.println("MEM > \tlatching data =   " + data);
        }
      }
    }
/////////////////////////////////////////////////////////////////

//===============================================================================================
//    EXTRA
//===============================================================================================
public void highlight(int data){	//surligne l'instruction dans la mémoire
	  if(data<=addressMax){

		table.changeSelection(data,1,false,false);//+rajouter le fait qu'un click de souris ne change pas la sélection?
		//memory.getSelectionModel().setLeadSelectionIndex(selectionLine);

	  }
	  }
public void highlightSet(int row1){	//surligne l'instruction dans la mémoire
	table.setRowSelectionInterval(row1, row1);

	  }
public void highlightAdd(int row1){	//surligne l'instruction dans la mémoire
	table.addRowSelectionInterval(row1, row1);

	  }
public void highlightRem(){
	table.removeRowSelectionInterval(0,0);
}


public void warning(String text, int a){

	String text2 = new String();
	text2 = "\nLine "
				+ a
				+ " :\n"+text+"\n"
				;
	JOptionPane.showMessageDialog(null, text2, "Warning : Data Memory",JOptionPane.WARNING_MESSAGE);

}

//================================================================================================
//   GRAPHICs
//================================================================================================
    public void printText(Graphics g,int size,String txt,int x,int y,Color col,int type){
      g.setColor(Color.white);
      g.setFont(new Font("Arial", Font.BOLD, size));
      g.drawString(txt,x-1,y-1);
      g.drawString(txt,x-1,y+1);
      g.drawString(txt,x+1,y+1);
      g.drawString(txt,x+1,y-1);
      g.setColor(col);
      g.drawString(txt,x,y);
      setDFont(g);
      }

      public void setDFont(Graphics g){
          g.setColor(Color.black);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
      }

//public int getX() { return x;}
//public int getY() { return y;}
    public int X() { return x;}
    public int Y() { return y;}
public int getLg() {  return lg; }
public int getHt() { return ht;}
//public void setColor(Color color) {    this.color = color;  }
      public void printText(Graphics g,int size,String txt,int x,int y,Color col){
          g.setColor(col);
          g.setFont(new Font("Arial", Font.BOLD, size));
          g.drawString(txt,x,y);
          setDFont(g);
      }

public void dessine(Graphics g) {
  g.setColor(color);
  g.fillRect(x, y, lg, ht);
  g.setColor(Color.black);
//  g.drawString("Memory", x + 25, y + 2 * ht / 3);
  g.drawRect(x, y, lg, ht);


  if (!ROM){//ram
     if(WE){
    	 printText(g,13,"WE!",X()+5,Y()+20,Color.red,1);
    	 g.drawString("[ " + data+" ]", X()+5, Y()+getHt()-20);
     }
     if (addressValue!=-1 && isActive && state != 0) g.drawString("[ "+addressValue+" ]",X()+45,Y()+getHt()-20);
  }
}


//================================================================================================
//    FILE
//================================================================================================


//////////////////////////////////////////////////////////////////
  public void fichier() {    this.emptyColumn(1);  }

  public void fileopen() {
	    int i = 0;
	    String s = "";
	    //Fich fi = new Fich();

	    fi.open();

	    if (fi.isOpen()) {

		    s = fi.getLine();

		    while (s != null){
				if(s.indexOf("@")==0){//aller à l'adresse @XXXX
					int j=0;

					i=Integer.decode(s.substring(1));
					s="0";

					while(j<i){
						if(getCase(j,1) == null){
							setCase(s, j, 1);
						}
						++j;
					}


				}else{
					////system.out.println("index de z"+s.indexOf("z"));
					if(s.lastIndexOf("//") > 0) s = s.substring(0, s.lastIndexOf("//")); // si on met un commentaire après l'instruction
					if(s.lastIndexOf("#") > 0) s = s.substring(0, s.lastIndexOf("#")); // si on met un commentaire après l'instruction
					////system.out.println("S="+s);

					if(s.indexOf("//") == -1 || s.indexOf("#") == -1){//permet d'ajouter des commentaires à l'aide de //

						s = s.trim();//si il n'y a pas mieux que des espaces dans s alors le string devient vide
						////system.out.println("S="+s+"   longueur de s"+ s.length());
						if(s.length() != 0){ // sert si on met juste des espaces avant les // pour les commentaires ou si la ligne est vide

							if (s.length() > 30) { // 15 premiers char de chaques lignes !!
								s = s.substring(0, 30);
							}


							setCase(s, i, 1);
							i++;
						}
					}
				}
				s = fi.getLine();
			}

	      fi.openclose(); // ferme le streem

	    }
	  }
////////////////////////////////////////////////////////////////////////

  public void filesave() {
	    //Fich fi = new Fich();
	    if(fi.save()){
	    int i = 0;
	    boolean adressWrite=false;
	    String adress;


	    while(i<addressMax){

			adress = Integer.toString(i);

		if(Integer.decode(getCase(i,1))!= 0){
			if(adressWrite){
				fi.setLine("@"+adress);
				fi.setLine(getCase(i, 1));
				adressWrite=false;
			}else{
				fi.setLine(getCase(i, 1));
			}

		}else{
			adressWrite=true;
		}

		++i;
	}

	    //system.out.println("last get case"+getCase(i, 1));
	    fi.saveclose();
  }
	  }

  public void tempsave(File temp) {
	    Fich fi = new Fich(true);
	    fi.save2(temp);
	    int i = 0;
	    boolean adressWrite=false;
	    String adress;


	    while(i<addressMax){

			adress = Integer.toString(i);

		if(Integer.decode(getCase(i,1))!= 0){
			if(adressWrite){
				fi.setLine("@"+adress);
				fi.setLine(getCase(i, 1));
				adressWrite=false;
			}else{
				fi.setLine(getCase(i, 1));
			}

		}else{
			adressWrite=true;
		}

		++i;
	}

	    //system.out.println("last get case"+getCase(i, 1));
	    fi.saveclose();
	  }

  public void tempload(File temp){

	  int i = 0;
	    String s = "";

	    Fich fi = new Fich(true);
	    fi.open2(temp);

	    if (fi.isOpen()) {

		    s = fi.getLine();

		    while (s != null){
				if(s.indexOf("@")==0){//aller à l'adresse @XXXX
					int j=0;

					i=Integer.decode(s.substring(1));
					s="0";

					while(j<i){
						if(getCase(j,1) == null){
							setCase(s, j, 1);
						}
						++j;
					}


				}else{
					////system.out.println("index de z"+s.indexOf("z"));
					if(s.lastIndexOf("//") > 0) s = s.substring(0, s.lastIndexOf("//")); // si on met un commentaire après l'instruction
					if(s.lastIndexOf("#") > 0) s = s.substring(0, s.lastIndexOf("#")); // si on met un commentaire après l'instruction
					////system.out.println("S="+s);

					if(s.indexOf("//") == -1 || s.indexOf("#") == -1){//permet d'ajouter des commentaires à l'aide de //

						s = s.trim();//si il n'y a pas mieux que des espaces dans s alors le string devient vide
						////system.out.println("S="+s+"   longueur de s"+ s.length());
						if(s.length() != 0){ // sert si on met juste des espaces avant les // pour les commentaires ou si la ligne est vide

							if (s.length() > 30) { // 15 premiers char de chaques lignes !!
								s = s.substring(0, 30);
							}


							setCase(s, i, 1);
							i++;
						}
					}
				}
				s = fi.getLine();
			}

	      fi.openclose(); // ferme le streem

	    }



  }

  public void setFormat(int disp)
  {
	  model.setFormat(disp);
  }

////////////////////////////////////////////////////////////////////////
}
