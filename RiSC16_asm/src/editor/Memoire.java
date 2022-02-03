package editor;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.table.*;


/**
 *
 * @author ENGLEBIN Laurent
 */
public class Memoire extends JInternalFrame {

	private static final long serialVersionUID = 2039310911158026524L;
	protected JTable table;//ptet mettre en private...
	private MemTable model;
	private JButton assemblage;
	private JButton resetMem;



	protected boolean isActive=true;//true=> l'instruction uilise cette chip

	protected int addressMax= 65536;
	private boolean ROM=true;
	private Fich fi;
	private Architecture architecture;

	//================================================================================================
	//   INITIALISATION
	//================================================================================================

	////////////////////////////////////
	public Memoire(String title) {

		// ex: DATA MEMORY

		super(title,
				true, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable



		//setWindowDim(1300, 305, 305);//setx sety locy
		model = new MemTable(addressMax,false);
		table = new JTable(model){

			//Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				int realColumnIndex = convertColumnIndexToModel(colIndex);

				if (realColumnIndex==1){

					tip =Integer.toBinaryString(Integer.decode((String) table.getModel().getValueAt(rowIndex,1))) ;
					while(tip.length()<16) tip="0"+tip;
					tip="<html>"+tip;
					tip+="<br>";
					tip+=Integer.decode((String) table.getModel().getValueAt(rowIndex,1)).toString() ;
					tip+="<br>";
					tip+=(Integer.decode((String) table.getModel().getValueAt(rowIndex,1))>=32768)?
							(new Integer(Integer.decode((String) table.getModel().getValueAt(rowIndex,1))-65536)).toString()
							:Integer.decode((String) table.getModel().getValueAt(rowIndex,1)).toString() ;
					tip+="</html>";

				}
				return tip;
			}};

			model.setJTable(table);

			;
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
			resetMem.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					fillColumn(1,"0");
				} });
			t.add(resetMem);
			c.add(t,"South");

			setVisible(true);


			setJMenuBar(createMenuBar());


			fi = new Fich();

	}



	///////////////////////////////
	public Memoire(String title,String[] columnNames,final Architecture architecture) {//ROM
		super(title,
				true, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable

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
					while(tip.length()<architecture.instructionSize) tip="0"+tip;
				}
				return tip;
			}};
			model.setJTable(table);

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

			setVisible(true);

			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.setColumnSelectionAllowed(false);
			table.setRowSelectionAllowed(true);


			fi = new Fich();


	}


	private JMenuBar createMenuBar() {
		JMenuBar maBarre = new JMenuBar();
		JMenuItem mImport = new JMenuItem("Import");
		JMenuItem mExport = new JMenuItem("Export");
		maBarre.add(mImport);
		maBarre.add(mExport);
		mImport.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				fileopen();

			}});
		mExport.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				filesave();

			}});
		return maBarre;
	}


	///////////////////////////////////////////////////////////////////////
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
				return (((String) table.getValueAt(l,c)));
			else
				return null;
		}
	}
	//////////////////////////////////////////////////////////////////
	public void setCase(int l, int valeur) {
		table.setValueAt(Integer.toString(valeur), l, 1);
	}
	//////////////////////////////////////////////////////////////////
	public void setCase(String code, int l, int c) {

		table.setValueAt(code, l, c);


	}

	public void setLabel(String label, int l) {
		table.getModel().setValueAt(label,l,4) ;
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

			fi.openclose(); // ferme le stream

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

				if(Integer.decode(getCase(i,1))!= 0){//if(getCase(i,1).indexOf("0")== -1 || getCase(i,1).indexOf("0")!= 0){
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
			fi.openclose(); // ferme le stream
		}
	}

	public void setFormat(int disp)
	{
		model.setFormat(disp);
	}

	////////////////////////////////////////////////////////////////////////
}
