package echonet.datawg.dataTypeObjects;

import org.apache.commons.text.CaseUtils;

import echonet.datawg.echonetObjects.EnJAStatement;

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

}
