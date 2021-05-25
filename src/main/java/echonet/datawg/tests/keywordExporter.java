package echonet.datawg.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.echonetObjects.ECHONETLiteProperty;
import echonet.datawg.inputParsers.DeviceDefinitionParser;
import echonet.datawg.modelExporters.DDGenerator;
import echonet.datawg.utils.Constants;

public class keywordExporter {
	public static void main(String[] args) {
		String inputFile = "0427mra.json";
		System.out.println("Converting DD from " + inputFile);
		DDGenerator ddExporter = new DDGenerator(inputFile);
		List<ECHONETLiteDevice> devs = ddExporter.getELDevices();
		toDeviceNames(devs, "deviceName_Gold");
		toPPNames(devs, "ppName_Gold");
		List<ECHONETLiteDevice> devs2 = deviceFromFile("elDeviceDescription.json");
		toDeviceNames(devs2, "deviceName_Test");
		toPPNames(devs2, "ppName_Test");

	}
	public static void toDeviceNames(List<ECHONETLiteDevice> elDevices, String fileName) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode devArray = mapper.createArrayNode();
		FileWriter file = null;
		try {
			file = new FileWriter("data/sig/" + fileName+".json",false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(ECHONETLiteDevice dev: elDevices) {
			devArray.add(dev.toDeviceName());
		}
		try {
			file.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(devArray));
			file.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void toPPNames(List<ECHONETLiteDevice> elDevices, String fileName) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode ppArray = mapper.createArrayNode();
		FileWriter file = null;
		try {
			file = new FileWriter("data/sig/" + fileName+".json",false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(ECHONETLiteDevice dev: elDevices) {
			for(ECHONETLiteProperty pp : dev.getProperties()) {
				ppArray.add(pp.toPPName());
			}	
		}
		try {
			file.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ppArray));
			file.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static  List<ECHONETLiteDevice> deviceFromFile(String fileName) {
		BufferedReader reader;
		JSONObject obj;
		JSONParser parser = new JSONParser();
		DeviceDefinitionParser loader = null;
		List<ECHONETLiteDevice> devs = null;
		try {
			// load JSON from file
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			obj = (JSONObject) parser.parse(reader);
			// load datatype definitions
			if(obj.get(Constants.KEYWORD_DEVICES) != null) {
				devs = DeviceDefinitionParserTest.getAllDeviceDefinition((JSONObject) obj.get(Constants.KEYWORD_DEVICES));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return devs;
	}
}
