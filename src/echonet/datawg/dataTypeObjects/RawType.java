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

public class RawType extends DataType{
	public RawType() {
		this.type= Constants.KEYWORD_RAW;
	}
	public RawType(String name) {
		this.type= Constants.KEYWORD_RAW;
		this.name = name;
	}
	private int minSize;
	private int maxSize;

	
	public int getMinSize() {
		return minSize;
	}
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_STRING);
		
		return rootNode;
	}
	
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_STRING);
		
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

}
