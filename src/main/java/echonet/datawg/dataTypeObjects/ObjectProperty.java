package echonet.datawg.dataTypeObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.text.CaseUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.eConstants;

public class ObjectProperty {
	static Logger LOGGER = Logger.getLogger(ObjectProperty.class.getName());
	public ObjectProperty() {
		
	}
	private String name;
	private List<DataType> element;
	private EnJAStatement description;
	private EnJAStatement note;
	public String getName() {
		return name;
	}
	public void setDataType(DataType dataType) {
		if(this.element == null) {
			element = new ArrayList<DataType>();
		}
		this.element.add(dataType);
	}
	public String toPropertyNameLowerCamel() {
		return CaseUtils.toCamelCase(name, false, ' ');
	}
	public String toPropertyNameCamel() {
		return CaseUtils.toCamelCase(name, true, ' ');
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DataType> getElement() {
		return element;
	}
	public void setElement(List<DataType> element) {
		this.element = element;
	}
	public EnJAStatement getDescription() {
		return description;
	}
	public void setDescription(EnJAStatement description) {
		this.description = description;
	}
	public ObjectNode toDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rs = null;
		if(this.getDescription()!=null) {
			rs = mapper.createObjectNode();
			rs.put(Constants.KEYWORD_JA, this.getDescription().getJP());
			rs.put(Constants.KEYWORD_EN, this.getDescription().getEN());
		}
		return rs;
	}
	public ObjectNode toNote() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rs = null;
		if(this.getNote()!=null) {
			rs = mapper.createObjectNode();
			rs.put(Constants.KEYWORD_JA, this.getNote().getJP());
			rs.put(Constants.KEYWORD_EN, this.getNote().getEN());
		}
		return rs;
	}
	public ObjectNode toJSONObjDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		ObjectNode descNode = toDescription();
		ObjectNode noteNode = toNote();
		if (this.getElement().size() == 1){
			
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
			DataType type = getElement().get(0);
			if(type != null) {
				rootNode.setAll(type.toWebAPIDeviceDescription());
			} else {
				LOGGER.severe(String.format("******Could not load data type of object type of %s", this.name));
				System.exit(1);
			}
			
			if( noteNode != null) {
				rootNode.set(Constants.KEYWORD_NOTE, noteNode);
			}
		} else if(this.getElement().size() > 1) {
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
			
			ArrayNode oneOf = mapper.createArrayNode();
			for(DataType type : this.getElement()) {
				if(type != null) {
					oneOf.add(type.toWebAPIDeviceDescription());
				} else {
					LOGGER.severe(String.format("******Could not load data type of object type of %s", this.name));
					System.exit(1);
				}
				
			}
			ObjectNode multipleTypePropertyNode = mapper.createObjectNode();
			multipleTypePropertyNode.set(eConstants.KEYWORD_ONEOF, oneOf);

			rootNode.setAll(multipleTypePropertyNode);
			if( noteNode != null) {
				rootNode.set(Constants.KEYWORD_NOTE, noteNode);
			}
		} 
		
		return rootNode;
	}
	public ObjectNode toPPDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		ObjectNode descNode = toDescription();
		ObjectNode noteNode = toNote();
		if (this.getElement().size() == 1){
			
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
			rootNode.setAll(getElement().get(0).toWebAPIDeviceDescription());
			if( noteNode != null) {
				rootNode.set(Constants.KEYWORD_NOTE, noteNode);
			}
		} else if(this.getElement().size() > 1) {
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
			
			ArrayNode oneOf = mapper.createArrayNode();
			for(DataType type : this.getElement()) {
				oneOf.add(type.toWebAPIDeviceDescription());
			}
			ObjectNode multipleTypePropertyNode = mapper.createObjectNode();
			multipleTypePropertyNode.set(eConstants.KEYWORD_ONEOF, oneOf);

			rootNode.setAll(multipleTypePropertyNode);
			if( noteNode != null) {
				rootNode.set(Constants.KEYWORD_NOTE, noteNode);
			}
		} 
		ObjectNode rs = mapper.createObjectNode();
		rs.set(this.getName(), rootNode);
		return rs;
	}
	public ObjectNode toJSONTDPropertySchema() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		ObjectNode descNode = toDescription();
		if (this.getElement().size() == 1){
			rootNode.setAll(getElement().get(0).toThingDescriptionDataSchema());	
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
		} else if(this.getElement().size() > 1) {
			ArrayNode oneOf = mapper.createArrayNode();
			for(DataType type : this.getElement()) {
				oneOf.add(type.toThingDescriptionDataSchema());
			}
			ObjectNode multipleTypePropertyNode = mapper.createObjectNode();
			multipleTypePropertyNode.set(eConstants.KEYWORD_ONEOF, oneOf);

			rootNode.setAll(multipleTypePropertyNode);
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
		} 
		
		return rootNode;
	}
	public boolean isReservedForFutureUse() {
		boolean rs = true;
		if(this.getName()!=null && !this.getName().equalsIgnoreCase("RFU"))
			rs = false;
		return rs;
	}
	public boolean isDELProperty() {
		boolean rs = true;
		if(this.getName()!=null && !this.getName().contains("(DEL)"))
			rs = false;
		return rs;
	}
	public boolean isDELProperty2() {
		boolean rs = true;
		if(	this.getName()!=null && !this.getName().equals("DEL"))
			rs = false;
		return rs;
	}
	public EnJAStatement getNote() {
		return note;
	}
	public void setNote(EnJAStatement note) {
		this.note = note;
	}

}