package echonet.datawg.inputParsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.echonetObjects.ECHONETLiteProperty;
import echonet.datawg.echonetObjects.EPCManualCode;
import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.echonetObjects.ManualCode;
import echonet.datawg.echonetObjects.PropertyAccessRule;
import echonet.datawg.echonetObjects.UrlParameter;
import echonet.datawg.main.DDExporter;
import echonet.datawg.utils.AccessRuleEnum;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.eConstants;

public class ManualCodeParser {
	public ManualCodeParser(){
		
	}
	public static ManualCode toManualCode(String key, JSONObject obj) {
		ManualCode mc = new ManualCode();
		mc.setEoj(key);
		Iterator<?> mcContains = obj.keySet().iterator();
		while(mcContains.hasNext()) {
			String epc = (String) mcContains.next();
			mc.addEpcs(toEPCManualCode(epc, (JSONObject)obj.get(epc)));
		}
		return mc;
	}
	public static ManualCode toManualCodeV1(JSONObject obj) {
		ManualCode mc = new ManualCode();
		
		mc.setEoj(obj.get(eConstants.KEYWORD_EOJ).toString());
		JSONArray properties = (JSONArray) obj.get(Constants.KEYWORD_PROPERTIES);
		if(properties != null) {
			for(int i = 0; i < properties.size(); i++) {
				JSONObject epcObj = (JSONObject) properties.get(i);
				mc.addEpcs(toEPCManualCodeV1(epcObj));
			}
		} else {
			System.out.println("No Properties in MC rule");
		}
		return mc;
	}
	public static EPCManualCode toEPCManualCodeV1(JSONObject obj) {
		EPCManualCode epc = new EPCManualCode();
		String epcString = obj.get(eConstants.KEYWORD_EPC).toString().trim();
		epc.setEpc(epcString);
		
		if(epcString.equalsIgnoreCase("0xFF")) {
			ECHONETLiteProperty pp = new DeviceDefinitionParser(DDExporter.dataTypes).
					 toProperty(epcString, obj);
			epc.setPp(pp);
		}  else {
			if(obj.get(Constants.KEYWORD_DATA) != null) {
				JSONObject dataObj = (JSONObject) obj.get(Constants.KEYWORD_DATA) ;
				List<DataType> type = new DeviceDefinitionParser(DDExporter.dataTypes).toDataRestrictionList(dataObj);
				epc.setType(type);
			}  
			if(obj.get(Constants.KEYWORD_ACTION) != null) {
				String action = obj.get(Constants.KEYWORD_ACTION).toString();
				epc.setAction(action);
			}
			if(obj.get(Constants.KEYWORD_URL_PARAM) != null) {
				JSONObject urlObj = (JSONObject) obj.get(Constants.KEYWORD_URL_PARAM);
				
				epc.setParemeter(toUrlParameters(urlObj));
				
			}
			if(obj.get(Constants.KEYWORD_NOTE) != null) {
				JSONObject noteObj = (JSONObject) obj.get(Constants.KEYWORD_NOTE);
				epc.setNote(new EnJAStatement(
							noteObj.get(Constants.KEYWORD_EN).toString(), 
							noteObj.get(Constants.KEYWORD_JA).toString()));
			}
			if(obj.get(Constants.KEYWORD_PROPERTY_NAME) != null) {
				JSONObject ppname = (JSONObject) obj.get(Constants.KEYWORD_PROPERTY_NAME);
				epc.setPpName(new EnJAStatement(
						ppname.get(Constants.KEYWORD_EN).toString(), 
						ppname.get(Constants.KEYWORD_JA).toString()));
			}
			if(obj.get(Constants.KEYWORD_ACCESS_RULE) != null) {
				JSONObject accessRules = (JSONObject) obj.get(Constants.KEYWORD_ACCESS_RULE);
				PropertyAccessRule rule = new PropertyAccessRule();
				String getRule = accessRules.get(Constants.KEYWORD_GET).toString();
				String setRule = accessRules.get(Constants.KEYWORD_SET).toString();
				String infRule = accessRules.get(Constants.KEYWORD_INF).toString();
				rule.setGet(AccessRuleEnum.getByName(getRule));
				rule.setSet(AccessRuleEnum.getByName(setRule));
				rule.setInf(AccessRuleEnum.getByName(infRule));
				epc.setAccessRule(rule);
			}
			if(obj.get(Constants.KEYWORD_SHORTNAME) != null) {
				epc.setShortName(obj.get(Constants.KEYWORD_SHORTNAME).toString() + "(MC)");
			}
		}
		
		
		return epc;
	}
	
