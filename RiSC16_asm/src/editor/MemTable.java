package editor;


import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.*;


/**
 *
 * @author ENGLEBIN Laurent
 */
public class MemTable
extends AbstractTableModel {
	/**
	 *
	 */
	private static final long serialVersionUID = 389310682674493969L;
	private int taille;
	private String[] columnNames; // = {"Addr","Contenu"};
	private Object[][] data; //= new String[taille][2];
	private int disp=0;
	private Class[] types;
	private boolean[] canEdit;
	private JTable table=null;


	public MemTable(int size,String[] columnNames) {
		super();
		taille=size;
		this.columnNames = columnNames;
		data = new Object[taille][columnNames.length];
		for (int i = 0; i < taille; i++) {
			data[i][0] = Integer.toString(i);
			data[i][3] = false;
			data[i][4] = Integer.toString(i);
		}
		types = new Class[]{String.class, String.class, String.class, Boolean.class, Boolean.class};
		canEdit = new boolean[]{
				false, false, true, true, false };
	} //pour numéroter




	public MemTable(int size,boolean isReg) {
		super();
		taille=size;
		columnNames = new String[2];
		columnNames[0] = "Address";
		columnNames[1] = "Data";
		data = new String[size][columnNames.length];
		for (int i = 0; i < size; i++) {
			if (isReg)	data[i][0] = "R"+Integer.toString(i);
			else 			data[i][0] = Integer.toString(i);
			data[i][1] = Integer.toString(0);
		}
		types = new Class[]{String.class, String.class};
		canEdit = new boolean[]{
				false, true};

	}


	public void setJTable(JTable table){
		this.table=table;
	};

	public String getColumnName(int col) {
		return columnNames[col];


	}

	public int getRowCount() {
		return taille;
	}

	public int getColumnCount() {
		return (columnNames.length);


	}
	public Class getColumnClass(int c) {
		return types[c];
	}

	public Object getValueAt(int l, int c) {
		if (columnNames[c] == "Data"){
			try{if (disp==0 || disp==3)return getHexaValueAt(l,c);
			//else if(disp==3) return getBinValueAt(l,c);
			//else if (disp==2) if (display==2 && nombre>=32768) nombre = nombre-65536;;
			else return (disp==2 && Integer.decode((String) data[l][c])>=32768) ? getDecSignValueAt(l,c) : getDecValueAt(l,c);
			} catch(NumberFormatException e){return ( (Object) data[l][c]);}
		}
		else return ( (Object) data[l][c]);
	}

	public Object getHexaValueAt(int l,int c){
		String temp=Integer.toHexString(Integer.decode((String) data[l][c])).toUpperCase();
		while(temp.length()<4) temp="0"+temp;
		return "0x"+temp;

	}
	public Object getDecValueAt(int l,int c){
		return (Integer.decode((String) data[l][c]).toString());
	}
	public Object getDecSignValueAt(int l,int c){
		return ((new Integer(Integer.decode((String) data[l][c])-65536))).toString();
	}
	public Object getBinValueAt(int l,int c){
		String temp=Integer.toBinaryString(Integer.decode((String) data[l][c])).toUpperCase();
		while(temp.length()<16) temp="0"+temp;
		return temp;
	}

	public void setValueAt(Object valeur, int l, int c) {
		if (l<taille){
			if (columnNames[c] == "Data"){
				try{
					valeur=(Integer.decode((String) valeur)<0 && Integer.decode((String) valeur)>=-32768) ? new Integer(Integer.decode((String) valeur)+65536).toString(): valeur;
					valeur=(Integer.decode((String) valeur)>65535 || Integer.decode((String) valeur)<-32768) ? "error : number is out of range": valeur;
				}catch(NumberFormatException e){	valeur = "error : not a decimal or hexadecimal number";}
			}

			data[l][c] = valeur;
			fireTableCellUpdated(l,c);
		}
	}

	// méthode non utilisée mais permet d'afficher le tableau à partir d'une certaine ligne
	public void scrollToVisible(int rowIndex) {
		if (!(table.getParent() instanceof JViewport)) {
			return;
		}
		JViewport viewport = (JViewport)table.getParent();
		Rectangle rect = table.getCellRect(rowIndex, 0, true);
		Point pt = viewport.getViewPosition();
		rect.setLocation(rect.x-pt.x, rect.y-pt.y);
		viewport.scrollRectToVisible(rect);
	}

	//////////////////////[*]
	public void emptyCol(int col) {
		for (int i = 0; i < taille; i++) {
			data[i][col] = null;
		}
	}

	//////////////////////////[*]
	public boolean isCellEditable(int l, int c) {
		return canEdit[c];

	}

	public void setFormat(int disp) {
		this.disp=disp;
		fireTableDataChanged();
	}
}
