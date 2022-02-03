package editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.*;


/**
 *
 * @author ENGLEBIN Laurent
 */
class Registers extends JPanel implements ActionListener{

	private JTable table;

	private JButton resetReg;

	private MemTable model;

	private int size;
	public Registers(int size){
		this.size=size;
		setLayout(new BorderLayout ());
		model = new MemTable(size,true);
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

		add(table, "Center");
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setWidth(50);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getTableHeader().setReorderingAllowed(false);

		JPanel t =new JPanel(new GridLayout(0,1));
		add(new JScrollPane(table), "Center");
		resetReg = new JButton("Clear");
		resetReg.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			} });
		t.add(resetReg);
		add(t,"South");
		add(table.getTableHeader(), BorderLayout.NORTH);


	}


	public void reset(){

		for (int i=0;i<size;i++){
			table.setValueAt(Integer.toString(0), i, 1);
		}
	}


	public void write(int i,int data){
		table.setValueAt(Integer.toString(data), i, 1);
	}


	public String getCase(int l,int c){
		//String temp;
		if(table.getValueAt(l, c) == null){
			return null;
		}else{
			try{
				int entier = Integer.decode( (String) table.getValueAt(l, c));
				return (String) table.getValueAt(l, c);
			}
			catch(NumberFormatException e){
				warning("The information is not a decimal or hexadecimal number\nData replaced by 0",l);
				table.setValueAt("0",l,c);
				return "0";
			}
		}
	}

	public void warning(String text, int a){

		String text2 = new String();
		text2 = "\nLine "
			+ a
			+ " :\n"+text+"\n"
			;
		JOptionPane.showMessageDialog(null, text2, "Warning : Registers",JOptionPane.WARNING_MESSAGE);

	}



	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==resetReg){
			reset();
		}

	}


	public void setFormat(int disp) {
		model.setFormat(disp);
	}
}
