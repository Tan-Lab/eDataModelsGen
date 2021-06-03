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

public class NullType extends DataType{
	public NullType() {
		this.type= Constants.KEYWORD_RAW;
	}
	public NullType(String name) {
		this.type= Constants.KEYWORD_RAW;
		this.name = name;
	}
	private String edt;

	

	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NULL);
		if(this.getEdt()!= null ) {
			rootNode.put(eConstants.KEYWORD_EDT, this.getEdt());
		}
		
		return rootNode;
	}
	
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NULL);
		
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
		rootNode.put(eConstants.KEYWORD_TYPE, Constants.TYPE_NULL);
		
		return rootNode;
	}
	public String getEdt() {
		return edt;
	}
	public void setEdt(String edt) {
		this.edt = edt;
	}

}
