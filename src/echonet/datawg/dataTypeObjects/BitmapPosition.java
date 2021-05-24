package echonet.datawg.dataTypeObjects;

public class BitmapPosition {
	public BitmapPosition() {
		
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getBitMask() {
		return bitMask;
	}
	public void setBitMask(String bitMask) {
		this.bitMask = bitMask;
	}
	private Integer index;
	private String bitMask;

}
