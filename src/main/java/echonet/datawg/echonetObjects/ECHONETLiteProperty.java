package echonet.datawg.echonetObjects;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import echonet.datawg.dataTypeObjects.BooleanType;
import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.dataTypeObjects.ObjectProperty;
import echonet.datawg.dataTypeObjects.ObjectType;
import echonet.datawg.dataTypeObjects.RawType;
import echonet.datawg.utils.AccessRuleEnum;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.WoTConstants;
import echonet.datawg.utils.eConstants;

public class ECHONETLiteProperty implements Comparable<Object>{
	public ECHONETLiteProperty() {
	}
public ECHONETLiteProperty(String code) {
		this.code = code;
		if(isContainDELProperty() || isToDeleteProperty()) {
			isDELProperty = true;
		} else {
			isDELProperty = false;
		}
	}
	private String code;
	private String atomic;
	private SpecificationReleaseInformation validRelease;
	private EnJAStatement propertyName;
	private String shortName;
	private PropertyAccessRule accessRule;
	private List<DataType> data;
	private EnJAStatement note;
	private List<UrlParameter> urlParameters;
	private boolean isDELProperty;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public SpecificationReleaseInformation getValidRelease() {
		return validRelease;
	}
	public void setValidRelease(SpecificationReleaseInformation validRelease) {
		this.validRelease = validRelease;
	}
	public EnJAStatement getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(EnJAStatement propertyName) {
		this.propertyName = propertyName;
	}
	public PropertyAccessRule getAccessRule() {
		return accessRule;
	}
	public void setAccessRule(PropertyAccessRule accessRule) {
		this.accessRule = accessRule;
	}
	public EnJAStatement getNote() {
		return note;
	}
	public void setNote(EnJAStatement note) {
		this.note = note;
	}
	public List<DataType> getDataRestrictions() {
		return data;
	}
	public void setDataRestrictions(List<DataType> data) {
		this.data = data;
	}
	public ObjectNode toDDWebAPIJSONObjectNode() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		//0xFF is a non EPC code
		if(!code.equals("0xFF")) {
			rootNode.put(eConstants.KEYWORD_EPC, code);
		}
		if(this.getAtomic() != null) {
			rootNode.put(Constants.KEYWORD_EPC_ATOMIC, this.getAtomic());
		}
		ObjectNode descriptionNode = mapper.createObjectNode();
		descriptionNode.put(eConstants.KEYWORD_JA, propertyName.getJP());
		descriptionNode.put(eConstants.KEYWORD_EN, propertyName.getEN());
		rootNode.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
		if(isWritable()) {
			rootNode.put(eConstants.KEYWORD_WRITABLE, true);
		} else {
			rootNode.put(eConstants.KEYWORD_WRITABLE, false);
		}
		if(isObservable()) {
			rootNode.put(eConstants.KEYWORD_OBSERVABLE, true);
		} else {
			rootNode.put(eConstants.KEYWORD_OBSERVABLE, false);
		}
		if(this.getUrlParameters() != null && this.getUrlParameters().size() > 0) {
			rootNode.setAll(toUrlParameterJSON());
		}
		if(data.size() == 1) {
			System.out.println(propertyName.getEN());
			//System.out.println( data.get(0).toWebAPIDeviceDescription().toPrettyString());
			rootNode.set(eConstants.KEYWORD_SCHEMA, data.get(0).toWebAPIDeviceDescription());
		} else if(data.size() > 1) {
			ArrayNode arrayNode = mapper.createArrayNode();
			for(DataType type : data) {
				arrayNode.add(type.toWebAPIDeviceDescription());
			}
			ObjectNode oneOfNode = mapper.createObjectNode();
			oneOfNode.set(eConstants.KEYWORD_ONEOF, arrayNode);
			rootNode.set(eConstants.KEYWORD_SCHEMA, oneOfNode);
		}
		ObjectNode note = null;
		if(this.getNote() != null) {
			note = mapper.createObjectNode();
			note.put(eConstants.KEYWORD_JA, this.getNote().getJP());
			note.put(eConstants.KEYWORD_EN, this.getNote().getEN());
		}
		if(note!=null)
			rootNode.set(eConstants.KEYWORD_NOTE, note);
		
