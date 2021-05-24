package echonet.datawg.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.echonetObjects.ECHONETLiteDevice;
import echonet.datawg.echonetObjects.ECHONETLiteProperty;
import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.echonetObjects.PropertyAccessRule;
import echonet.datawg.echonetObjects.SpecificationReleaseInformation;
import echonet.datawg.utils.AccessRuleEnum;
import echonet.datawg.utils.Constants;

public class DeviceDefinitionParserTest {

	public static List<ECHONETLiteDevice> getAllDeviceDefinition(JSONObject obj) {
		List<ECHONETLiteDevice> rs = new ArrayList<ECHONETLiteDevice>();
		Iterator<?> keys = obj.keySet().iterator();
		while(keys.hasNext()) {
			String key = (String) keys.next();
			ECHONETLiteDevice device = toDeviceDefinitions(key, (JSONObject)obj.get(key));
			if(device != null)
				rs.add(device);
		}
		return rs;
	}
	public static ECHONETLiteDevice toDeviceDefinitions(String key, JSONObject jsonObj) {
		ECHONETLiteDevice rs = null;
		if(jsonObj.get(Constants.KEYWORD_ONE_OF) != null)  {
			JSONArray array = (JSONArray) jsonObj.get(Constants.KEYWORD_ONE_OF);
			for(Object o: array.toArray()) {
				rs = toTheLatestDeviceDefinition( key, (JSONObject) o);
			}
		} else {
			rs = toTheLatestDeviceDefinition( key, jsonObj);	
		}
		return rs;
	}
	public static ECHONETLiteDevice  toTheLatestDeviceDefinition(String key, JSONObject jsonObj) {
		ECHONETLiteDevice device = null;
		JSONObject validRelease = (JSONObject) jsonObj.get(Constants.KEYWORD_VALID_RELEASE);
		if(isLatestRelease(validRelease)) {
			device = new ECHONETLiteDevice(key);
			JSONObject className = (JSONObject) jsonObj.get(Constants.KEYWORD_CLASS_NAME);
			JSONObject elProperties = (JSONObject) jsonObj.get(Constants.KEYWORD_EL_PROPERTIES);
			device.setClassName(toClassName(className));
			device.setShortName("");
			device.setProperties(toPropertyList(elProperties));
		}
		return device;
		
	}
	private static boolean isLatestRelease(JSONObject obj) {
		boolean rs = false;
		Object toReleaseVersionObj = obj.get(Constants.KEYWORD_TO);
		if(toReleaseVersionObj!=null && 
				toReleaseVersionObj.toString().equalsIgnoreCase(Constants.KEYWORD_LATEST)) {
			rs = true;
		}
		return rs;
	}
	private static EnJAStatement toClassName(JSONObject obj) {
		
		String en = obj.get(Constants.KEYWORD_EN).toString();
		String jp = obj.get(Constants.KEYWORD_JA).toString();
		return new EnJAStatement(en, jp);
	}
	
	private static List<ECHONETLiteProperty> toPropertyList(JSONObject obj) {
		List<ECHONETLiteProperty> properties = new ArrayList<ECHONETLiteProperty>();
		Iterator<?> property = obj.keySet().iterator();
		while(property.hasNext()) {
			String code = (String) property.next();
			ECHONETLiteProperty pp = toProperty(code, (JSONObject) obj.get(code));
			if (pp !=null)
				properties.add(pp);
		}
		return properties;
	}
	private static ECHONETLiteProperty toProperty(String code, JSONObject obj) {
		ECHONETLiteProperty rs = null;
		if(obj.get(Constants.KEYWORD_ONE_OF) != null)  {
			JSONArray array = (JSONArray) obj.get(Constants.KEYWORD_ONE_OF);
			for(Object o: array.toArray()) {
				rs = toPropertyFromLatestSpecification(code, (JSONObject) o);
			}
		} else {
			rs = toPropertyFromLatestSpecification(code, obj);
		}
		return rs;
	}
	private static ECHONETLiteProperty  toPropertyFromLatestSpecification(String code, JSONObject obj) {
		ECHONETLiteProperty property = null;
		JSONObject validRelease = (JSONObject) obj.get(Constants.KEYWORD_VALID_RELEASE);
		if(validRelease == null) {
			System.out.println(obj.toJSONString());
		}
		if(isLatestRelease(validRelease)) {
			property = new ECHONETLiteProperty(code);
			JSONObject propertyName = (JSONObject) obj.get(Constants.KEYWORD_PROPERTY_NAME);
			if(propertyName == null) {
				System.out.println(code);
			}
			property.setShortName("");
			property.setPropertyName(toPropertyName(propertyName));
			
		}	
		return property;
		
	}
	private static EnJAStatement toPropertyName(JSONObject obj) {
		
		String en = obj.get(Constants.KEYWORD_EN).toString();
		String jp = obj.get(Constants.KEYWORD_JA).toString();
		return new EnJAStatement(en,jp);
	}
	
}
