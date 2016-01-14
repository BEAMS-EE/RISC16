/*
 * Editor.java
 *
 * Created on 2 octobre 2006, 17:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package editor;

import java.awt.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * 
 * @author ENGLEBIN Laurent
 */
public class Editor extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Creates a new instance of Editor */
	public Editor() {
		super();
		this.setFont(new Font("Courier", this.getFont().getStyle(), this
				.getFont().getSize()));
		this.setCaretColor(Color.BLUE);

		try {
			doc = this.getStyledDocument();

			// Style "normal":
			normal = doc.addStyle("normal", null);
			StyleConstants.setForeground(normal, Color.black);
			StyleConstants.setBold(normal, false);

			// Style commentaire:
			CommentStyle = doc.addStyle("Comment", null);
			StyleConstants.setForeground(CommentStyle, Color.gray);
			StyleConstants.setBold(CommentStyle, false);

			// Style label:
			LabelStyle = doc.addStyle("Label", null);
			StyleConstants.setForeground(LabelStyle, Color.magenta);
			StyleConstants.setBold(LabelStyle, false);
			StyleConstants.setItalic(LabelStyle, true);
			// Style addresse:
			AddrStyle = doc.addStyle("Addresse", null);
			StyleConstants.setForeground(AddrStyle, Color.blue);
			StyleConstants.setBold(AddrStyle, false);
			StyleConstants.setItalic(AddrStyle, true);

			// Style instru:
			instruStyle = doc.addStyle("Instruction", null);
			StyleConstants.setForeground(instruStyle, new Color(80, 170, 230));
			StyleConstants.setBold(instruStyle, true);
			// Style instru:
			instruAmelioreStyle = doc.addStyle("Instruction", null);
			StyleConstants.setForeground(instruAmelioreStyle, Color.green);
			StyleConstants.setBold(instruAmelioreStyle, true);

			this.colorise(0, doc.getLength(), doc.getText(0, doc.getLength()));

			courant = doc.getText(0, doc.getLength());
			// this.addKeyListener(this);

		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}


	public void colorise(boolean all) {
		try {
			String text = doc.getText(0, doc.getLength());
			int curs, i, j, k;
			String tmp = courant;
			courant = text;

			if (!tmp.equals(text) && !all) {

				curs = this.getCaretPosition();
				k = text.length() - tmp.length();
				if ((i = curs - 50) < 0)
					i = 0;
				if ((j = curs + 50) > text.length())
					j = text.length();

				if (k > 0)
					if ((i -= k) < 0)
						i = 0;

				for (; i > 0; i--) {
					if (text.charAt(i) == '\n')
						break;
					else if (text.charAt(i) == '\r')
						break;
				}
				k = text.indexOf("\n", j);
				if (k == -1)
					k = text.indexOf("\r", j);
				if (k == -1)
					k = text.length();
				j = k;

				this.colorise(i, j, text.substring(i, j));

				this.setStyledDocument(doc);
				this.setCaretPosition(curs);
			} else if (all)
				this.colorise(0, doc.getLength(), text);

		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}

	public void colorise(int start, int end, String text) {
		
		doc.setCharacterAttributes(start, end - start, normal, true);

		colorise(instruRegex2, instruAmelioreStyle);
		colorise(instruRegex, instruStyle);
		
		colorise("(^|\n)*(@)((?:-?[0-9]{1,5})|(?:0x[0-9A-F]{1,4}))*(\\n)", AddrStyle);
		colorise("(#|//).*", CommentStyle);
		colorise("(^|\n).*.:", LabelStyle);
	}


	private void colorise(String regex,Style style){
		StyledDocument doc = getStyledDocument();
		   Pattern pattern= Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		try {
			String text = doc.getText(0,doc.getLength());
		   Matcher matcher = pattern.matcher(text);
		   while(matcher.find()){
			   doc.setCharacterAttributes(matcher.start(),matcher.end()-matcher.start(),style,true);
        }
		} catch (BadLocationException e) {
			e.printStackTrace();
		}		
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public void setSize(Dimension d) {
		if (d.width < getParent().getSize().width) {
			d.width = getParent().getSize().width;
		}
		super.setSize(d);
	}

	public void updateCourant() {
		try {
			this.courant = doc.getText(0, doc.getLength());
		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}

	public void setIsa(int i) {
		isa = i;

		if (i>0) instruRegex2=instruAmelioreRegex[i-1];
		else instruRegex2="";
		colorise(true);
	}

	public void setRegex(String[] instructionSet) {
		instruRegex2="";
		for (int i=0;i<instructionSet.length;i++){
			instruRegex2+=instructionSet[i];
			instruRegex2+="|";		
		}
		colorise(true);
	}

	private Style normal, LabelStyle, AddrStyle, CommentStyle, instruStyle,
	instruAmelioreStyle;

	StyledDocument doc;
	String courant;

	private int isa = 0;
	private String instruRegex="addi|add|nand|sw|lw|jalr|beq|lui|movi|nop|reset|halt";
	private String instruRegex2="";
	private String[] instruAmelioreRegex ={"sub|shl|sha|nor|xor|shifti|bl|bg","sub|shl|sha|nor|xor|shifti|bl|mul"};

}
