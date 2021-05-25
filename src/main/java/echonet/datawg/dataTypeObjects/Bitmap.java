package echonet.datawg.dataTypeObjects;

import org.apache.commons.text.CaseUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.utils.eConstants;

public class Bitmap {
	public Bitmap() {
		
	}
	public String getName() {
		return name;
	}
	public String getNameCamel() {
		return CaseUtils.toCamelCase(getName(), true, ' ');
	}
	public void setName(String name) {
		this.name = name;
	}
	public EnJAStatement getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(EnJAStatement descriptions) {
		this.descriptions = descriptions;
	}
	public BitmapPosition getPosition() {
		return position;
	}
	public void setPosition(BitmapPosition position) {
		this.position = position;
	}
	public DataType getValue() {
		return value;
	}
	public void setValue(DataType value) {
		this.value = value;
	}
	private String name;
	private EnJAStatement descriptions;
	private BitmapPosition position;
	private DataType value;
	public ObjectNode toBitMapPPJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		ObjectNode bitmapDescription = mapper.createObjectNode();
		ObjectNode descriptionNode = mapper.createObjectNode();
		descriptionNode.put(eConstants.KEYWORD_JA, getDescriptions().getJa());
		descriptionNode.put(eConstants.KEYWORD_EN, getDescriptions().getEn());	
		bitmapDescription.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
		rootNode.setAll(bitmapDescription);
		rootNode.setAll(getValue().toWebAPIDeviceDescription());
		return rootNode;
	}

}
