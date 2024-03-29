package echonet.datawg.inputParsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.echonetObjects.ManualCode;
import echonet.datawg.utils.Constants;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class FilesParser {
	static Logger LOGGER = Logger.getLogger(FilesParser.class.getName());
	public static List<DataType> definedDataTypeFromJSON(JSONObject obj) {
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
	public static List<DataType> definedDataTypeFromMRA(String fileName) {
		BufferedReader reader;
		JSONObject obj;
		JSONParser parser = new JSONParser();
		List<DataType> rs = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			// load JSON from file
			obj = (JSONObject) parser.parse(reader);
			// load datatype definitions
			if(obj.get(Constants.KEYWORD_DEFINITIONS) != null) 
				rs = definedDataTypeFromJSON((JSONObject) obj.get(Constants.KEYWORD_DEFINITIONS));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	public static List<DataType> definedDataTypeDefault(String fileName) {
		ClassLoader classLoader = new FilesParser().getClass().getClassLoader();
		BufferedReader reader;
		JSONObject obj;
		InputStream in = classLoader.getResourceAsStream(fileName); 
		JSONParser parser = new JSONParser();
		List<DataType> rs = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			// load JSON from file
			obj = (JSONObject) parser.parse(reader);
			// load datatype definitions
			if(obj.get(Constants.KEYWORD_DEFINITIONS) != null) 
				rs = definedDataTypeFromJSON((JSONObject) obj.get(Constants.KEYWORD_DEFINITIONS));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	public static List<ECHONETLiteDevice> deviceDefinitionFromMRA(String fileName, List<DataType> preDefinedDataTypes){
		BufferedReader reader;
		JSONObject obj;
		JSONParser parser = new JSONParser(); 
		List<ECHONETLiteDevice> rs = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			obj = (JSONObject) parser.parse(reader);
			if(obj.get(Constants.KEYWORD_DEVICES) != null) {
				rs = new DeviceDefinitionParser(preDefinedDataTypes).getAllDeviceDefinition((JSONObject) obj.get(Constants.KEYWORD_DEVICES));
			} 
			if(obj.get(Constants.KEYWORD_NODE_PROFILE) != null) {
				rs.add(new DeviceDefinitionParser(preDefinedDataTypes).
						toTheLatestDeviceDefinition(Constants.KEYWORD_NODE_PROFILE, 
						(JSONObject)obj.get(Constants.KEYWORD_NODE_PROFILE)));
			}
			if(obj.get(Constants.KEYWORD_SUPER_CLASS) != null) {
				rs.add(new DeviceDefinitionParser(preDefinedDataTypes).
						toTheLatestDeviceDefinition(Constants.KEYWORD_SUPER_CLASS, 
						(JSONObject)obj.get(Constants.KEYWORD_SUPER_CLASS)));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public static ECHONETLiteDevice deviceDefinitionFromFile(String fileName, List<DataType> preDefinedDataTypes){
		BufferedReader reader;
		JSONObject obj;		
		JSONParser parser = new JSONParser();
		ECHONETLiteDevice device = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			obj = (JSONObject) parser.parse(reader);
			String key = StringUtils.left(FilenameUtils.getName(fileName),6);
			device = new DeviceDefinitionParser(preDefinedDataTypes).toDeviceDefinitions(key,obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return device;
	}
	public static List<ManualCode> mcFromFile(String fileName){
		BufferedReader reader;
		JSONObject obj;		JSONParser parser = new JSONParser();
		List<ManualCode> rs = new ArrayList<ManualCode>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			obj = (JSONObject) parser.parse(reader);
			Iterator<?> keys = obj.keySet().iterator();
			while(keys.hasNext()) {
				String key = (String) keys.next();
				ManualCode mc = ManualCodeParser.toManualCode(key, (JSONObject)obj.get(key));
				if(mc != null)
					rs.add(mc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public static List<ManualCode> mcFromFileV1(String fileName){
		BufferedReader reader;
		JSONObject oneObj= null;		
		JSONArray arrayObj = null;
		JSONParser parser = new JSONParser();
		List<ManualCode> rs = new ArrayList<ManualCode>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			Object inputObj = parser.parse(reader);
			if(inputObj instanceof JSONArray) {
				arrayObj = (JSONArray) inputObj;
			} else {
				oneObj = (JSONObject) inputObj;
			}
			if( arrayObj != null) {
				for(int i = 0; i < arrayObj.size(); i ++) {
					ManualCode mc = ManualCodeParser.toManualCodeV1((JSONObject)arrayObj.get(i));
					if(mc != null) {
						rs.add(mc);
					} else {
						LOGGER.severe(String.format("Could not load MC Rule correctly! from %s", fileName));
						System.exit(1);
					}
						
				}
			}
			if(oneObj != null) {
				ManualCode mc = ManualCodeParser.toManualCodeV1(oneObj);
				if(mc != null) {
					rs.add(mc);
				} else {
					LOGGER.severe(String.format("Could not load MC Rule correctly! from %s", fileName));
					System.exit(1);
				}
					
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public static ManualCode mcFromFileV1_oneMC(String fileName){
		BufferedReader reader;
		JSONParser parser = new JSONParser();
		ManualCode mc = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			Object inputObj = parser.parse(reader);
			
			
			if(inputObj != null) {
				mc = ManualCodeParser.toManualCodeV1((JSONObject)inputObj);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mc;
	}

}