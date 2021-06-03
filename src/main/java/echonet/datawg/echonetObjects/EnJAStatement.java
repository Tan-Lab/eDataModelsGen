package echonet.datawg.echonetObjects;


public class EnJAStatement {
	public EnJAStatement() {
		
	}
	public EnJAStatement(String en, String ja) {
		setEN(en);
		setJP(ja);
	}
	private String ja;
	private String en;
	public String getJP() {
		return ja;
	}
	public void setJP(String ja) {
		this.ja = ja;
	}
	public String getEN() {
		return en;
	}
	public void setEN(String en) {
		this.en = en;
	}
}
