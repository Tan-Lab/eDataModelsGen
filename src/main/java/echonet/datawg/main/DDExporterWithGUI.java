package echonet.datawg.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.modelExporters.DDGenerator;


public class DDExporterWithGUI {
	public static JButton exportDD;
	public static DDGenerator ddHandler;
	public static JTextArea txtDisplay;
	public static List<ECHONETLiteDevice> elDevices;
	public static void main(String[] args) {
		elDevices = new ArrayList<ECHONETLiteDevice>();
		createUI();
	 }
	private static JPanel createTopPanel() {
		   JPanel panel = new JPanel();
		   panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		   JMenuBar mb=new JMenuBar(); 
		   JMenu fileMenu=new JMenu("JSON File"); 
		   fileMenu.setBackground(Color.BLUE);
		   JMenuItem openFile=new JMenuItem("Open");  
		   fileMenu.add(openFile);
		   mb.add(fileMenu);
		   panel.add(mb);
		   openFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
					fileChooser.setFileFilter(new FileFilter() {
						public String getDescription() {
							return "JSON Files (*.json)";
						}
						public boolean accept(File f) {
							if (f.isDirectory()) {
								return true;
							} else {
								String filename = f.getName().toLowerCase();
								return filename.endsWith(".json") ;
							}
						}
					});
					fileChooser.setAcceptAllFileFilterUsed(true);
					int result = fileChooser.showOpenDialog(null);
					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						ddHandler= new DDGenerator(selectedFile.getPath());
						elDevices = ddHandler.getELDevices();
						
					} 
					
					if(elDevices.size() != 0) {
						txtDisplay.setText("There are: " + elDevices.size() + " devices from the MRA\n");
						exportDD.setEnabled(true);
					} else {
						txtDisplay.setText("Can not load any device from the MRA\n");
					}
					
				}
		   });
		   return panel;
	   }
	private static JPanel createMiddlePanel() {
		   JPanel panel = new JPanel();
		   panel.setBorder(new TitledBorder(new EtchedBorder(), "Logs Display Area"));
	       txtDisplay = new JTextArea(20, 50);
	       txtDisplay.setEditable(false); // set textArea non-editable
	       JScrollPane scroll = new JScrollPane(txtDisplay);
	       scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	       panel.add(scroll);
		   return panel;
	   }
	private static JPanel createBottomPanel() {
		   JPanel panel = new JPanel();
		   exportDD = new JButton("To DeviceDescription");
		   exportDD.setEnabled(false);
		   JButton exportFiware = new JButton("To FiwareSmartDataModel");
		   exportFiware.setEnabled(false);
		   JButton exports4echonet = new JButton("To s4echonet");
		   exports4echonet.setEnabled(false);
		   JButton toswagger = new JButton("Export swagger.json");
		   toswagger.setEnabled(false);
		   panel.add(exportDD);
		   panel.add(exportFiware);
		   panel.add(exports4echonet);
		   panel.add(toswagger);
		   exportDD.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
				   JFileChooser chooser = new JFileChooser();
				   chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				   chooser.setDialogTitle("Select a directory to store DD files");
				   chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				   if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
					   txtDisplay.setText("Start converting device descriptions... \n");
					   ddHandler.toDDFile(chooser.getSelectedFile());	
					   txtDisplay.setText("Finish converting device descriptions \n");
				   } else {
					   txtDisplay.setText("Please choose the target directory \n");
				   }
				}
		      });
		      return panel;
	   }
	private static void createUI(){ 
		   JFrame frame = new JFrame("Data Models Exporter");
		   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		   frame.setLayout(new BorderLayout());
		   frame.add(createTopPanel(), BorderLayout.NORTH);
		   frame.add(createMiddlePanel(), BorderLayout.CENTER);
		   frame.add(createBottomPanel(), BorderLayout.SOUTH);
		   frame.setSize(800, 500);      
		   frame.setLocationRelativeTo(null);  
		   frame.setVisible(true);	      
	   }

}
