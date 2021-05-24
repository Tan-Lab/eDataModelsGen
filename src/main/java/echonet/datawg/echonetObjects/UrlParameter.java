package echonet.datawg.echonetObjects;

import java.util.List;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.eConstants;

public class UrlParameter {
	public UrlParameter() {
		
	}
	private String name;
	private EnJAStatement des;
	private List<DataType> type;
	private boolean required;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public EnJAStatement getDes() {
		return des;
	}
	public void setDes(EnJAStatement des) {
		this.des = des;
	}
	public List<DataType> getType() {
		return type;
	}
	public void setType(List<DataType> type) {
		this.type = type;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public ObjectNode toURLNode() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		
		if (this.getType().size() == 1){
			ObjectNode descNode = toDescription();
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
			rootNode.setAll(getType().get(0).toWebAPIDeviceDescription());	
			rootNode.setAll(toRequire());
			
		} else if(this.getType().size() > 1) {
			ObjectNode descNode = toDescription();
			if(descNode != null) {
				rootNode.set(Constants.KEYWORD_DESCRIPTIONS, descNode);
			}
			ArrayNode oneOf = mapper.createArrayNode();
			for(DataType type : this.getType()) {
				oneOf.add(type.toWebAPIDeviceDescription());
			}
			ObjectNode multipleTypePropertyNode = mapper.createObjectNode();
			multipleTypePropertyNode.set(eConstants.KEYWORD_ONEOF, oneOf);

			rootNode.setAll(multipleTypePropertyNode);
			rootNode.setAll(toRequire());
		} 
		return rootNode;
	}
	public ObjectNode toDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rs = null;
		if(this.getDes()!=null) {
			rs = mapper.createObjectNode();
			rs.put(Constants.KEYWORD_JA, this.getDes().getJa());
			rs.put(Constants.KEYWORD_EN, this.getDes().getEn());
		}
		return rs;
	}
	public ObjectNode toRequire() {
		ObjectMapper mapper = new ObjectMapper();
		 return mapper.createObjectNode().put(Constants.KEYWORD_REQUIRED, isRequired());
	}
}
