package echonet.datawg.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.echonetObjects.ManualCode;
import echonet.datawg.inputParsers.FilesParser;
import echonet.datawg.modelExporters.DDGenerator;

public class DDExporter {
	protected static String outputDir;
	protected static String outputWoTDir;
	protected static String[] inputFiles;
	protected static boolean isGUI = false;
	protected static String full_MRA_File_Name;
	protected static String aDevice_File_Name;
	protected static String MC_Rules_File_Name;
	protected static String definition_File_Name;
	public static List<ManualCode> mcRules;
	public static JButton exportDD;
	public static DDGenerator ddHandler;
	public static JTextArea txtDisplay;
	public static List<ECHONETLiteDevice> elDevices;
	public static List<DataType> dataTypes = null;
	
	public static void main(String[] args) {
		full_MRA_File_Name = "";
		aDevice_File_Name = "";
		MC_Rules_File_Name="";
		definition_File_Name = "";
		mcRules = new ArrayList<ManualCode>();
		elDevices = new ArrayList<ECHONETLiteDevice>();
		outputDir = "";
		outputWoTDir = "";
		//outputDir = "data" + File.separator + "DD";
		//outputWoTDir = "data" + File.separator + "TD";
		
		
		getCLIParams(args);
		fileTypeFromNames();
		loadFileContents();
		if(isGUI) {
			createUI();
		} else {
			if(elDevices.size() > 0) {
				System.out.println("DD Contents loaded " + elDevices.size() + " DD found");
				if(mcRules.size() >0) {
					System.out.println("MC rules loaded " + mcRules.size() + " MC rules");
					ddHandler = new DDGenerator(elDevices,mcRules);
				} else {
					ddHandler = new DDGenerator(elDevices);
				}
				if(!outputDir.equals("")) {
					ddHandler.toDDFile(outputDir);
					System.out.println("DD exported!! Check " + outputDir + " directory for results");
				} 
				if (!outputWoTDir.equals("")){
					ddHandler.toTDFile(outputWoTDir);
					System.out.println("TD exported!! Check " + outputWoTDir + " directory for results");
				} 
			} else {
				System.out.println ("Can not load device object!!");
				System.exit(1);
			}
		}
		
	}
	public static void loadFileContents() {
		
		String default_definition_File_Name = "20211026definitions.json";
		
		if(!full_MRA_File_Name.equals("")) {
			if(!definition_File_Name.equals("")) {
				dataTypes = FilesParser.definedDataTypeFromMRA(definition_File_Name);
			}else {
				dataTypes = FilesParser.definedDataTypeFromMRA(full_MRA_File_Name);
			}
			elDevices = FilesParser.deviceDefinitionFromMRA(full_MRA_File_Name, dataTypes);
		} else if(!aDevice_File_Name.equals("")){
			if(!definition_File_Name.equals("")) {
				dataTypes = FilesParser.definedDataTypeFromMRA(definition_File_Name);
			}else {
				dataTypes = FilesParser.definedDataTypeDefault(default_definition_File_Name);
			}
			elDevices = FilesParser.deviceDefinitionFromFile(aDevice_File_Name, dataTypes);
		} else {
			System.out.println("No device definition file");
		}
		if(!MC_Rules_File_Name.equals("")) {
			mcRules = FilesParser.mcFromFileV1(MC_Rules_File_Name);
		}
	}
	public static void fileTypeFromNames() {
		for(String fileName: inputFiles) {
			if(fileName.toLowerCase().contains("mra.json".toLowerCase())) {
				full_MRA_File_Name = fileName;
			} else if(fileName.toLowerCase().contains("mcrules.json") || fileName.toLowerCase().contains("mcrule.json")) {
				MC_Rules_File_Name = fileName;
			} else if(fileName.toLowerCase().contains("definition.json".toLowerCase()) || fileName.toLowerCase().contains("definitions.json".toLowerCase())) {
				definition_File_Name = fileName;
			} else {
				aDevice_File_Name = fileName;
			}
		}
	}
	public static void getCLIParams(String[] args) {
		Options option = buildOpts();
		CommandLineParser parser = new DefaultParser();
		HelpFormatter help = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(option, args);
		} catch (ParseException e) {			//e.printStackTrace();
			help.printHelp("Usaged:", buildOpts());
			System.exit(1);
		}
		
		if(cmd.hasOption("input-file")) {
			inputFiles = cmd.getOptionValues("input-file");
		}
		if(cmd.hasOption("dd-output-directory")) {
			outputDir = cmd.getOptionValue("dd-output-directory").trim();
		}
		if(cmd.hasOption("wot-output-directory")) {
			outputWoTDir = cmd.getOptionValue("wot-output-directory").trim();
		}
		if(cmd.hasOption("display-GUI")) {
			isGUI = true;
		}
		
	}
	public static Options buildOpts() {
		Options options = new Options();
		Option inputFile = new Option("i", "input-file", true, "input files (full_MRA=xxxmra.json, "
				+ "deviceObj=xxx.json, full MCRules = mcRules, 1mcRule=xxx_mcRules.json");
		inputFile.setRequired(true);
		inputFile.setArgs(Option.UNLIMITED_VALUES);
		inputFile.setValueSeparator(' ');
		options.addOption( inputFile);
		
		Option outputDir = new Option("o", "dd-output-directory", true, "Ouput directory of device description");
		outputDir.setRequired(false);
		options.addOption( outputDir);
		
		Option outputTDDir = new Option("otd", "wot-output-directory", true, "Ouput directory of Thing Description");
		outputTDDir.setRequired(false);
		
		options.addOption( outputTDDir);
		
		Option GUI = new Option("g", "display-GUI", false, "Enable GUI App");
		options.addOption( GUI);
		return options;
	}
	private static JPanel createTopPanel() {
		   JPanel panel = new JPanel();
		   panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		   JMenuBar mb=new JMenuBar(); 
		   JMenu fileMenu=new JMenu("JSON File"); 
		   fileMenu.setBackground(Color.BLUE);
		   JMenuItem openMRAFile=new JMenuItem("Load Full MRA");  
		   JMenuItem openOneFile=new JMenuItem("Load One MRA"); 
		   fileMenu.add(openMRAFile);
		   fileMenu.add(openOneFile);
		   mb.add(fileMenu);
		   panel.add(mb);
		   openMRAFile.addActionListener(new ActionListener() {
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
		   
		   openOneFile.addActionListener(new ActionListener() {
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
						List<DataType> types = FilesParser.definedDataTypeFromMRA("0427mraDefinition.json");
						elDevices = FilesParser.deviceDefinitionFromFile(selectedFile.getPath(), types);
						
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
