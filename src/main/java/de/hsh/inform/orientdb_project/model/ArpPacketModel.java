package de.hsh.inform.orientdb_project.model;

import org.pcap4j.packet.ArpPacket;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

// TODO: Not finished?
public class ArpPacketModel {
	
	public long ts;
	public int ms;
	
	public int size;
	public int payloadSize;

	/*
	 * Static helper method to create type for itself in OrientDb
	 * Check this.getArguments() to ensure completeness.
	 */
	public static void createType(OrientGraphNoTx og) {
		OrientVertexType arpPacketType = og.createVertexType("ArpPacket", "V");
		arpPacketType.createProperty("askedForIp", OType.STRING);
		arpPacketType.createProperty("hasIp", OType.STRING);
		arpPacketType.createProperty("size", OType.INTEGER);
		arpPacketType.createProperty("payloadSize", OType.INTEGER);
	}
	
	public ArpPacketModel(ArpPacket arp, long ts, int ms) {
		this.ts = ts;
		this.ms = ms;
		this.size = arp.getRawData().length;
		this.payloadSize = arp.getRawData().length - arp.getHeader().length();
	}
	
	public Object[] getArguments() {
		Object[] arguments = {
			"size", this.size,
			"payloadSize", this.payloadSize,
		};
		return arguments;
	}

}
