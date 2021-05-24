package echonet.datawg.echonetObjects;

public class SpecificationReleaseInformation {
	public SpecificationReleaseInformation() {
		
	}
	public SpecificationReleaseInformation(String from, String to) {
		setFrom(from);
		setTo(to);
	}
	private String from;
	private String to;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}

}
