package echonet.datawg.echonetObjects;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpecificationMetaData {
	private Date date;
	private String release;
	private String version;
	private EnJAStatement note;
	@JsonProperty("Copyright")
	private String copyright;
	public SpecificationMetaData() {
		
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public EnJAStatement getNote() {
		return note;
	}
	public void setNote(EnJAStatement note) {
		this.note = note;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

}
