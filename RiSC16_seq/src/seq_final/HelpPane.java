package seq_final;
//////////////////////////////////////////////////////////////////

//package goes here
import java.io.*;
import javax.swing.event.*;
import javax.swing.*;
import java.net.*;

import java.awt.*;

@SuppressWarnings("serial")
public class HelpPane extends JEditorPane {

    private URL helpURL;
//////////////////////////////////////////////////////////////////
/**
 * HelpWindow constructor
 * @param String and URL
 */
public HelpPane(URL hlpURL) {
    //super(title);
    helpURL = hlpURL;
    setBackground(new Color(220, 220, 220));

    setEditable(false);
    try {
    	setPage(helpURL);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    //anonymous inner listener
    addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent ev) {
            try {
                if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    System.out.println(ev.getURL());
                	setPage(ev.getURL());
                }
            } catch (IOException ex) {
                //put message in window
                ex.printStackTrace();
            }
        }
    });

    //setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    // dynamically set location
    //calculateLocation();

    // end constructor
}
}//end HelpWindow class
////////////////////////////////////////////////////////////////