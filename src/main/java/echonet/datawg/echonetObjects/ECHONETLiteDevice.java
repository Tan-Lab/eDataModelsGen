package echonet.datawg.echonetObjects;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import echonet.datawg.utils.eConstants;

public class ECHONETLiteDevice {
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
	public void setClassName(EnJAStatement className) {
		this.className = className;
	}
	public List<ECHONETLiteProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<ECHONETLiteProperty> properties) {
		this.properties = properties;
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
		descriptionNode.put(eConstants.KEYWORD_JA, className.getJa());
		descriptionNode.put(eConstants.KEYWORD_EN, className.getEn());
		rootNode.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
		
		ObjectNode ppNode = mapper.createObjectNode();
		Collections.sort(properties, Collections.reverseOrder());
		for(ECHONETLiteProperty pp : properties) {
			if(!pp.isReservedForFutureUse() && !pp.isDELProperty() && !pp.isContainDELProperty()){
				if(pp.isSetOnly()) {
					if(actionNode == null) {
						actionNode = mapper.createObjectNode();
					}
					actionNode.set(pp.getShortName(), pp.toDDWebAPIJSONObjectNodeReadOnly());
				} else {
					ppNode.set(pp.getShortName(), pp.toDDWebAPIJSONObjectNode());
				}	
			} 
		}
		rootNode.set(eConstants.KEYWORD_PROPERTIES, ppNode);
		if(actionNode !=null)
			rootNode.set(eConstants.KEYWORD_ACTIONS, actionNode);
		return rootNode;
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
	
}