		return rootNode;
	}
	public ObjectNode toThingDescriptionJSONObjectNode() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		// title node
		rootNode.put(WoTConstants.KEYWORD_TITLE, this.getShortName().replace("(MC)", ""));
		// titles node
		ObjectNode titlesNode = mapper.createObjectNode();
		titlesNode.put(WoTConstants.EN_LANGUAGE, this.getShortName().replace("(MC)", ""));
		titlesNode.put(WoTConstants.JP_LANGUAGE , this.getShortJPName());
		rootNode.set(WoTConstants.KEYWORD_TITLES, titlesNode);
		rootNode.put("echonet:epc", this.getCode());
		//  description node
		rootNode.put(WoTConstants.KEYWORD_DESCRIPTION, this.getPropertyName().getEN());
		// descriptions node
		ObjectNode descriptions = mapper.createObjectNode();
		descriptions.put(WoTConstants.EN_LANGUAGE, this.getPropertyName().getEN());
		descriptions.put(WoTConstants.JP_LANGUAGE , this.getPropertyName().getJP());
		rootNode.set(WoTConstants.KEYWORD_DESCRIPTIONS, descriptions);
		if(isGetOnly()) {
			rootNode.put(WoTConstants.KEYWORD_READ_ONLY, true);
		} else {
			rootNode.put(WoTConstants.KEYWORD_READ_ONLY, false);
		}
		if(isSetOnly()) {
			rootNode.put(WoTConstants.KEYWORD_WRITE_ONLY, true);
		} else {
			rootNode.put(WoTConstants.KEYWORD_WRITE_ONLY, false);
		}
		if(isObservable()) {
			rootNode.put(WoTConstants.KEYWORD_OBSERVABLE, true);
		} else {
			rootNode.put(WoTConstants.KEYWORD_OBSERVABLE, false);
		}
		if(data.size() == 1) {
			rootNode.setAll(data.get(0).toThingDescriptionDataSchema());
		} else if(data.size() > 1) {
			ArrayNode arrayNode = mapper.createArrayNode();
			for(DataType type : data) {
				arrayNode.add(type.toThingDescriptionDataSchema());
			}
			ObjectNode oneOfNode = mapper.createObjectNode();
			oneOfNode.set(eConstants.KEYWORD_ONEOF, arrayNode);
			rootNode.setAll(oneOfNode);
		}
		
		return rootNode;
	}
	public ObjectNode toEvent() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		// title node
		rootNode.put(WoTConstants.KEYWORD_TITLE, this.getShortName());
		// titles node
		ObjectNode titlesNode = mapper.createObjectNode();
		titlesNode.put(WoTConstants.EN_LANGUAGE, this.getShortName());
		titlesNode.put(WoTConstants.JP_LANGUAGE , this.getShortJPName());
		rootNode.set(WoTConstants.KEYWORD_TITLES, titlesNode);
		//  description node
		rootNode.put(WoTConstants.KEYWORD_DESCRIPTION, this.getPropertyName().getEN());
		// descriptions node
		ObjectNode descriptions = mapper.createObjectNode();
		descriptions.put(WoTConstants.EN_LANGUAGE, this.getPropertyName().getEN());
		descriptions.put(WoTConstants.JP_LANGUAGE , this.getPropertyName().getJP());
		rootNode.set(WoTConstants.KEYWORD_DESCRIPTIONS, descriptions);
		if(data.size() == 1) {
			rootNode.setAll(data.get(0).toThingDescriptionDataSchema());
		} else if(data.size() > 1) {
			ArrayNode arrayNode = mapper.createArrayNode();
			for(DataType type : data) {
				arrayNode.add(type.toThingDescriptionDataSchema());
			}
			ObjectNode oneOfNode = mapper.createObjectNode();
			oneOfNode.set(eConstants.KEYWORD_ONEOF, arrayNode);
			rootNode.setAll(oneOfNode);
		}
		
		return rootNode;
	}
	public ObjectNode toDDWebAPIJSONObjectNodeReadOnly() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		
		rootNode.put(eConstants.KEYWORD_EPC, code);
		ObjectNode descriptionNode = mapper.createObjectNode();
		descriptionNode.put(eConstants.KEYWORD_JA, propertyName.getJP());
		descriptionNode.put(eConstants.KEYWORD_EN, propertyName.getEN());
		rootNode.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
		rootNode.set(eConstants.KEYWORD_SCHEMA, mapper.createObjectNode());
		ObjectNode note = null;
		if(this.getNote() != null) {
			note = mapper.createObjectNode();
			note.put(eConstants.KEYWORD_JA, this.getNote().getJP());
			note.put(eConstants.KEYWORD_EN, this.getNote().getEN());
		}
		if(note!=null)
			rootNode.set(eConstants.KEYWORD_NOTE, note);
		
		return rootNode;
	}
	public ObjectNode toAction() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		// title node
		rootNode.put(WoTConstants.KEYWORD_TITLE, this.getShortName());
		// titles node
		ObjectNode titlesNode = mapper.createObjectNode();
		titlesNode.put(WoTConstants.EN_LANGUAGE, this.getShortName());
		titlesNode.put(WoTConstants.JP_LANGUAGE , this.getShortJPName());
		rootNode.set(WoTConstants.KEYWORD_TITLES, titlesNode);
		//  description node
		rootNode.put(WoTConstants.KEYWORD_DESCRIPTION, this.getPropertyName().getEN());
		// descriptions node
		ObjectNode descriptions = mapper.createObjectNode();
		descriptions.put(WoTConstants.EN_LANGUAGE, this.getPropertyName().getEN());
		descriptions.put(WoTConstants.JP_LANGUAGE , this.getPropertyName().getJP());
		rootNode.set(WoTConstants.KEYWORD_DESCRIPTIONS, descriptions);
		ObjectNode inputNode = mapper.createObjectNode();
		rootNode.set(WoTConstants.KEYWORD_INPUT, inputNode);
		rootNode.set(WoTConstants.KEYWORD_OUTPUT, toOutput());
		
		return rootNode;
	}
	public ObjectNode toOutput() {
		ObjectType output = new ObjectType();
		output.setDescription(new EnJAStatement("Return true/false and a message", 
												"true/false とメッセージを返す"));
		ObjectProperty rsPP = new ObjectProperty();
		rsPP.setName("result");
		rsPP.setDataType(new BooleanType());
		output.setProperty(rsPP);
		ObjectProperty msgPP = new ObjectProperty();
		msgPP.setName("message");
		msgPP.setDataType(new RawType());
		output.setProperty(msgPP);
		return output.toThingDescriptionDataSchema();
	}
	public ObjectNode toFIWAREJSON() {
		return null;
	}
	public boolean isSetOnly() {
		boolean rs = false;
		if(accessRule.getGet() == AccessRuleEnum.notApplicable
		&& accessRule.getSet() != AccessRuleEnum.notApplicable) {
			rs = true;
		}
		return rs;
	}
	public boolean isGetOnly() {
		boolean rs = false;
		if(accessRule.getGet() != AccessRuleEnum.notApplicable
		&& accessRule.getSet() == AccessRuleEnum.notApplicable) {
			rs = true;
		}
		return rs;
	}
	public boolean isINFOnly() {
		boolean rs = false;
		if(accessRule.getGet() == AccessRuleEnum.notApplicable
		&& accessRule.getInf() != AccessRuleEnum.notApplicable
		&& accessRule.getSet() == AccessRuleEnum.notApplicable) {
			rs = true;
		}
		return rs;
	}

	private boolean isWritable() {
		boolean rs = false;
		if(accessRule.getSet() != AccessRuleEnum.notApplicable)
			rs = true;
		return rs;
	}
	private boolean isObservable() {
		boolean rs = false;
		if(accessRule.getInf()== AccessRuleEnum.required || accessRule.getInf()== AccessRuleEnum.require_c)
			rs = true;
		return rs;
	}
	public boolean isReservedForFutureUse() {
		boolean rs = true;
		if(this.getShortName()!=null && !this.getShortName().equalsIgnoreCase("RFU"))
			rs = false;
		return rs;
	}
	public boolean isContainDELProperty() {
		boolean rs = true;
		if(this.getShortName()!=null && !this.getShortName().contains("(DEL)")) {
			rs = false;
		}	
		return rs;
	}
	public boolean isToDeleteProperty() {
		boolean rs = true;
		if(this.getShortName()!=null && !this.getShortName().equalsIgnoreCase("DEL")) {
			rs = false;
		}	
		return rs;
	}
	public boolean isMCProperty() {
		boolean rs = true;
		if(this.getShortName()!=null && !this.getShortName().contains("(MC)")) {
			rs = false;
		}
		return rs;
	}
	public boolean isDELProperty() {
		
		return isDELProperty;
	}
	public String getShortName() {
		return shortName;
	}
	public String getShortJPName() {
		String rs = "";
		if(this.getPropertyName() != null) {
			rs = this.getPropertyName().getJP().replaceAll("\\s+","");
		}
		return rs;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
		if(isContainDELProperty() || isToDeleteProperty()) {
			isDELProperty = true;
		} else {
			isDELProperty = false;
		}
	}
	public String getAtomic() {
		return atomic;
	}
	public void setAtomic(String atomic) {
		this.atomic = atomic;
	}
	@Override
	public int compareTo(Object o) {
		
		return ((ECHONETLiteProperty)o).getCode().compareTo(this.getCode());
	}
	// For test
		public ObjectNode toPPName() {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode rootNode = mapper.createObjectNode();
			rootNode.put("Input", this.getPropertyName().getEN());
			if(!this.getShortName().equalsIgnoreCase("DEL")) { 
				if(this.getShortName().contains("(MC)")) {
					rootNode.put("Output", StringUtils.join (
							StringUtils.splitByCharacterTypeCamelCase(
									this.getShortName().replace("(MC)", "")),' '));
				} else {
					rootNode.put("Output",StringUtils.join (
							StringUtils.splitByCharacterTypeCamelCase(this.getShortName()),' '));
				}
			}
			return rootNode;
			
		}
		public List<UrlParameter> getUrlParameters() {
			return urlParameters;
		}
		public void setUrlParameters(List<UrlParameter> urlParameters) {
			this.urlParameters = urlParameters;
		}
		public ObjectNode toUrlParameterJSON() {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode rootNode = mapper.createObjectNode();
			ObjectNode propertyNode = mapper.createObjectNode();
			
			for(UrlParameter param : getUrlParameters()) {
				propertyNode.set(param.getName(), param.toURLNode());
			}
			if(propertyNode.size() != 0)
				rootNode.set(Constants.KEYWORD_URL_PARAM, propertyNode);
			return rootNode;
		}
		public void setDELProperty(boolean isDELProperty) {
			this.isDELProperty = isDELProperty;
		}
}