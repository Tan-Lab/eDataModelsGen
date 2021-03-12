package echonet.datawg.echonetObjects;


import java.util.List;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.utils.AccessRuleEnum;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.eConstants;

public class ECHONETLiteProperty implements Comparable<Object>{
	public ECHONETLiteProperty() {
		
	}
public ECHONETLiteProperty(String code) {
		this.code = code;
	}
	private String code;
	private String atomic;
	private SpecificationReleaseInformation validRelease;
	private EnJAStatement propertyName;
	private String shortName;
	private PropertyAccessRule accessRule;
	private List<DataType> data;
	private EnJAStatement note;
	
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
		
		rootNode.put(eConstants.KEYWORD_EPC, code);
		if(this.getAtomic() != null) {
			rootNode.put(Constants.KEYWORD_EPC_ATOMIC, this.getAtomic());
		}
		ObjectNode descriptionNode = mapper.createObjectNode();
		descriptionNode.put(eConstants.KEYWORD_JA, propertyName.getJa());
		descriptionNode.put(eConstants.KEYWORD_EN, propertyName.getEn());
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
		if(data.size() == 1) {
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
			note.put(eConstants.KEYWORD_JA, this.getNote().getJa());
			note.put(eConstants.KEYWORD_EN, this.getNote().getEn());
		}
		if(note!=null)
			rootNode.set(eConstants.KEYWORD_NOTE, note);
		
		return rootNode;
	}
	public ObjectNode toDDWebAPIJSONObjectNodeReadOnly() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		
		rootNode.put(eConstants.KEYWORD_EPC, code);
		ObjectNode descriptionNode = mapper.createObjectNode();
		descriptionNode.put(eConstants.KEYWORD_JA, propertyName.getJa());
		descriptionNode.put(eConstants.KEYWORD_EN, propertyName.getEn());
		rootNode.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
		rootNode.set(eConstants.KEYWORD_SCHEMA, mapper.createObjectNode());
		ObjectNode note = null;
		if(this.getNote() != null) {
			note = mapper.createObjectNode();
			note.put(eConstants.KEYWORD_JA, this.getNote().getJa());
			note.put(eConstants.KEYWORD_EN, this.getNote().getEn());
		}
		if(note!=null)
			rootNode.set(eConstants.KEYWORD_NOTE, note);
		
		return rootNode;
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
	public boolean isDELProperty() {
		boolean rs = true;
		if(this.getShortName()!=null && !this.getShortName().equalsIgnoreCase("DEL")) {
			rs = false;
		}	
		return rs;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
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
}
