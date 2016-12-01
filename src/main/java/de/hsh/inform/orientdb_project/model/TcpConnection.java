package de.hsh.inform.orientdb_project.model;

import java.util.LinkedList;

import org.pcap4j.packet.TcpPacket;

import com.tinkerpop.blueprints.Vertex;


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

	public LinkedList<Vertex> knownTcpPacketVertices;
	
	
	public TcpConnection(TcpPacket tcp, String sourceIp, String targetIp, long ts, int ms) {
		this.setStart(ts, ms);
		this.setEnd(ts, ms);
		this.sourceIp = sourceIp;
		this.sourcePort = tcp.getHeader().getSrcPort().valueAsInt();
		this.targetIp = targetIp;
		this.targetPort = tcp.getHeader().getDstPort().valueAsInt();
		this.knownTcpPacketVertices = new LinkedList<Vertex>();
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
	
	public void addKnownTcpPacketVertex(Vertex tcpPacketVertex) {
		this.knownTcpPacketVertices.add(tcpPacketVertex);
	}

	public Object[] getArguments() {
		Object[] arguments = {
				"startTs", this.startTs,
				"startMs", this.startMs,
				"endTs", this.endTs,
				"endMs", this.endMs,
				"sourceIp", this.sourceIp,
				"sourcePort", this.sourcePort,
				"targetIp", this.targetIp,
				"targetPort", this.targetPort,
				"volumeSourceToTarget", this.volumeSourceToTarget,
				"volumeTargetToSource", this.volumeTargetToSource,
				"totalVolume", this.getTotalVolume(),
			};
		return arguments;
	}

}
