package risc16_pipeline;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;



class RegTable extends JPanel implements ActionListener{

	private JTable table;
	private RegistreBank registreBank;
	private int size=8;
	private JButton submitReg,resetReg;
	private Dessin d;
	private MemTable model;

	public RegTable(RegistreBank registreBank){

		setLayout(new BorderLayout ());

		model = new MemTable(size,true);

		table = new JTable(model);
		table.getColumnModel().getColumn(0).setPreferredWidth(6);

		add(table, "Center");
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setWidth(50);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getTableHeader().setReorderingAllowed(false);
		this.registreBank=registreBank;

		JPanel t =new JPanel(new GridLayout(0,2));
		submitReg= new JButton("Submit");
		resetReg = new JButton("Clear");
		submitReg.addActionListener(this);
		resetReg.addActionListener(this);
		t.add(submitReg);
		t.add(resetReg);
		add(table.getTableHeader(), BorderLayout.NORTH);
		add(table, "Center");
		add(t,"South");

		//  setVisible(true);
	}


	private void reset(){

		for (int i=0;i<8;i++){
			registreBank.setRegData(0,i);
			table.setValueAt(Integer.toString(0), i, 1);
		}
		d.repaint();

	}

	private void submit(){
		String temp;

		for (int i=0;i<8;i++){
			temp=getCase(i,1);
			if (temp != null && temp.length() > 0)
				registreBank.setRegData(Integer.decode(temp),i);
		}
		table.setValueAt(Integer.toString(0), 0, 1);
		d.repaint();
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

	private void warning(String text, int a){

		String text2 = new String();
		text2 = "\nLine "
			+ a
			+ " :\n"+text+"\n"
			;
		JOptionPane.showMessageDialog(null, text2, "Warning : Registers",JOptionPane.WARNING_MESSAGE);

	}

	public void setDessin(Dessin d){
		this.d=d;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==resetReg){
			reset();
		} else if (e.getSource()==submitReg){
			submit();
		}

	}


	public void setFormat(int disp) {
		model.setFormat(disp);
	}
}
