package de.hsh.inform.orientdb_project.model;

import org.pcap4j.packet.UdpPacket;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class UdpPacketModel implements Model {

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
		OrientVertexType udpPacketType = og.createVertexType("UdpPacket", "V");
		udpPacketType.createProperty("sourcePort", OType.INTEGER);
		udpPacketType.createProperty("targetPort", OType.INTEGER);
		udpPacketType.createProperty("size", OType.INTEGER);
		udpPacketType.createProperty("payloadSize", OType.INTEGER);
	}
	
	public UdpPacketModel(UdpPacket udp, long ts, int ms) {
			this.ts = ts;
			this.ms = ms;
			this.sourcePort = udp.getHeader().getSrcPort().valueAsInt();
			this.targetPort = udp.getHeader().getDstPort().valueAsInt();
			this.size = udp.getRawData().length;
			this.payloadSize = udp.getRawData().length - udp.getHeader().length();
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
