package echonet.datawg.dataTypeObjects;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;

import echonet.datawg.inputParsers.SarefOntologyParser;

public abstract class DataType {
	String name;
	String type;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public abstract OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName);
	public abstract ObjectNode toFiwareSchemaJSON();
	public abstract ObjectNode toWebAPIDeviceDescription();
	public abstract ObjectNode toThingDescriptionDataSchema();

}
