package echonet.datawg.modelExporters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.inputParsers.DataTypeDefinitionParser;
import echonet.datawg.inputParsers.DeviceDefinitionParser;
import echonet.datawg.inputParsers.SimpleDataTypeParser;
import echonet.datawg.utils.Constants;

public class DDGenerator {
	public DDGenerator(String filePath) {
		this.predefinedDataTypes = new ArrayList<DataType>();
		this.elDevices = new ArrayList<ECHONETLiteDevice>();
		deviceFromFile(filePath);
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
	private List<DataType> predefinedDataTypes;
	private List<ECHONETLiteDevice> elDevices;	
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
