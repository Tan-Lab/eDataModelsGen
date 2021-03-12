package echonet.datawg.inputParsers;

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

public class DeviceDefinitionParser {
	public DeviceDefinitionParser(List<DataType> dataType) {
		complexDataTypeLoader = new DataTypeDefinitionParser(dataType);
	}
	private DataTypeDefinitionParser complexDataTypeLoader;
	public DataTypeDefinitionParser getComplexDataTypeLoader() {
		return complexDataTypeLoader;
	}
	public void setComplexDataTypeLoader(DataTypeDefinitionParser complexDataTypeLoader) {
		this.complexDataTypeLoader = complexDataTypeLoader;
	}

	public List<ECHONETLiteDevice> getAllDeviceDefinition(JSONObject obj) {
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
	public ECHONETLiteDevice toDeviceDefinitions(String key, JSONObject jsonObj) {
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
	public ECHONETLiteDevice toTheLatestDeviceDefinition(String key, JSONObject jsonObj) {
		ECHONETLiteDevice device = null;
		JSONObject validRelease = (JSONObject) jsonObj.get(Constants.KEYWORD_VALID_RELEASE);
		if(isLatestRelease(validRelease)) {
			device = new ECHONETLiteDevice(key);
			JSONObject className = (JSONObject) jsonObj.get(Constants.KEYWORD_CLASS_NAME);
			JSONObject elProperties = (JSONObject) jsonObj.get(Constants.KEYWORD_EL_PROPERTIES);
			device.setValidRelease(toValidReleaseInformation(validRelease));
			device.setClassName(toClassName(className));
			if(jsonObj.get(Constants.KEYWORD_SHORTNAME) != null) {
				device.setShortName(jsonObj.get(Constants.KEYWORD_SHORTNAME).toString());
			}
			device.setProperties(toPropertyList(elProperties));
		}
		return device;
		
	}
	private SpecificationReleaseInformation toValidReleaseInformation(JSONObject obj) {
		SpecificationReleaseInformation rs = new SpecificationReleaseInformation();
		rs.setFrom(obj.get(Constants.KEYWORD_FROM).toString());
		rs.setTo(obj.get(Constants.KEYWORD_TO).toString());
		return rs;
	}
	private boolean isLatestRelease(JSONObject obj) {
		boolean rs = false;
		Object toReleaseVersionObj = obj.get(Constants.KEYWORD_TO);
		if(toReleaseVersionObj!=null && 
				toReleaseVersionObj.toString().equalsIgnoreCase(Constants.KEYWORD_LATEST)) {
			rs = true;
		}
		return rs;
	}
	private EnJAStatement toClassName(JSONObject obj) {
		
		String en = obj.get(Constants.KEYWORD_EN).toString();
		String jp = obj.get(Constants.KEYWORD_JA).toString();
		return new EnJAStatement(en, jp);
	}
	
	private List<ECHONETLiteProperty> toPropertyList(JSONObject obj) {
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
	private ECHONETLiteProperty toProperty(String code, JSONObject obj) {
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
	private ECHONETLiteProperty toPropertyFromLatestSpecification(String code, JSONObject obj) {
		ECHONETLiteProperty property = null;
		JSONObject validRelease = (JSONObject) obj.get(Constants.KEYWORD_VALID_RELEASE);
		if(isLatestRelease(validRelease)) {
			property = new ECHONETLiteProperty(code);
			JSONObject propertyName = (JSONObject) obj.get(Constants.KEYWORD_PROPERTY_NAME);
			if(propertyName == null) {
				System.out.println(code);
			}
			JSONObject accessRule = (JSONObject) obj.get(Constants.KEYWORD_ACCESS_RULE);
			JSONObject data = (JSONObject) obj.get(Constants.KEYWORD_DATA);
			JSONObject note = (JSONObject) obj.get(Constants.KEYWORD_NOTE);
			if(obj.get(Constants.KEYWORD_SHORTNAME) != null) {
				property.setShortName(obj.get(Constants.KEYWORD_SHORTNAME).toString());
			}
			if(obj.get(Constants.KEYWORD_ATOMIC) != null) {
				property.setAtomic(obj.get(Constants.KEYWORD_ATOMIC).toString());
			}
			property.setValidRelease(toValidReleaseInformation(validRelease));
			property.setPropertyName(toPropertyName(propertyName));
			property.setAccessRule(toAccessRule(accessRule));
			if(note!= null) {
				property.setNote(toPropertyNote(note));
			}
			property.setDataRestrictions(toDataRestrictionList(data));
		}	
		return property;
		
	}
	private EnJAStatement toPropertyName(JSONObject obj) {
		
		String en = obj.get(Constants.KEYWORD_EN).toString();
		String jp = obj.get(Constants.KEYWORD_JA).toString();
		return new EnJAStatement(en,jp);
	}
	private PropertyAccessRule toAccessRule(JSONObject obj) {
		PropertyAccessRule rule = new PropertyAccessRule();
		String getRule = obj.get(Constants.KEYWORD_GET).toString();
		String setRule = obj.get(Constants.KEYWORD_SET).toString();
		String infRule = obj.get(Constants.KEYWORD_INF).toString();
		rule.setGet(AccessRuleEnum.getByName(getRule));
		rule.setSet(AccessRuleEnum.getByName(setRule));
		rule.setInf(AccessRuleEnum.getByName(infRule));
		return rule;
	}
	private EnJAStatement toPropertyNote(JSONObject obj) {
		
		String en = obj.get(Constants.KEYWORD_EN).toString();
		String jp = obj.get(Constants.KEYWORD_JA).toString();
		return new EnJAStatement(en,jp);
	}
	
	private List<DataType> toDataRestrictionList(JSONObject obj) {
		List<DataType> rs = new ArrayList<DataType>();
		JSONArray typeArray = (JSONArray) obj.get(Constants.KEYWORD_ONE_OF);
		
		if(typeArray != null) {
			for(Object o: typeArray.toArray()) {
				rs.add(complexDataTypeLoader.toDataDefinition((JSONObject) o));
			}
		} else {
			rs.add(complexDataTypeLoader.toDataDefinition(obj));
		}
		return rs;
	}
}