	public static EPCManualCode toEPCManualCode(String key, JSONObject obj) {
		EPCManualCode epc = new EPCManualCode();
		epc.setEpc(key);
		
		if(key.equalsIgnoreCase("0xFF")) {
			ECHONETLiteProperty pp = new DeviceDefinitionParser(DDExporter.dataTypes).
					 toProperty(key, obj);
			epc.setPp(pp);
		}  else {
			if(obj.get(Constants.KEYWORD_DATA) != null) {
				JSONObject dataObj = (JSONObject) obj.get(Constants.KEYWORD_DATA) ;
				List<DataType> type = new DeviceDefinitionParser(DDExporter.dataTypes).toDataRestrictionList(dataObj);
				epc.setType(type);
			}  
			if(obj.get(Constants.KEYWORD_ACTION) != null) {
				String action = obj.get(Constants.KEYWORD_ACTION).toString();
				epc.setAction(action);
			}
			if(obj.get(Constants.KEYWORD_URL_PARAM) != null) {
				JSONObject urlObj = (JSONObject) obj.get(Constants.KEYWORD_URL_PARAM);
				
				epc.setParemeter(toUrlParameters(urlObj));
				
			}
			if(obj.get(Constants.KEYWORD_NOTE) != null) {
				JSONObject noteObj = (JSONObject) obj.get(Constants.KEYWORD_NOTE);
				epc.setNote(new EnJAStatement(
							noteObj.get(Constants.KEYWORD_EN).toString(), 
							noteObj.get(Constants.KEYWORD_JA).toString()));
			}
			if(obj.get(Constants.KEYWORD_PROPERTY_NAME) != null) {
				JSONObject ppname = (JSONObject) obj.get(Constants.KEYWORD_PROPERTY_NAME);
				epc.setPpName(new EnJAStatement(
						ppname.get(Constants.KEYWORD_EN).toString(), 
						ppname.get(Constants.KEYWORD_JA).toString()));
			}
			if(obj.get(Constants.KEYWORD_ACCESS_RULE) != null) {
				JSONObject accessRules = (JSONObject) obj.get(Constants.KEYWORD_ACCESS_RULE);
				PropertyAccessRule rule = new PropertyAccessRule();
				String getRule = accessRules.get(Constants.KEYWORD_GET).toString();
				String setRule = accessRules.get(Constants.KEYWORD_SET).toString();
				String infRule = accessRules.get(Constants.KEYWORD_INF).toString();
				rule.setGet(AccessRuleEnum.getByName(getRule));
				rule.setSet(AccessRuleEnum.getByName(setRule));
				rule.setInf(AccessRuleEnum.getByName(infRule));
				epc.setAccessRule(rule);
			}
		}
		
		
		return epc;
	}
	public static List<UrlParameter> toUrlParameters(JSONObject obj) {
		List<UrlParameter> rs = new ArrayList<UrlParameter>();
		Iterator<?> key = obj.keySet().iterator();
		while(key.hasNext()) {
			String name = (String) key.next();
			JSONObject rootObj = (JSONObject) obj.get(name);
			JSONObject dataTypeObj =(JSONObject) rootObj.get(Constants.KEYWORD_DATA);
			JSONObject descObj =(JSONObject) rootObj.get(Constants.KEYWORD_DESCRIPTIONS);
			String isRequired =rootObj.get(Constants.KEYWORD_REQUIRED).toString();
			UrlParameter url = new UrlParameter();
			url.setName(name);
			if(dataTypeObj != null) {
				url.setType( new DeviceDefinitionParser(DDExporter.dataTypes).toDataRestrictionList(dataTypeObj));
			}
			
			if(descObj != null) {
				url.setDes(new EnJAStatement(
						descObj.get(Constants.KEYWORD_EN).toString(), 
						descObj.get(Constants.KEYWORD_JA).toString()));
			}
			if(isRequired != null && !isRequired.contentEquals("")) {
				url.setRequired(Boolean.parseBoolean(isRequired));
			}
			rs.add(url);
		}
		return rs;
	}

}
