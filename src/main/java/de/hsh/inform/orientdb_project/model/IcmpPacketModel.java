package de.hsh.inform.orientdb_project.model;

import org.pcap4j.packet.IcmpV4CommonPacket;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class IcmpPacketModel {

	public long ts;
	public int ms;
	
	public int payloadSize;
	public int size;
	
	/*
	 * Static helper method to create type for itself in OrientDb
	 * Check this.getArguments() to ensure completeness.
	 */
	public static void createType(OrientGraphNoTx og) {
		OrientVertexType icmpPacketType = og.createVertexType("IcmpPacket", "V");
		icmpPacketType.createProperty("size", OType.INTEGER);
		icmpPacketType.createProperty("payloadSize", OType.INTEGER);		
	}

	public IcmpPacketModel(IcmpV4CommonPacket icmp, long ts, int ms) {
		this.ts = ts;
		this.ms = ms;
		this.size = icmp.getRawData().length;
		this.payloadSize = icmp.getRawData().length - icmp.getHeader().length();
	}
	
	public Object[] getArguments() {
		Object[] arguments = {
			"size", this.size,
			"payloadSize", this.payloadSize,
		};
		return arguments;
	}
	
}
