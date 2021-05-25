package echonet.datawg.tests;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.echonetObjects.ManualCode;
import echonet.datawg.inputParsers.DataTypeDefinitionParser;
import echonet.datawg.inputParsers.DeviceDefinitionParser;
import echonet.datawg.inputParsers.FilesParser;
import echonet.datawg.inputParsers.SimpleDataTypeParser;
import echonet.datawg.main.DDExporter;
import echonet.datawg.modelExporters.DDGenerator;
import echonet.datawg.utils.Constants;

public class MCRuleExport {

	public static void main(String[] args) {
		String inputFile = "MCRules.json";
		DDExporter.dataTypes = FilesParser.definedDataTypeFromMRA("0427mraDefinition.json");
		List<ManualCode> codes = FilesParser.mcFromFile(inputFile);
	}

}
