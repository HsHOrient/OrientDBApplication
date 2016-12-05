package de.hsh.inform.orientdb_project.model;

import java.net.Inet4Address;

import org.pcap4j.packet.IpV4Packet;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class Ipv4PacketModel {

	public long ts;
	public int ms;
	
	public String sourceIp;
	public String targetIp;
	public int size;
	public int payloadSize;

	/*
	 * Static helper method to create type for itself in OrientDb
	 * Check this.getArguments() to ensure completeness.
	 */
	public static void createType(OrientGraphNoTx og) {
		OrientVertexType ipPacketType = og.createVertexType("IpPacket", "V");
		ipPacketType.createProperty("sourceIp", OType.STRING);
		ipPacketType.createProperty("targetIp", OType.STRING);
		ipPacketType.createProperty("size", OType.INTEGER);
		ipPacketType.createProperty("payloadSize", OType.INTEGER);		
	}
	
	public Ipv4PacketModel(IpV4Packet ipv4, Inet4Address sourceIp, Inet4Address targetIp, long ts, int ms) {
		this.ts = ts;
		this.ms = ms;
		this.sourceIp = sourceIp.toString().split("/")[1];
		this.targetIp = targetIp.toString().split("/")[1];
		this.size = ipv4.getRawData().length;
		this.payloadSize = ipv4.getRawData().length - ipv4.getHeader().length();
		
	}
	
	public Object[] getArguments() {
		Object[] arguments = {
			"sourceIp", this.sourceIp,
			"targetIp", this.targetIp,
			"size", this.size,
			"payloadSize", this.payloadSize,
		};
		return arguments;
	}

}
