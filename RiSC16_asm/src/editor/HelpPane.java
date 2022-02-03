package editor;

import java.io.*;
import javax.swing.event.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;

/**
 *
 * @author ENGLEBIN Laurent
 */
public class HelpPane extends JEditorPane {

    private URL helpURL;
//////////////////////////////////////////////////////////////////
/**
 * HelpWindow constructor
 * @param String and URL
 */
public HelpPane(URL hlpURL) {

    helpURL = hlpURL;
    setBackground(new Color(220, 220, 220));

    setEditable(false);
    try {
    	setPage(helpURL);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent ev) {
            try {
                if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    setPage(ev.getURL());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    });

    // end constructor
}
}//end HelpWindow class
////////////////////////////////////////////////////////////////