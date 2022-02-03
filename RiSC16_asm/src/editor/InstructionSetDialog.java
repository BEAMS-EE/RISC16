package editor;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 *
 * @author ENGLEBIN Laurent
 */
public class InstructionSetDialog extends JDialog {

	private final static String[] INSTRUSTRING={"ADD","ADDI","NAND","LUI","LW","SW","BEQ","JALR","SUB","XOR","NOR","SHL","SHA","SHIFTI","BL","BG","MUL","XNOR","OR","AND"};

	private JList list;
	private DefaultListModel listModel;
	private String[] instructionSet;
	private String[] oldSet;


	public InstructionSetDialog(JFrame f,String[] oldSet) {
		super(f,"Instruction Set Configuration",true);

		//this.setPreferredSize(new Dimension(500,250));
	    this.setLocation(250,150);
	 	this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    this.setResizable(false);

		this.oldSet=oldSet;


		Container c=this.getContentPane();

		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		JPanel northPanel=new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));

		JPanel listPane=createList();

		northPanel.add(createArea(oldSet));
		northPanel.add(listPane);

		JPanel southPanel=new JPanel();

		JButton okButton=new JButton("OK");
		southPanel.add(okButton);
		//okButton.setSize(okButton.getPreferredSize());
		okButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonOK_actionPerformed(e);

			}});
		JButton cancelButton=new JButton("Cancel");
		southPanel.add(cancelButton);
		//cancelButton.setSize(cancelButton.getPreferredSize());
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonCANCEL_actionPerformed(e);

			}});

		c.add(northPanel);
		c.add(southPanel);
	}

	private JPanel createList() {
		listModel = new DefaultListModel();

		/*for (int i = 0; i < 10; i++) {
			listModel.addElement("List Item " + i);
		}*/

		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(100,150));

		//list.setDragEnabled(true);
		//list.setTransferHandler(new ListTransferHandler());

		JButton btnUp=new JButton("UP");
		btnUp.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				int index = list.getSelectedIndex();
				if( index == -1 )
					JOptionPane.showMessageDialog( null, "Select something to move." );
				else if( index > 0 )
				{
					String temp = (String)listModel.remove( index );
					listModel.add( index - 1, temp );
					list.setSelectedIndex( index - 1 );
				}
			}
		} );

		JButton btnDown=new JButton("DOWN");
		btnDown.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				int index = list.getSelectedIndex();
				if( index == -1 )
					JOptionPane.showMessageDialog( null, "Select something to move." );
				else if( index < listModel.size() - 1 )
				{
					String temp = (String)listModel.remove( index );
					listModel.add( index + 1, temp );
					list.setSelectedIndex( index + 1 );
				}
			}
		} );




		JPanel buttPanel = new JPanel();
		//buttPanel.add(new JLabel("List Drop Mode:"));
		buttPanel.add(btnUp);
		buttPanel.add(btnDown);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(buttPanel, BorderLayout.SOUTH);
		panel.setBorder(BorderFactory.createTitledBorder("Instructions"));
		return panel;
	}

	private JPanel createArea(String[] oldSet) {

		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS ));


		final JLabel opSizeLabel = new JLabel();
		final JLabel instruCountLabel=new JLabel();


		JCheckBox[][]instruCheckBox =new JCheckBox[3][8];

		JPanel panel = new JPanel(new GridLayout(1,INSTRUSTRING.length/8));
		JPanel[] colPanel=new JPanel[INSTRUSTRING.length/8+1];
		HashMap<String,Integer> instruMap=new HashMap<String,Integer>();

		for(int i=0;i<colPanel.length;i++){
			colPanel[i]=new JPanel(new GridLayout(8,1));
			panel.add(colPanel[i]);
			for (int j=0;j<instruCheckBox[i].length && i*8+j<INSTRUSTRING.length;j++){
				instruCheckBox[i][j]=new JCheckBox(INSTRUSTRING[i*8+j]);
				instruMap.put(INSTRUSTRING[i*8+j],i*8+j);

				colPanel[i].add(instruCheckBox[i][j]);
				instruCheckBox[i][j].addItemListener(new ItemListener(){

					@Override
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange() == ItemEvent.SELECTED){
							listModel.addElement(((JCheckBox) e.getSource()).getText());
							instruCountLabel.setText(listModel.size()+" instructions ");
							int opSize= (int) Math.ceil((Math.log(listModel.size())/Math.log(2.0)));
							opSizeLabel.setText(opSize+" bits ");
						}
						else if(e.getStateChange() == ItemEvent.DESELECTED){
							String text=((JCheckBox) e.getSource()).getText();
							listModel.remove(listModel.indexOf(text));
							instruCountLabel.setText(listModel.size()+" instructions ");
							int opSize= (int) Math.ceil((Math.log(listModel.size())/Math.log(2.0)));
							opSizeLabel.setText(opSize+" bits ");
						}
					}
				});

				if (i==0){
					instruCheckBox[i][j].setSelected(true);
					instruCheckBox[i][j].setEnabled(false);
				}


			}

		}

		for(int i=0;i<oldSet.length;i++){
			if (instruMap.containsKey(oldSet[i].toUpperCase())){
				int value=instruMap.get(oldSet[i].toUpperCase());
				//instruCheckBox[value/8][value%8].setEnabled(true);
				instruCheckBox[value/8][value%8].setSelected(true);
				//instruCheckBox[value/8][value%8].doClick();
			}
		}

		panel.setPreferredSize(new Dimension(200,150));
		//panel.add(scrollPane, BorderLayout.CENTER);
		//panel.setBorder(BorderFactory.createTitledBorder("Text Area"));


		mainPanel.add(panel);
		mainPanel.add(instruCountLabel);
		mainPanel.add(opSizeLabel);

		return mainPanel;
	}



    public String [] getDonnees(){
        return instructionSet;
    }

    void jButtonOK_actionPerformed(ActionEvent e) {

    	instructionSet=new String[listModel.size()];
		int opSize= (int) Math.ceil((Math.log(listModel.size())/Math.log(2.0)));
		System.out.println(opSize);
		for(int i=0;i<listModel.size();i++){
			instructionSet[i]=(String) listModel.get(i);
		}
    	setVisible(false);
    	//dispose();

      }
    void jButtonCANCEL_actionPerformed(ActionEvent e) {
    	instructionSet=oldSet;
    	setVisible(false);
    	//dispose();
     }

}
