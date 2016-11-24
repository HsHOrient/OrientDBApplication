package de.hsh.inform.orientdb_project.orientdb;

public class TcpConnection {
	
	public long startTs;
	public int startMs;
	
	public long endTs;
	public int endMs;
	
	public String sourceIp;
	public int sourcePort;
	
	public String targetIp;
	public int targetPort;
	
	public long volumeSourceToTarget;
	public long volumeTargetToSource;

	
	public void setStart(long ts, int ms) {
		this.startTs = ts;
		this.startMs = ms;
	}

	public void setEnd(long ts, int ms) {
		this.endTs = ts;
		this.endMs = ms;
	}
	
	public void addVolumeSourceToTarget(long vol) {
		this.volumeSourceToTarget += vol;
	}

	public void addVolumeTargetToSource(long vol) {
		this.volumeTargetToSource += vol;
	}

	public long getTotalVolume() {
		return this.volumeSourceToTarget + this.volumeTargetToSource;
	}

}
