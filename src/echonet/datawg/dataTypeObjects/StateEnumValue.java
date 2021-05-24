package echonet.datawg.dataTypeObjects;

import echonet.datawg.echonetObjects.EnJAStatement;

public class StateEnumValue{
	public StateEnumValue() {
		
	}
	private String edt;
	private String name;
	private Boolean readOnly;
	private EnJAStatement description;
	public String getEdt() {
		return edt;
	}
	public void setEdt(String edt) {
		this.edt = edt;
	}
	public Boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public EnJAStatement getDescription() {
		return description;
	}
	public void setDescription(EnJAStatement description) {
		this.description = description;
	}

}
