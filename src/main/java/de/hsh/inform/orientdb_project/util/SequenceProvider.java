package de.hsh.inform.orientdb_project.util;

public class SequenceProvider {

	private static String NOT_LABELED = "Not labeled";
	
	private long sequenceNumber;
	private String label;
	
	public SequenceProvider() {
		this(0, SequenceProvider.NOT_LABELED);
	}
	
	public SequenceProvider(long startValue) {
		this(startValue, SequenceProvider.NOT_LABELED);
	}
	
	public SequenceProvider(long startValue, String label) {
		this.sequenceNumber = startValue;
		this.label = label;
	}
	
	
	public long getNextNumber() {
		long result = this.sequenceNumber;
		this.sequenceNumber++;
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[SequenceProvider '");
		sb.append(this.label);
		sb.append("'] #=");
		sb.append(this.sequenceNumber);
		return sb.toString();
	}
	
}
