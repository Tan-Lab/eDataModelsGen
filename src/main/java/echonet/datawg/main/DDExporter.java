package echonet.datawg.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import org.apache.commons.io.FilenameUtils;

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
	public static boolean exportFF = false;
	protected static String full_MRA_File_Name;
	protected static String aDevice_File_Name;
	protected static String MC_Rules_File_Name;
	protected static String definition_File_Name;
	protected static String inputMRA;
	public static List<ManualCode> mcRules;
	public static JButton exportDD;
	public static DDGenerator ddHandler;
	public static JTextArea txtDisplay;
	public static List<ECHONETLiteDevice> elDevices;
	public static List<DataType> dataTypes = null;
	public static  String currentDir;
	static Logger LOGGER = Logger.getLogger(DDExporter.class.getName());
	
	public static void main(String[] args) {
		full_MRA_File_Name = "";
		aDevice_File_Name = "";
		MC_Rules_File_Name="";
		definition_File_Name = "";
		mcRules = new ArrayList<ManualCode>();
		elDevices = new ArrayList<ECHONETLiteDevice>();
		outputDir = "";
		outputWoTDir = "";
		inputMRA="";
		currentDir = System.getProperty("user.dir");
		
		//outputDir = "data" + File.separator + "DD";
		//outputWoTDir = "data" + File.separator + "TD";
		
		
		getCLIParams(args);
		
		if(isGUI) {
			createUI();
			System.exit(0);
		}
		else if (!inputMRA.equals("")) {
			loadMRAFolderContents();
		} else {
			fileTypeFromNames();
			loadFileContents();
		}
		writeOutput();
		
	}
	public static void writeOutput() {
		if(elDevices.size() > 0) {
			
			if(mcRules.size() >0) {
				ddHandler = new DDGenerator(elDevices,mcRules);
			} else {
				ddHandler = new DDGenerator(elDevices);
			}
			if(!outputDir.equals("")) {
				File ddDir = new File(outputDir);
		        if (! ddDir.exists()){
		        	ddDir.mkdir();   
		        }
				ddHandler.toDDFile(outputDir);
			} 
			if (!outputWoTDir.equals("")){
				ddHandler.toTDFile(outputWoTDir);
			} 
		} else {
			LOGGER.severe("Can not load device object!!");
			System.exit(1);
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
			elDevices.add(FilesParser.deviceDefinitionFromFile(aDevice_File_Name, dataTypes));
		} else {
			LOGGER.severe("**** Could not load device object!!");
		}
		if(!MC_Rules_File_Name.equals("")) {
			mcRules = FilesParser.mcFromFileV1(MC_Rules_File_Name);
		}
	}
	public static void loadMRAFolderContents() {
		definition_File_Name = inputMRA + File.separator + "definitions" + File.separator + "definitions.json";


		File mcRuleFolder = new File(inputMRA + File.separator + "MCRules");
		File deviceObjectsFolder = new File(inputMRA + File.separator + "devices");
		String superClassFileName = inputMRA + File.separator + "superClass" +  File.separator + "0x0000.json";
		String nodeProfileFileName = inputMRA + File.separator + "nodeProfile" +  File.separator + "0x0EF0.json";
		dataTypes = FilesParser.definedDataTypeFromMRA(definition_File_Name);
		for(final File file :  mcRuleFolder.listFiles()) {
			if(file.isFile()) {
				if(FilenameUtils.getExtension(file.getAbsolutePath()).equals("json")) {
					mcRules.add(FilesParser.mcFromFileV1_oneMC(file.getAbsolutePath()));
				}
			}
		}
		
		for(final File file :  deviceObjectsFolder.listFiles()) {
			if(file.isFile()) {
				if(FilenameUtils.getExtension(file.getAbsolutePath()).equals("json")) {
					elDevices.add(FilesParser.deviceDefinitionFromFile(file.getAbsolutePath(), dataTypes));
				}
				
			}
		}
		elDevices.add(FilesParser.deviceDefinitionFromFile(superClassFileName, dataTypes));
		elDevices.add(FilesParser.deviceDefinitionFromFile(nodeProfileFileName, dataTypes));
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
			help.printHelp("Available options:", buildOpts());
			System.exit(1);
		}
		
		if(cmd.hasOption("input-file")) {
			inputFiles = cmd.getOptionValues("input-file");
		}
		if(cmd.hasOption("mra-folder")) {
			inputMRA = cmd.getOptionValue("mra-folder").trim();
			if(inputMRA.equals(".")) {
				inputMRA = currentDir;
			}
		}
		if(cmd.hasOption("dd-output-directory")) {
			outputDir = cmd.getOptionValue("dd-output-directory").trim();
		} else {
			outputDir = currentDir + File.separator  + "webapi_dd";
		}
		
		if(cmd.hasOption("wot-output-directory")) {
			outputWoTDir = cmd.getOptionValue("wot-output-directory").trim();
		}
		if(cmd.hasOption("enable-GUI")) {
			isGUI = true;
		}
		if(cmd.hasOption("output-ff")) {
			exportFF = true;
		}
		
	}
	public static Options buildOpts() {
		Options options = new Options();
		Option inputFile = new Option("i", "input-file", true, "input files (full_MRA=xxxmra.json, "
				+ "deviceObj=xxx.json, full MCRules = mcRules, 1mcRule=xxx_mcRules.json");
		inputFile.setRequired(false);
		inputFile.setArgName("file or files");
		inputFile.setArgs(Option.UNLIMITED_VALUES);
		inputFile.setValueSeparator(' ');
		options.addOption( inputFile);
		
		Option MRAFolder = new Option("mra", "mra-folder", true, "Load the MRA Folder as Input");
		MRAFolder.setRequired(false);
		MRAFolder.setArgName("folder");
		options.addOption( MRAFolder);
		
		
		Option outputDir = new Option("o", "dd-output-directory", true, "Ouput directory of device description");
		outputDir.setRequired(false);
		outputDir.setArgName("folder");
		options.addOption( outputDir);
		
		Option outputTDDir = new Option("otd", "wot-output-directory", true, "Ouput directory of Thing Description");
		outputTDDir.setRequired(false);
		
		options.addOption( outputTDDir);
		
		Option GUI = new Option("g", "enable-GUI", false, "Enable GUI App");
		options.addOption( GUI);
		Option exportFF = new Option("f", "output-ff", false, "Output EPC:0xFF to DD for special cases");
		options.addOption( exportFF);
		
		
		
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
						elDevices.add(FilesParser.deviceDefinitionFromFile(selectedFile.getPath(), types));
						
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
