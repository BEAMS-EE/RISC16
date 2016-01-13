package seq_final;

import java.io.*;

import javax.swing.*;
//import java.lang.*;

//////////////////////////////////////////////////////////////////////////////
// Préparation du flux d'entrée
//http://java.sun.com/j2se/1.5.0/docs/api/java/io/BufferedInputStream.html
//src: http://www.scit.wlv.ac.uk/~jphb/java/basicio.html
//    //system.out.println(f.getPath()); // --> 11345
//////////////////////////////////////////////////////////////////////////////

public class Fich extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8174446083380149969L;

	JFileChooser fc;

	//  boolean error = true;
	boolean open = false;
	int i=0,tailleMem=2048;
	String line, msg = "";
	FileReader fr;
	BufferedReader in;
	FileWriter fw;
	BufferedWriter out;



	private String path;

	//////////////////////////////////////////////////////////////////////////////
	  public Fich() {
		   
		    String directory = System.getProperty("user.dir");
			fc = new JFileChooser(directory);
			//chooser.removeChoosableFileFilter(chooser.getFileFilter());
			fc.setFileFilter(new javax.swing.filechooser.FileFilter () {
		        public boolean accept(File f) {
		        	return f.isDirectory()
					|| f.getName().endsWith(".txt");
		        }
		        public String getDescription() {
		            return "memory files (*.txt)";
		        }
		    });
		    
		  }

	public Fich(String path) {
		//system.out.println("* creation class Fich >> path");
		File f = new File(path);
		this.path=path;
		try {
			if (f.exists() && f.canRead()) {
				fr = new FileReader(f);
				in = new BufferedReader(fr);
				//System.out.println(getClass().getResource(path));
				//in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));

				open = true;
			}
		}
		catch (Exception e) {
			System.err.println("File access error !");
			e.printStackTrace();
		}
	}

	public Fich(boolean temp){

	}

	public boolean isOpen() { //Strin
		return open;
	}

	//////////////////////////////////////////////////////////////////////////////
	public boolean open() { //String header
		//system.out.println("* Ouvrir un fichier");
		int returnVal = fc.showOpenDialog(Fich.this);
		open = false;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			path=f.getPath();

			try {
				if (f.exists() && f.canRead()) {
					fr = new FileReader(f);
					in = new BufferedReader(fr);

					open = true;

				}
			}
			catch (Exception e) {
				System.err.println("File access error !");
				e.printStackTrace();
			}
		}
		//system.out.println(msg);
		return open;
	}


	public void open2(File temp) { //String header
		//system.out.println("* Ouvrir un fichier");
		//int returnVal = fc.showOpenDialog(Fich.this);
		open = false;

		File f = temp;
		//if (returnVal == JFileChooser.APPROVE_OPTION) {
		// File f = fc.getSelectedFile();


		try {
			if (f.exists() && f.canRead()) {
				fr = new FileReader(f);
				in = new BufferedReader(fr);

				open = true;

			}
		}
		catch (Exception e) {
			System.err.println("File access error !");
			e.printStackTrace();
		}
		//}
		//system.out.println(msg);
		//return open;
	}

	public String getLine() {
		try {
			if (open && ( (line = in.readLine()) != null)) {
				//system.out.println(line);//une ligne sans rien n'est pas null!
				return line;
			}

		}
		catch (Exception e) {
			System.err.println("File access error !");
			e.printStackTrace();
		}
		i=0;
		return null;//new String();
	}

	public void openclose() {
		try {
			in.close();
		}
		catch (Exception e) {
			System.err.println("File access error !");
			e.printStackTrace();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	public boolean save() {
		//system.out.println("* Sauver un fichier");
		int returnVal = fc.showSaveDialog(Fich.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File fst = fc.getSelectedFile();
			String ext = null;
			File fs = null;
			String s = fst.getPath();
			int i = s.lastIndexOf('.');

			if (i == -1)
				fs = new File(s + ".txt");
			else {
				if (i > 0 && i < s.length() - 1)
					ext = s.substring(i + 1).toLowerCase();

				if (ext != "txt")
					fs = new File(s.substring(0, i) + ".txt");
				else
					fs = fst;
			}
			try {
				if (!fs.exists()) {
					fs.createNewFile();
				}else{
					int res = JOptionPane.showConfirmDialog(null,
							"The file exists: do you want to overwrite it?","",JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.NO_OPTION) return false;

				}
				if (fs.canWrite()) {
					fw = new FileWriter(fs);
					out = new BufferedWriter(fw);
				}
			} catch (Exception e) {
				System.err.println("File access error !");
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}


	public void save2(File temp) {
		//system.out.println("* Sauver un fichier");
		//int returnVal = fc.showSaveDialog(Fich.this);
		//if (returnVal == JFileChooser.APPROVE_OPTION) {
		//File fst = fc.getSelectedFile();
		//String ext = null;
		File fs = temp;
		//String s = fst.getPath();
		//int i = s.lastIndexOf('.');

		//if (i == -1)
		//	fs = new File(s + ".txt");
		//else {
		//	if (i > 0 && i < s.length() - 1)
		//		ext = s.substring(i + 1).toLowerCase();

		//	if (ext != "txt")
		//		fs = new File(s.substring(0, i) + ".txt");
		//	else
		//		fs = fst;
		//}
		try {
			if (!fs.exists()) {
				fs.createNewFile();}
			//}else{
			//	int res = JOptionPane.showConfirmDialog(null,
			//			"The file exists: do you want to overwrite it?","",JOptionPane.YES_NO_OPTION);
			//	if (res == JOptionPane.NO_OPTION) return false;

			//}
			if (fs.canWrite()) {
				fw = new FileWriter(fs);
				out = new BufferedWriter(fw);
			}
		} catch (Exception e) {
			System.err.println("File access error !");
			e.printStackTrace();
		}
		//	return true;
		//}
		//return false;
	}

	public void setLine(String s) {
		try {
			out.write(s);
			out.newLine();
		}
		catch (Exception e) {
			System.err.println("File access error !");
			e.printStackTrace();
		}
	}

	public void saveclose() {
		try {
			out.close();
		}
		catch (Exception e) {
			System.err.println("File access error !");
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}

	//////////////////////////////////////////////////////////////////////////////
}