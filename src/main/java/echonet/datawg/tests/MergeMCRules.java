package echonet.datawg.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class MergeMCRules {

	public static void main(String[] args) {
		List<MCRule> rules = new ArrayList<MCRule>();
		File folder = new File("/home/cupham/Code/guidelines/dd/v130/1_mra/devices");
		File[] listOfFiles = folder.listFiles();
		JSONParser parser = new JSONParser();
		ObjectMapper mapper = new ObjectMapper();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	if(file.getName().contains("mcrule.json")) {
		    		JSONObject obj = null;
		    		try {
						obj = (JSONObject) parser.parse(new FileReader(file));
						Iterator<?> keys = obj.keySet().iterator();
						while(keys.hasNext()) {
							String key = (String) keys.next();
							JSONObject ruleObj = (JSONObject) obj.get(key);
							JsonNode jsonNode = mapper.valueToTree(ruleObj);
							if (jsonNode.isObject()) {
								rules.add(new MCRule(key, (ObjectNode) jsonNode));
						      }
							//
						}
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
		    	}
		        
		    }
		}
		System.out.println(rules.size());
		Collections.sort(rules, Collections.reverseOrder());
		ObjectNode rootNode = mapper.createObjectNode();
		for(MCRule rule: rules) {
			rootNode.set(rule.getEoj(), rule.getRule());
			
		}
		try (Writer file = new OutputStreamWriter(
				new FileOutputStream("MCRule.json"),
				StandardCharsets.UTF_8)) { 
		
			file.write(mapper.writerWithDefaultPrettyPrinter().
					writeValueAsString(rootNode));
			
		} catch (IOException e) {
		e.printStackTrace();
		}
				

	}

	

}
