package echonet.datawg.dataTypeObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jena.rdf.model.Literal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntDataProperty;
import com.github.owlcs.ontapi.jena.model.OntDataRange;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.vocabulary.XSD;

import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.SAREFConstants;
import echonet.datawg.utils.eConstants;

public class NumericType extends DataType{
	public NumericType() {
		this.type = "numericValue";
	}
	private List<NumericEnumValue> enumValue;
	private Integer size;

	public List<NumericEnumValue> getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(List<NumericEnumValue> enumValue) {
		this.enumValue = enumValue;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}


	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		System.out.println("------Exporting Data Type: " + this.type + " Name: " + this.name);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NUMBER);
		ArrayNode enumArrayNode  = mapper.createArrayNode();
		ArrayNode valueArrayNode  = mapper.createArrayNode();
		for(NumericEnumValue enumValue : enumValue) {
			// Enum Object
			enumArrayNode.add(enumValue.getNumericValue().floatValue());
			// Value Object
			ObjectNode anEntry = mapper.createObjectNode();
			anEntry.put(eConstants.KEYWORD_VALUE, enumValue.getNumericValue().floatValue());
			anEntry.put(eConstants.KEYWORD_EDT, enumValue.getEdt());
			
			valueArrayNode.add(anEntry);
		}
		rootNode.set(eConstants.KEYWORD_ENUM, enumArrayNode);
		rootNode.set(eConstants.KEYWORD_VALUES, valueArrayNode);
		
		return rootNode;
	}
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NUMBER);
		ArrayNode enumArrayNode  = mapper.createArrayNode();

		for(NumericEnumValue enumValue : enumValue) {
			// Enum Object
			enumArrayNode.add(enumValue.getNumericValue().floatValue());
			// Value Object
		}
		rootNode.set(eConstants.KEYWORD_ENUM, enumArrayNode);
		
		return rootNode;
	}


	@Override
	public OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName) {
		OntModel baseModel = owlHanlder.getBaseModel();
		OntClass baseMeasurementClass = baseModel.getOntClass(owlHanlder.SAREF_NS+ SAREFConstants.MEASUREMENT_CLASS);
		OntDataProperty baseHasValue = baseModel.getDataProperty(owlHanlder.SAREF_NS + SAREFConstants.HAS_VALUE_PP);
		//Create echonetMeasurement
		OntClass echonetMeasurement = baseModel.getOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
		
		if(echonetMeasurement == null) {
			echonetMeasurement = baseModel.createOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
			echonetMeasurement.addSuperClass(baseMeasurementClass);
			echonetMeasurement.addSuperClass(baseModel.createDataMinCardinality(baseHasValue, 1, valueRestriction(owlHanlder)));
		}
		return echonetMeasurement;
	}
	public OntDataRange valueRestriction(SarefOntologyParser owlHanlder) {
		OntModel baseModel = owlHanlder.getBaseModel();
		OntDataRange.Named valueType = null;
		Collection<Literal> literals = new ArrayList<Literal>();
		for(NumericEnumValue value:enumValue) {
			if(value.getNumericValue() > 1) {
				valueType =  baseModel.getDatatype(XSD.xint);
			} else {
				valueType =  baseModel.getDatatype(XSD.xfloat);
			}
			literals.add(valueType.createLiteral(value.getNumericValue()));
		}
		return baseModel.createDataOneOf(literals);
	}
	private String toTypeURI(String ppName) {
		return "has" + ppName + "Measurement" ;
	}

	@Override
	public ObjectNode toThingDescriptionDataSchema() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NUMBER);
		ArrayNode enumArrayNode  = mapper.createArrayNode();
		for(NumericEnumValue enumValue : enumValue) {
			// Enum Object
			enumArrayNode.add(enumValue.getNumericValue().floatValue());
		}
		rootNode.set(eConstants.KEYWORD_ENUM, enumArrayNode);
		
		return rootNode;
	}

}
