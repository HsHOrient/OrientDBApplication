package de.hsh.inform.orientdb_project.model;

import org.pcap4j.packet.TcpPacket;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class TcpPacketModel {

	public long ts;
	public int ms;
	
	public int sourcePort;
	public int targetPort;
	public int size;
	public int payloadSize;
	
	/*
	 * Static helper method to create type for itself in OrientDb
	 * Check this.getArguments() to ensure completeness.
	 */
	public static void createType(OrientGraphNoTx og) {
		OrientVertexType tcpPacketType = og.createVertexType("TcpPacket", "V");
		tcpPacketType.createProperty("sourcePort", OType.INTEGER);
		tcpPacketType.createProperty("targetPort", OType.INTEGER);
		tcpPacketType.createProperty("size", OType.INTEGER);
		tcpPacketType.createProperty("payloadSize", OType.INTEGER);
	}
	
	public TcpPacketModel(TcpPacket tcp, long ts, int ms) {
		this.ts = ts;
		this.ms = ms;
		this.sourcePort = tcp.getHeader().getSrcPort().valueAsInt();
		this.targetPort = tcp.getHeader().getDstPort().valueAsInt();
		this.size = tcp.getRawData().length;
		this.payloadSize = tcp.getRawData().length - tcp.getHeader().length();
	}
	
	public Object[] getArguments() {
		Object[] arguments = {
			"sourcePort", this.sourcePort,
			"targetPort", this.targetPort,
			"size", this.size,
			"payloadSize", this.payloadSize,
		};
		return arguments;
	}

}
