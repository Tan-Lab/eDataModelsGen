package echonet.datawg.dataTypeObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.text.CaseUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntIndividual;
import com.github.owlcs.ontapi.jena.model.OntModel;

import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.eConstants;

public class StateType extends DataType{
	public StateType(){
		this.type = Constants.KEYWORD_STATE;
	}
	public StateType(String name){
		this.type = Constants.KEYWORD_STATE;
		this.name = name;
	} 
	private Integer size;
	private List<StateEnumValue> enumValue;
	
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public List<StateEnumValue> getENumValue() {
		return enumValue;
	}
	public void setEnumValue(List<StateEnumValue> enumValue) {
		this.enumValue = enumValue;
	}
	
	private ObjectNode toStringType() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_STRING);
		ArrayNode enumArrayNode  = mapper.createArrayNode();
		ArrayNode valueArrayNode  = mapper.createArrayNode();
		for(StateEnumValue enumValue : enumValue) {
			ObjectNode anEntryNode = mapper.createObjectNode();
			// Enum Object
			String enumString = enumValue.getName();
			enumArrayNode.add(enumString);
			// Value Object
			anEntryNode.put(eConstants.KEYWORD_VALUE,enumString);
			ObjectNode descriptionNode = mapper.createObjectNode();
			descriptionNode.put(eConstants.KEYWORD_JA, enumValue.getDescription().getJP());
			descriptionNode.put(eConstants.KEYWORD_EN, enumValue.getDescription().getEN());
			anEntryNode.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
			
			anEntryNode.put(eConstants.KEYWORD_EDT, enumValue.getEdt());
			
			valueArrayNode.add(anEntryNode);
		}
		rootNode.set(eConstants.KEYWORD_ENUM, enumArrayNode);
		rootNode.set(eConstants.KEYWORD_VALUES, valueArrayNode);
		return rootNode;
	}
	private ObjectNode toStringTypeTD() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_STRING);
		ArrayNode enumArrayNode  = mapper.createArrayNode();
		for(StateEnumValue enumValue : enumValue) {
			enumArrayNode.add(enumValue.getName());
		}
		rootNode.set(eConstants.KEYWORD_ENUM, enumArrayNode);
		return rootNode;
	}
	private ObjectNode toFIWAREStringEnum() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_STRING);
		ArrayNode enumArrayNode  = mapper.createArrayNode();
		for(StateEnumValue enumValue : enumValue) {
			String enumString = CaseUtils.toCamelCase(enumValue.getDescription().getEN(), false, ' ').trim();
			enumArrayNode.add(enumString);
			// Value Object

		}
		rootNode.set(eConstants.KEYWORD_ENUM, enumArrayNode);
		return rootNode;
	}
	private ObjectNode toBooleanType() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_BOOLEAN);
		
		ArrayNode valueArrayNode  = mapper.createArrayNode();
		for(StateEnumValue enumValue : enumValue) {
			ObjectNode anEntry = mapper.createObjectNode();
			if(enumValue.getName().equalsIgnoreCase("true")) {
				anEntry.put(eConstants.KEYWORD_VALUE, true);
			} else {
				anEntry.put(eConstants.KEYWORD_VALUE, false);
			}
			ObjectNode descriptionNode = mapper.createObjectNode();
			descriptionNode.put(eConstants.KEYWORD_JA, enumValue.getDescription().getJP());
			descriptionNode.put(eConstants.KEYWORD_EN, enumValue.getDescription().getEN());
			
			anEntry.set(eConstants.KEYWORD_DESCRIPTIONS, descriptionNode);
			anEntry.put(eConstants.KEYWORD_EDT, enumValue.getEdt());
			
			valueArrayNode.add(anEntry);
		}
		rootNode.set(eConstants.KEYWORD_VALUES, valueArrayNode);
		return rootNode;
	}
	private ObjectNode toBooleanTypeTD() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_BOOLEAN);
		return rootNode;
	}
	private ObjectNode toFIWAREBooleanType() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_BOOLEAN);
		
		ArrayNode valueArrayNode  = mapper.createArrayNode();
		for(StateEnumValue enumValue : enumValue) {

			if(enumValue.getEdt().equalsIgnoreCase("0x30") ||
			   enumValue.getEdt().equalsIgnoreCase("0x41") ||
			   enumValue.getDescription().getEN().equalsIgnoreCase("ON")) {
				valueArrayNode.add(true);
			} else {
				valueArrayNode.add(false);
			}
		}
		rootNode.set(eConstants.KEYWORD_VALUES, valueArrayNode);
		return rootNode;
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		ObjectNode rootNode  = null;
		boolean isBooleanType = false;
		for(StateEnumValue e : this.getENumValue()) {
			if(e.getName().equalsIgnoreCase("true") || e.getName().equalsIgnoreCase("false")) {
				isBooleanType = true;
			}
		}
		if(isBooleanType) {
			rootNode = toBooleanType();
		} else {
			rootNode = toStringType();
		}
		return rootNode;
	}
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectNode rootNode  = null;
		if(this.enumValue.size() == 2) {
			rootNode = toFIWAREBooleanType();
		} else {
			rootNode = toFIWAREStringEnum();
		}
		return rootNode;
	}
	@Override
	public OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName) {
		OntModel baseModel = owlHanlder.getBaseModel();
		
		Collection<OntIndividual > individuals = new ArrayList<OntIndividual>();
		for(StateEnumValue state:enumValue) {
			String individualName = CaseUtils.toCamelCase(state.getName(), true, ' ');
			individuals.add(baseModel.createIndividual(owlHanlder.ECHONET_NS + individualName));
		}
		OntClass.OneOf states = baseModel.createObjectOneOf(individuals);
		
		return states;
	}
	@Override
	public ObjectNode toThingDescriptionDataSchema() {
		ObjectNode rootNode  = null;
		boolean isBooleanType = false;
		for(StateEnumValue e : this.getENumValue()) {
			if(e.getName().equalsIgnoreCase("true") || e.getName().equalsIgnoreCase("false")) {
				isBooleanType = true;
			}
		}
		if(isBooleanType) {
			rootNode = toBooleanTypeTD();
		} else {
			rootNode = toStringTypeTD();
		}
		return rootNode;
	}

}
