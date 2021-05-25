package echonet.datawg.echonetObjects;

import java.util.List;

import echonet.datawg.dataTypeObjects.DataType;

public class EPCManualCode {
	public EPCManualCode() {
		// TODO Auto-generated constructor stub
	}
	private String epcCode;
	private String action;
	private List<DataType> type;
	private List<UrlParameter> paremeter;
	private ECHONETLiteProperty pp;
	private EnJAStatement note;
	private EnJAStatement ppName;
	private PropertyAccessRule accessRule;
	public String getEpc() {
		return epcCode;
	}
	public void setEpc(String epc) {
		this.epcCode = epc;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<DataType> getType() {
		return type;
	}
	public void setType(List<DataType> type) {
		this.type = type;
	}
	public List<UrlParameter> getParemeter() {
		return paremeter;
	}
	public void setParemeter(List<UrlParameter> paremeter) {
		this.paremeter = paremeter;
	}
	public ECHONETLiteProperty getPp() {
		return pp;
	}
	public void setPp(ECHONETLiteProperty pp) {
		this.pp = pp;
	}
	public EnJAStatement getNote() {
		return note;
	}
	public void setNote(EnJAStatement note) {
		this.note = note;
	}
	public EnJAStatement getPpName() {
		return ppName;
	}
	public void setPpName(EnJAStatement ppName) {
		this.ppName = ppName;
	}
	public PropertyAccessRule getAccessRule() {
		return accessRule;
	}
	public void setAccessRule(PropertyAccessRule accessRule) {
		this.accessRule = accessRule;
	}

}

