package echonet.datawg.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.inputParsers.FilesParser;
import echonet.datawg.utils.Constants;

public class aaa {
	public static List<DataType> dataTypes = null;

	public static void main(String[] args) {
		dataTypes = FilesParser.definedDataTypeDefault("20211014mraDefinition.json");
		System.out.println(dataTypes.size());

	}
	
    
}
