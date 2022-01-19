package echonet.datawg.dataTypeObjects;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntDataProperty;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.vocabulary.XSD;

import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.SAREFConstants;
import echonet.datawg.utils.eConstants;

public class BooleanType extends DataType{
	public BooleanType() {
		this.type= Constants.TYPE_BOOLEAN;
	}
	public BooleanType(String name) {
		this.type= Constants.TYPE_BOOLEAN;
		this.name = name;
	}

	
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		System.out.println("------Exporting Data Type: " + this.type + " Name: " + this.name);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, Constants.TYPE_BOOLEAN);
		
		return rootNode;
	}
	
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, Constants.TYPE_BOOLEAN);
		
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
			echonetMeasurement.addSuperClass(baseModel.createDataMinCardinality(baseHasValue, 1, baseModel.getDatatype(XSD.xstring)));
		}
		return echonetMeasurement;
	}
	private String toTypeURI(String ppName) {
		return "has" + ppName + "Measurement" ;
	}
	@Override
	public ObjectNode toThingDescriptionDataSchema() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, Constants.TYPE_BOOLEAN);
		
		return rootNode;
	}

}
