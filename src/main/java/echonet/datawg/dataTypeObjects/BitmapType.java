package echonet.datawg.dataTypeObjects;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;

import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.eConstants;

public class BitmapType extends DataType{
	public BitmapType() {
		this.type = Constants.KEYWORD_BITMAP;
	}
	private Integer size;
	private List<Bitmap> bitmaps;
	
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public List<Bitmap> getBitmaps() {
		return bitmaps;
	}
	public void setBitmaps(List<Bitmap> bitmaps) {
		this.bitmaps = bitmaps;
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_OBJECT);
		
		ObjectNode bitmapProperty = mapper.createObjectNode();
		for(Bitmap bm : bitmaps) {
			bitmapProperty.set(bm.getName(), bm.toBitMapPPJSON());
		}
		if(bitmapProperty.size() != 0)
			rootNode.set(eConstants.KEYWORD_PROPERTIES, bitmapProperty);
		
		return rootNode;
	}
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_OBJECT);
		
		ObjectNode bitmapProperty = mapper.createObjectNode();
		for(Bitmap bm : bitmaps) {
			System.out.println(bm.getName());
			bitmapProperty.set(bm.getName(), bm.getValue().toFiwareSchemaJSON());
		}
		if(bitmapProperty.size() != 0)
			rootNode.set(eConstants.KEYWORD_PROPERTIES, bitmapProperty);
		return rootNode;
	}
	@Override
	public OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName) {
		return null;
	}
	@Override
	public ObjectNode toThingDescriptionDataSchema() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_OBJECT);
		
		ObjectNode bitmapProperty = mapper.createObjectNode();
		for(Bitmap bm : bitmaps) {
			bitmapProperty.set(bm.getName(), bm.toBitMapPPJSON());
		}
		if(bitmapProperty.size() != 0)
			rootNode.set(eConstants.KEYWORD_PROPERTIES, bitmapProperty);
		
		return rootNode;
	}

}
