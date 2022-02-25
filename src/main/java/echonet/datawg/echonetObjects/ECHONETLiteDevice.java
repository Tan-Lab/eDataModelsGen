package echonet.datawg.echonetObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import echonet.datawg.utils.WoTConstants;
import echonet.datawg.utils.eConstants;

public class ECHONETLiteDevice {
	Logger LOGGER = Logger.getLogger(ECHONETLiteDevice.class.getName());
	public ECHONETLiteDevice(String code) {
		this.code = code;
	}
	public ECHONETLiteDevice() {
	}
	private String code;
	private SpecificationReleaseInformation validRelease;
	private EnJAStatement className;
	private String shortName;
	private List<ECHONETLiteProperty> properties;
	
	
	public SpecificationReleaseInformation getValidRelease() {
		return validRelease;
	}
	public void setValidRelease(SpecificationReleaseInformation validRelease) {
		this.validRelease = validRelease;
	}
	public EnJAStatement getClassName() {
		return className;
	}
	public String getShortJPName() {
		String rs = "";
		if(this.getClassName() != null) {
			rs = this.getClassName().getJP().replaceAll("\\s+","");
		}
		return rs;
	}
	public void setClassName(EnJAStatement className) {
		this.className = className;
	}
	public List<ECHONETLiteProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<ECHONETLiteProperty> properties) {
		this.properties = properties;
	}
	public void addAProperty(ECHONETLiteProperty pp) {
		if(this.properties == null) {
			this.properties = new ArrayList<ECHONETLiteProperty>();
		} else {
			this.properties.add(pp);
		}
	}
	public void removeAProperty(ECHONETLiteProperty pp) {
		if(this.properties == null) {

		} else {
			this.properties.remove(pp);
		}
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
		}
	public ObjectNode toDDWebAPIJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		ObjectNode actionNode = null;
		rootNode.put(eConstants.KEYWORD_DEVICE_TYPE, this.getShortName());
		rootNode.put(eConstants.KEYWORD_EOJ, code);
		ObjectNode descriptionNode = mapper.createObjectNode();
		descriptionNode.put(eConstants.KEYWORD_JA, className.getJP());
		descriptionNode.put(eConstants.KEYWORD_EN, className.getEN());
		rootNode.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
		
		ObjectNode ppNode = mapper.createObjectNode();
		Collections.sort(properties, Collections.reverseOrder());
		for(ECHONETLiteProperty pp : properties) {
			if(pp == null) {
				LOGGER.severe(String.format("****Could not export a property at %s (%s)",
						this.getShortName(), this.getCode()));
				System.exit(1);
			} else if(!pp.isReservedForFutureUse() && !pp.isDELProperty() && !pp.isContainDELProperty()){
				if(pp.isSetOnly()) {
					LOGGER.info(String.format("----Exporting Action: %s",pp.getShortName()));
					if(actionNode == null) {
						actionNode = mapper.createObjectNode();
					}
					ObjectNode actionNodeContent= pp.toDDWebAPIJSONObjectNodeReadOnly();
					if(actionNodeContent != null) {
						actionNode.set(pp.getShortName(),	actionNodeContent );
					}
					
				} else if (pp.isMCProperty()){
					LOGGER.info(String.format("----Exporting MC property: %s",pp.getShortName()));
					ObjectNode mcNodeContent = pp.toDDWebAPIJSONObjectNode();
					if(mcNodeContent != null) {
						ppNode.set(pp.getShortName().replace("(MC)", ""), mcNodeContent);
					}
				} else {
					LOGGER.info(String.format("----Exporting a property: %s",pp.getShortName()));
					ObjectNode ppNodeContent = pp.toDDWebAPIJSONObjectNode();
					if(ppNodeContent != null) {
						ppNode.set(pp.getShortName(),ppNodeContent );
					} 

				}
			
			}
		
		}
		rootNode.set(eConstants.KEYWORD_PROPERTIES, ppNode);
		if(actionNode !=null)
			rootNode.set(eConstants.KEYWORD_ACTIONS, actionNode);
		return rootNode;
	}
	public ObjectNode toThingDescriptionJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		// context node
		ArrayNode contextNode = mapper.createArrayNode();
		ObjectNode languageNode = mapper.createObjectNode();
		languageNode.put(WoTConstants.KEYWORD_LANGUAGE, WoTConstants.EN_LANGUAGE);
		contextNode.add(WoTConstants.LD_CONTEXT);
		contextNode.add(mapper.createObjectNode().put("echonet","https://echonet.jp/"));
		contextNode.add(languageNode);
		rootNode.set(WoTConstants.KEYWORD_CONTEXT, contextNode);
		//id node
		rootNode.put(WoTConstants.KEYWORD_ID, "");
		rootNode.put("echonet:eoj", this.getCode());
		// title node
		rootNode.put(WoTConstants.KEYWORD_TITLE, this.getShortName());
		// titles node
		ObjectNode titlesNode = mapper.createObjectNode();
		titlesNode.put(WoTConstants.EN_LANGUAGE, this.getShortName());
		titlesNode.put(WoTConstants.JP_LANGUAGE , this.getShortJPName());
		rootNode.set(WoTConstants.KEYWORD_TITLES, titlesNode);
		//  description node
		rootNode.put(WoTConstants.KEYWORD_DESCRIPTION, this.getClassName().getEN());
		// descriptions node
		ObjectNode descriptions = mapper.createObjectNode();
		descriptions.put(WoTConstants.EN_LANGUAGE, this.getClassName().getEN());
		descriptions.put(WoTConstants.JP_LANGUAGE , this.getClassName().getJP());
		rootNode.set(WoTConstants.KEYWORD_DESCRIPTIONS, descriptions);		
		
		ObjectNode ppNode = mapper.createObjectNode();
		ObjectNode actionNode = mapper.createObjectNode();
		ObjectNode eventNode = mapper.createObjectNode();
		Collections.sort(properties, Collections.reverseOrder());
		for(ECHONETLiteProperty pp : properties) {
			if(!pp.isReservedForFutureUse() && !pp.isDELProperty() && !pp.isContainDELProperty()){
				if (pp.isINFOnly()) {
					eventNode.set(pp.getShortName(), pp.toEvent());
				} else if (pp.isSetOnly()) {
					actionNode.set(pp.getShortName(), pp.toAction());
				}
				else if(pp.isMCProperty()) {
					ppNode.set(pp.getShortName().replace("(MC)", ""), pp.toThingDescriptionJSONObjectNode());
				} else {
					ppNode.set(pp.getShortName(), pp.toThingDescriptionJSONObjectNode());
				} 	
			} 
		}
		
		if(ppNode.size() > 0)
			rootNode.set(WoTConstants.KEYWORD_PROPERTIES, ppNode);
		if(actionNode.size() > 0)
			rootNode.set(WoTConstants.KEYWORD_ACTIONS, actionNode);
		if(eventNode.size() > 0)
			rootNode.set(WoTConstants.KEYWORD_EVENTS, eventNode);
		return rootNode;
		
	}
	public void addProperties(List<ECHONETLiteProperty> properties) {
		List<ECHONETLiteProperty> ppToAdd = new ArrayList<ECHONETLiteProperty>();
		for(ECHONETLiteProperty newPP : properties) {
			for(ECHONETLiteProperty currentPP : this.getProperties()) {
				if(!newPP.getShortName().equals(currentPP.getShortName())) {
					ppToAdd.add(newPP);
				}
			}
		}
		if(ppToAdd.size() > 0) {
			for(ECHONETLiteProperty newPP : ppToAdd) {
				this.addAProperty(newPP);
			}
		}
	}
	public ObjectNode toFIWAREJSON() {
		return null;
	}
	
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	// For test
	public ObjectNode toDeviceName() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("Input", this.getClassName().getEN());
		rootNode.put("Output",StringUtils.join (
				StringUtils.splitByCharacterTypeCamelCase(this.getShortName()),' '));
	
		return rootNode;
		
	}
	
}