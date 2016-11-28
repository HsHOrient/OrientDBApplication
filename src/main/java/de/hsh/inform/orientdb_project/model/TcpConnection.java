package de.hsh.inform.orientdb_project.model;

import org.pcap4j.packet.TcpPacket;

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

	
	public TcpConnection(TcpPacket tcp, String sourceIp, String targetIp, long ts, int ms) {
		this.setStart(ts, ms);
		this.setEnd(ts, ms);
		this.sourceIp = sourceIp;
		this.sourcePort = tcp.getHeader().getSrcPort().valueAsInt();
		this.targetIp = targetIp;
		this.targetPort = tcp.getHeader().getDstPort().valueAsInt();
	}

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
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(this.startTs);
		sb.append(".");
		sb.append(this.startMs);
		sb.append("] ");
		sb.append(this.sourceIp);
		sb.append(":");
		sb.append(this.sourcePort);
		sb.append(" -> ");
		sb.append(this.targetIp);
		sb.append(":");
		sb.append(this.targetPort);
		sb.append(" -- ");
		sb.append(this.getTotalVolume());
		return sb.toString();
	}

}
