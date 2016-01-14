/*
 * Icônes provenant de http://www.readyicons.com/ (free) : Sky_Light_(Basic)_SL
 */

package editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

/**
 * 
 * @author ENGLEBIN Laurent
 */
public class EditorTab extends JPanel implements UndoableEditListener,
		ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFileChooser chooser;

	/** Creates a new instance of EditorTab */
	public EditorTab() {
		super();
		this.setLayout(new BorderLayout());

		newButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/New.png")));
		newButton.setPreferredSize(new Dimension(30, 30));
		newButton.addActionListener(this);
		newButton.setToolTipText("New (" + KeyEvent.getKeyModifiersText(mask)
				+ "+N" + ")");
		openButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Open.png")));
		openButton.setPreferredSize(new Dimension(30, 30));
		openButton.addActionListener(this);
		openButton.setToolTipText("Open (" + KeyEvent.getKeyModifiersText(mask)
				+ "+O" + ")");
		saveButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Save.png")));
		saveButton.setPreferredSize(new Dimension(30, 30));
		saveButton.addActionListener(this);
		saveButton.setToolTipText("Save (" + KeyEvent.getKeyModifiersText(mask)
				+ "+S" + ")");
		saveasButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Save_As.png")));
		saveasButton.setToolTipText("Save As...");
		saveasButton.setPreferredSize(new Dimension(30, 30));
		saveasButton.addActionListener(this);
		undoButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Undo.png")));
		undoButton.setPreferredSize(new Dimension(30, 30));
		undoButton.addActionListener(this);
		undoButton.setToolTipText("Undo (" + KeyEvent.getKeyModifiersText(mask)
				+ "+Z" + ")");
		redoButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Redo.png")));
		redoButton.setPreferredSize(new Dimension(30, 30));
		redoButton.addActionListener(this);
		redoButton.setToolTipText("Redo (" + KeyEvent.getKeyModifiersText(mask)
				+ "+Y" + ")");
		copyButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Copy.png")));
		copyButton.setPreferredSize(new Dimension(30, 30));
		copyButton.addActionListener(this);
		copyButton.setToolTipText("Copy (" + KeyEvent.getKeyModifiersText(mask)
				+ "+C" + ")");
		cutButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Cut.png")));
		cutButton.setPreferredSize(new Dimension(30, 30));
		cutButton.addActionListener(this);
		cutButton.setToolTipText("Cut (" + KeyEvent.getKeyModifiersText(mask)
				+ "+X" + ")");
		pasteButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Past.png")));
		pasteButton.setPreferredSize(new Dimension(30, 30));
		pasteButton.addActionListener(this);
		pasteButton.setToolTipText("Paste ("
				+ KeyEvent.getKeyModifiersText(mask) + "+V" + ")");
		// commentButton.addActionListener(this);
		assembButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Assemb.png")));
		assembButton.setPreferredSize(new Dimension(30, 30));
		assembButton.setToolTipText("Assembly");
		assembButton.addActionListener(this);
		printButton.setIcon(new ImageIcon(this.getClass().getResource(
				"icons/Print.png")));
		printButton.setPreferredSize(new Dimension(30, 30));
		printButton.addActionListener(this);
		printButton.setToolTipText("Print ("
				+ KeyEvent.getKeyModifiersText(mask) + "+P" + ")");
		

		modified.setPreferredSize(new Dimension(15, 30));
		modified.setForeground(Color.red);
		modified.setFont(new Font(modified.getFont().getName(), Font.BOLD,
				modified.getFont().getSize()));

		toolBar = new JToolBar();

		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(printButton);
		toolBar.add(saveButton);
		toolBar.add(saveasButton);
		toolBar.add(modified);
		toolBar.addSeparator();
		toolBar.add(undoButton);
		toolBar.add(redoButton);
		toolBar.addSeparator();
		toolBar.add(copyButton);
		toolBar.add(cutButton);
		toolBar.add(pasteButton);
		toolBar.addSeparator();
		// toolBar.add(commentButton);
		// toolBar.addSeparator();
		toolBar.add(assembButton);
		

		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		undoManager.setLimit(5000);

		editor.doc.addUndoableEditListener(this);
		editor.addKeyListener(this);

		JScrollPane scrollpane = new JScrollPane();
		scrollpane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setViewportView(editor);

		/*
		 * JPanel editPanel = new JPanel(); editPanel.add(scrollpane);
		 * editPanel.add(toolBar, BorderLayout.NORTH); JTabbedPane tabbedPaneMem
		 * = new JTabbedPane(); tabbedPaneMem.addTab("Editor",editPanel);
		 * tabbedPaneMem.setMnemonicAt(0, KeyEvent.VK_E);
		 * 
		 * memprog = new MemProg();
		 * tabbedPaneMem.addTab("Prog Mem",memprog.getContentPane());
		 * tabbedPaneMem.setMnemonicAt(1, KeyEvent.VK_P);
		 */

		this.add(scrollpane);
		this.add(toolBar, BorderLayout.NORTH);
		updateButtons();
		
		
		
		// à placer dans le constructeur pour éviter des problèmes de lenteur !
		String directory = System.getProperty("user.dir");
		chooser = new JFileChooser(directory);
		//chooser.removeChoosableFileFilter(chooser.getFileFilter());
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter () {
            public boolean accept(File f) {
            	return f.isDirectory()
				|| f.getName().endsWith(".txt");
            }
            public String getDescription() {
                return ".txt";
            }
        });
		
	}

	public void updateButtons() {
		undoButton.setEnabled(undoManager.canUndo());
		redoButton.setEnabled(undoManager.canRedo());
	}

	public void undoableEditHappened(UndoableEditEvent e) {
		if (e.getEdit().isSignificant() && undoManager.isInProgress()) {
			undoManager.addEdit(e.getEdit());
			updateButtons();
			modified.setText("*");
		}
	}

	public void undo() {
		try {
			while (undoManager.canUndo()
					&& editor.doc.getText(0, editor.doc.getLength()).equals(
							editor.courant)) {
				undoManager.undo();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		updateButtons();
		editor.updateCourant();
	}

	public void redo() {
		int i = 0;
		try {
			while (undoManager.canRedo() && i < 2) {
				undoManager.redo();
				if (!editor.doc.getText(0, editor.doc.getLength()).equals(
						editor.courant)) {
					editor.updateCourant();
					i++;
				}
			}
			if (i >= 2)
				undoManager.undo();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		updateButtons();
		editor.updateCourant();
	}

	public void actionPerformed(ActionEvent e) {
		String temp;

		editor.requestFocus();
		if (e.getSource().equals(undoButton))
			undo();
		else if (e.getSource().equals(redoButton))
			redo();
		else if (e.getSource().equals(newButton)) {
			newEditor();
		} else if (e.getSource().equals(openButton)) {
			open();
		} else if (e.getSource().equals(printButton)) {
			print();
		} else if (e.getSource().equals(saveButton)) {
			save();
		} else if (e.getSource().equals(saveasButton)) {
			saveas();
		} else if (e.getSource().equals(copyButton)) {
			editor.copy();
		} else if (e.getSource().equals(cutButton)) {
			editor.cut();
			editor.colorise(false);
		} else if (e.getSource().equals(pasteButton)) {
			editor.paste();
			editor.colorise(false);
			// } else if(e.getSource().equals(commentButton)) {
			// temp = editor.getSelectedText()!=null ? editor.getSelectedText()
			// : "";
			// editor.replaceSelection("/* "+temp+" */");
			// editor.colorise(("/* "+temp+" */").length());
		} else if (e.getSource().equals(assembButton)) {
			assembler();
			//assembler_C();
		}
	}

	public void newEditor() {
		if (!modified.getText().equals(""))
			if (JOptionPane
					.showConfirmDialog(new JFrame(),
							"The active document has been modified.\nDo you want save it?") == JOptionPane.OK_OPTION)
				save();
		pathfile = "";
		open(pathfile);
		undoManager.discardAllEdits();
		updateButtons();
	}

	public void open() {
		if (!modified.getText().equals(""))
			if (JOptionPane
					.showConfirmDialog(new JFrame(),
							"The active document has been modified.\nDo you want save it?") == JOptionPane.OK_OPTION)
				save();
		/*String directory = System.getProperty("user.dir");
		JFileChooser chooser = new JFileChooser(directory);
		//chooser.removeChoosableFileFilter(chooser.getFileFilter());
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter () {
            public boolean accept(File f) {
            	return f.isDirectory()
				|| f.getName().endsWith(".txt");
            }
            public String getDescription() {
                return ".txt";
            }
        });*/
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			pathfile = chooser.getSelectedFile().getAbsolutePath();
			open(pathfile);
			undoManager.discardAllEdits();
			updateButtons();
		}
	}

	public void open(String pathfile) {
		try {
			if (!pathfile.equals("")) {
				File file = new File(pathfile);
				FileInputStream fileStream = new FileInputStream(file);
				editor.doc.removeUndoableEditListener(this);
				editor.read(fileStream, editor);
				editor.doc = editor.getStyledDocument();
				editor.courant = editor.doc.getText(0, editor.doc.getLength());
				editor.colorise(true);
				editor.doc.addUndoableEditListener(this);
				modified.setText("");
			} else if (pathfile.equals("")) {
				editor.doc.remove(0, editor.doc.getLength());
				editor.courant = "";
				modified.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		if (!pathfile.equals(""))
			save(pathfile);
		else
			saveas();
	}

	public void save(String pathfile) {
		try {
			String text = editor.doc.getText(0, editor.doc.getLength());
			text = text.replaceAll("\\n", "\r\n");
			File file = new File(pathfile);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(text);
			fileWriter.close();
			modified.setText("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveas() {
		/*String directory = System.getProperty("user.dir");
		JFileChooser chooser = new JFileChooser(directory);
		//chooser.removeChoosableFileFilter(chooser.getFileFilter());
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter () {
            public boolean accept(File f) {
            	return f.isDirectory()
				|| f.getName().endsWith(".txt");
            }
            public String getDescription() {
                return ".txt";
            }
        });*/
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			pathfile = chooser.getSelectedFile().getAbsolutePath();
			if (!pathfile.endsWith(".txt"))
				pathfile += ".txt";
			
			save(pathfile);
		}
	}

	public void assembler() {
		/*if (!pathfile.equals("")) {
			save(pathfile);
			memprog.fileopen(pathfile);
		} else {
			File tempFile = new File("tempROM.txt");
			tempFile.deleteOnExit();
			save("tempROM.txt");
			memprog.fileopen("tempROM.txt");
		}*/
		memprog.fileopen(saveAndGetPathFile());
	}
	
	public String saveAndGetPathFile(){
		if (!pathfile.equals("")) {
			save(pathfile);
			return pathfile;
		} else {
			File tempFile = new File("tempROM.txt");
			tempFile.deleteOnExit();
			save("tempROM.txt");
			return("tempROM.txt");
		}
		
	}



	public void print() {

		try {
			PrinterJob pjob = PrinterJob.getPrinterJob();
			pjob.setJobName("RiSC16 Editor");
			pjob.setCopies(1);

			PageFormat page = pjob.defaultPage();
			;
			Paper papier = page.getPaper();
			;
			papier.setImageableArea(30, 30, papier.getWidth() - 2*30.0, papier
					.getHeight() - 2*30.0);
			page.setPaper(papier);

			pjob.setPrintable(editor.getPrintable(null, new MessageFormat(
					"[File] : "
							+ ((pathfile.length() > 30) ? "..."
									+ pathfile.substring(
											pathfile.length() - 30, pathfile
													.length()) : pathfile)
							+ " \t :: "
							+ MessageFormat.format("@ {0,time} - {0,date}",
									new Date()))), page);
			if (pjob.printDialog() == false) // choose printer
				return;
			pjob.print();
		} catch (PrinterException pe) {
			pe.printStackTrace();
		}
	}

	public void keyPressed(KeyEvent e) {
		if (e.getModifiers() == mask) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_Z:
				if (undoManager.canUndo())
					undo();
				break;

			case KeyEvent.VK_Y:
				if (undoManager.canRedo())
					redo();
				break;
			case KeyEvent.VK_O:
				open();
				break;
			case KeyEvent.VK_N:
				newEditor();
				break;
			case KeyEvent.VK_P:
				print();
				break;
			case KeyEvent.VK_S:
				save();
				break;
			}
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
		if (e.getModifiers() != mask)
			editor.colorise(false);
		else if (e.getKeyCode() == KeyEvent.VK_V
				|| e.getKeyCode() == KeyEvent.VK_X)
			editor.colorise(false);
	}

	public void setMemprog(MemProg memprog) {
		this.memprog = memprog;
	}
	public Editor getEditor() {
		return editor;
	}

	MemProg memprog;

	UndoManager undoManager = new UndoManager();
	JButton undoButton = new JButton();
	JButton redoButton = new JButton();
	JButton newButton = new JButton();
	JButton openButton = new JButton();
	JButton saveButton = new JButton();
	JButton saveasButton = new JButton();
	JButton cutButton = new JButton();
	JButton copyButton = new JButton();
	JButton pasteButton = new JButton();
	JButton printButton = new JButton();
	JButton assembButton = new JButton();
	int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	String pathfile = "";

	Editor editor = new Editor();


	JLabel modified = new JLabel("");

	JToolBar toolBar;
}
