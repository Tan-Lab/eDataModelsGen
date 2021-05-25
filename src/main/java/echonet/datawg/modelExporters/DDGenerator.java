package echonet.datawg.modelExporters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.atlas.lib.FileOps;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.echonetObjects.ECHONETLiteProperty;
import echonet.datawg.echonetObjects.EPCManualCode;
import echonet.datawg.echonetObjects.ManualCode;
import echonet.datawg.inputParsers.DataTypeDefinitionParser;
import echonet.datawg.inputParsers.DeviceDefinitionParser;
import echonet.datawg.inputParsers.SimpleDataTypeParser;
import echonet.datawg.utils.Constants;

public class DDGenerator {
	public DDGenerator(List<ECHONETLiteDevice> devices) {
		this.elDevices = devices;
	}
	public DDGenerator(List<ECHONETLiteDevice> devices, List<ManualCode> mcRules) {
		this.mcRules = mcRules;
		this.elDevices = devices;
		updateMCRules();
	}
	public void updateMCRules() {
		for(ManualCode mc : mcRules) {
			for(ECHONETLiteDevice dev: getELDevices()) {
				if(mc.getEoj().equals(dev.getCode())) {		
					for(EPCManualCode mcEPC :mc.getEpcs()) {
						if(mcEPC.getEpc().equals("0xFF")) {
							dev.addAProperty(mcEPC.getPp());
						} else {
							for(ECHONETLiteProperty epc: dev.getProperties()) {
								if(mcEPC.getEpc().equals(epc.getCode())) {
									if(mcEPC.getAccessRule() != null) {
										epc.setAccessRule(mcEPC.getAccessRule());
									}
									if(mcEPC.getAction() !=null && mcEPC.getAction().equals("DEL")) {
										epc.setDELProperty(true);
									}
									if(mcEPC.getNote() != null) {
										epc.setNote(mcEPC.getNote());
									}
									if(mcEPC.getParemeter()!= null) {
										epc.setUrlParameters(mcEPC.getParemeter());
									}
									if(mcEPC.getPpName()!= null) {
										epc.setPropertyName(mcEPC.getPpName());
									}
									if(mcEPC.getType() != null) {
										epc.setDataRestrictions(mcEPC.getType());
									}
								}
								
							}
						}
					}
				}
			}
		}
	}
	public DDGenerator(String filePath) {
		this.predefinedDataTypes = new ArrayList<DataType>();
		this.elDevices = new ArrayList<ECHONETLiteDevice>();
		deviceFromFile(filePath);
	}
	public DDGenerator(String filePath, String defFilePath) {
		this.predefinedDataTypes = new ArrayList<DataType>();
		this.elDevices = new ArrayList<ECHONETLiteDevice>();
		deviceFromFile(filePath,defFilePath);
	}
	public List<DataType> getPredefinedDataTypes() {
		return this.predefinedDataTypes;
	}
	public List<ECHONETLiteDevice> getELDevices() {
		return this.elDevices;
	}
	public void toDDFile(File filePath) {
		if(elDevices.size() > 0) {
			for(ECHONETLiteDevice dev: elDevices) {
				try (Writer file = new OutputStreamWriter(
									new FileOutputStream( filePath + File.separator + dev.getShortName()+".json"),
									StandardCharsets.UTF_8)) { 
					if(dev.toDDWebAPIJSON() != null) {
						ObjectMapper mapper = new ObjectMapper();
						file.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dev.toDDWebAPIJSON()));
					}
						
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		}else {
			System.out.println("Nothing to show");
		}
	}
	public void toDDFile(String filePath) {
		if(elDevices.size() > 0) {
			for(ECHONETLiteDevice dev: elDevices) {
				try (Writer file = new OutputStreamWriter(
									new FileOutputStream( filePath + File.separator + dev.getShortName()+".json"),
									StandardCharsets.UTF_8)) { 
					if(dev.toDDWebAPIJSON() != null) {
						ObjectMapper mapper = new ObjectMapper();
						file.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dev.toDDWebAPIJSON()));
					}
						
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		}else {
			System.out.println("Nothing to show");
		}
	}

	private List<DataType> predefinedDataTypes;
	private List<ECHONETLiteDevice> elDevices;
	private List<ManualCode> mcRules;	
	private  void deviceFromFile(String fileName) {
		BufferedReader reader;
		JSONObject obj;
		JSONParser parser = new JSONParser();
		DeviceDefinitionParser loader = null;
		try {
			// load JSON from file
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			obj = (JSONObject) parser.parse(reader);
			// load datatype definitions
			if(obj.get(Constants.KEYWORD_DEFINITIONS) != null)
				this.predefinedDataTypes = getDataTypeDefinitions((JSONObject) obj.get(Constants.KEYWORD_DEFINITIONS));
			
			if(obj.get(Constants.KEYWORD_DEVICES) != null) {
				loader = new DeviceDefinitionParser(predefinedDataTypes);
				this.elDevices = loader.getAllDeviceDefinition((JSONObject) obj.get(Constants.KEYWORD_DEVICES));
			}
			if(obj.get(Constants.KEYWORD_NODE_PROFILE) != null) {
				this.elDevices.add(loader.toTheLatestDeviceDefinition(
						Constants.KEYWORD_NODE_PROFILE, 
						(JSONObject)obj.get(Constants.KEYWORD_NODE_PROFILE)));
			}
			if(obj.get(Constants.KEYWORD_SUPER_CLASS) != null) {
				this.elDevices.add(loader.toTheLatestDeviceDefinition(
						Constants.KEYWORD_SUPER_CLASS, 
						(JSONObject)obj.get(Constants.KEYWORD_SUPER_CLASS)));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private  void deviceFromFile(String fileName, String defFileName) {
		
		JSONParser parser = new JSONParser();
		DeviceDefinitionParser loader = null;
		try {
			// load JSON from file
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(defFileName), "UTF-8"));
			JSONObject defObj = (JSONObject) parser.parse(reader);
			// load datatype definitions
			if(defObj.get(Constants.KEYWORD_DEFINITIONS) != null)
				this.predefinedDataTypes = getDataTypeDefinitions((JSONObject) defObj.get(Constants.KEYWORD_DEFINITIONS));
			reader.close();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			JSONObject devObj = (JSONObject) parser.parse(reader);
			System.out.println(devObj.toJSONString());
			if(devObj != null) {
				loader = new DeviceDefinitionParser(predefinedDataTypes);
				this.elDevices = loader.getAllDeviceDefinition((JSONObject) devObj);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private  List<DataType> getDataTypeDefinitions(JSONObject obj) {
		JSONObject objType = null;
		List<DataType> dataTypes = new ArrayList<DataType>();
		String objKey = "";
		
		Iterator<?> keys = obj.keySet().iterator();
		while(keys.hasNext()) {
			String key = (String) keys.next();
			DataType type = SimpleDataTypeParser.toTypeDefinition(key, (JSONObject)obj.get(key));
			if(type != null)
				dataTypes.add(type);
			if(key.contains("object")) {
				objType = (JSONObject)obj.get(key);
				objKey = key;
			}
		}
		if(objType != null) {
			// load object Type;
			
			DataTypeDefinitionParser loader = new DataTypeDefinitionParser(dataTypes);
			DataType rs = loader.toObjectType(objKey, objType);
			dataTypes.add(rs);
		}
		return dataTypes;
	}

}
