package tools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Xml2Excel {

	private static int row = 0;

	public static void main(String[] args) {

		JFrame frame = new JFrame("XML2Excel");
		final JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JLabel l1 = new JLabel();
		l1.setText("xml Folder Path:");
		JLabel l2 = new JLabel();
		l2.setText("excel Folder Path:");
		final JTextField t1 = new JTextField(70);
		final JTextField t2 = new JTextField(70);
		JButton b = new JButton("start!");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (t1.getText().length() == 0) {
					JOptionPane.showMessageDialog(panel,
							"xml Folder Path is required!!");
					return;
				}
				if (t2.getText().length() == 0) {
					JOptionPane.showMessageDialog(panel,
							"excel Folder Path is required!!");
					return;
				}
				try {
					File p1 = new File(t1.getText());
					File p2 = new File(t2.getText());
					if (!p1.isDirectory() && !p1.isFile()) {
						JOptionPane.showMessageDialog(panel,
								"xml Folder Path is not a File or Directory!!" + t1.getText());
						return;
					}
					if (p1.isFile()) {
						int dot = p1.getName().lastIndexOf('.');
						if ((dot > -1) && (dot < (p1.getName().length() - 1))) {
							if (!"xml".equalsIgnoreCase(p1.getName().substring(
									dot + 1))) {
								JOptionPane.showMessageDialog(panel,
										"xml Folder Path is not a XML File!!");
								return;
							}
						}
						else {

							JOptionPane.showMessageDialog(panel,
									"xml Folder Path is not a XML File!!");
							return;
						}
					}
					if (!p2.isDirectory()) {
						JOptionPane.showMessageDialog(panel,
								"excel Folder Path is not a Directory!!" + t2.getText());
						return;
					}
					new Xml2Excel().execute(p1, p2);

					JOptionPane.showMessageDialog(panel,
							"Complete!!!!");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(panel, e.getMessage());
				}

			}
		});

		panel.add(l1);
		panel.add(t1);
		JButton c1 = new JButton("XML Path");
		c1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(".");
				fc.setDialogTitle("");
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int status = fc.showOpenDialog(panel);
			    if (status == JFileChooser.APPROVE_OPTION) {
			     File f = fc.getSelectedFile();
			     t1.setText(f.getAbsolutePath());
			     }
			}
		});
		panel.add(c1);
		panel.add(l2);
		panel.add(t2);
		JButton c2 = new JButton("Excel Path");

		c2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(".");
				fc.setDialogTitle("");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = fc.showOpenDialog(panel);
			    if (status == JFileChooser.APPROVE_OPTION) {
			     File f = fc.getSelectedFile();
			     t2.setText(f.getAbsolutePath());
			     }
			}
		});
		
		panel.add(c2);
		panel.add(b);
		frame.add(panel);
		frame.setSize(1050, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public boolean execute(File xmlPath, File excelPath) {
		File[] files = null;
		if (xmlPath.isDirectory()) {

			files = xmlPath.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					int dot = pathname.getName().lastIndexOf('.');
					if ((dot > -1) && (dot < (pathname.getName().length() - 1))) {
						if (pathname.isFile()
								&& "xml".equalsIgnoreCase(pathname.getName()
										.substring(dot + 1))) {
							return true;
						}
					}
					return false;
				}
			});
		} else {
			files = new File[]{xmlPath};
		}

		for (int i = 0; i < files.length; i++) {

			File f = files[i];

			SAXReader reader = new SAXReader();
			Document doc = null;
			try {
				doc = reader.read(f);
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			row = 0;
			Workbook wb = new HSSFWorkbook();
			CellStyle style = wb.createCellStyle();
		    style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			Sheet sheet = wb.createSheet("struct");
			w(style, sheet, doc.getRootElement(), 0);

			FileOutputStream fileOut = null;
			try {
				int dot = f.getName().lastIndexOf('.');
				fileOut = new FileOutputStream(excelPath.getAbsolutePath()
						+ File.separator + f.getName().substring(0, dot)
						+ ".xls");
				wb.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private static void w(CellStyle style, Sheet sheet, Element e, int col) {
		sheet.createRow(row++).createCell(col).setCellValue(e.getName());
		List<Attribute> al = e.attributes();
		boolean hasA = false;
		int t = col;
		if (!al.isEmpty()){hasA = true;t=col+1;}
		
		for (Attribute attribute : al) {
			Cell cl = sheet.createRow(row++).createCell(t);
			cl.setCellStyle(style);
			cl.setCellValue(attribute.getName());
		}
		for (int i = 0; i < e.elements().size(); i++) {
			w(style, sheet, (Element) e.elements().get(i), hasA ? col + 2 : col + 1);
		}
	}
}
