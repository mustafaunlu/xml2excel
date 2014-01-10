package tools;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class FileChooserDemo extends JPanel {

 static final long serialVersionUID = 5854418136127725290L;

 public class ExtensionFilter extends FileFilter {
  private String extensions[];

  private String description;

  public ExtensionFilter(String description, String extension) {
   this(description, new String[] { extension });
  }

  public ExtensionFilter(String description, String extensions[]) {
   this.description = description;
   this.extensions = (String[]) extensions.clone();
  }

  public boolean accept(File file) {
   if (file.isDirectory()) {
    return true;
   }
   int count = extensions.length;
   String path = file.getAbsolutePath();
   for (int i = 0; i < count; i++) {
    String ext = extensions[i];
    if (path.endsWith(ext)
      && (path.charAt(path.length() - ext.length()) == '.')) {
     return true;
    }
   }
   return false;
  }

  public String getDescription() {
   return (description == null ? extensions[0] : description);
  }
 }

 public FileChooserDemo() {
  JButton jb = new JButton("Open File Viewer");
  add(jb);
  ActionListener listener = new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser(".");
    // chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    FileFilter type1 = new ExtensionFilter("Java source", ".java");
    FileFilter type2 = new ExtensionFilter("Image files",
      new String[] { ".jpg", ".gif", "jpeg", "xbm" });
    FileFilter type3 = new ExtensionFilter("HTML files",
      new String[] { ".htm", ".html" });
    chooser.addChoosableFileFilter(type1);
    chooser.addChoosableFileFilter(type2);
    chooser.addChoosableFileFilter(type3);
    chooser.setAcceptAllFileFilterUsed(true);
    chooser.setFileFilter(type2); // Initial filter setting
    int status = chooser.showOpenDialog(FileChooserDemo.this);
    if (status == JFileChooser.APPROVE_OPTION) {
     File f = chooser.getSelectedFile();
     System.out.println(f);
    }
   }
  };
  jb.addActionListener(listener);
 }

 public static void main(String args[]) {
  JFrame f = new JFrame("Enhanced File Example");
  JPanel j = new FileChooserDemo();
  f.getContentPane().add(j, BorderLayout.CENTER);
  f.setSize(300, 200);
  f.setVisible(true);
  f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 }
}