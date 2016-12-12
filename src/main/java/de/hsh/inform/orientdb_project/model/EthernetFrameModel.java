package de.hsh.inform.orientdb_project.model;

import org.pcap4j.packet.EthernetPacket;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class EthernetFrameModel {

	public long ts;
	public int ms;
	
	public String sourceMac;
	public String targetMac;
	public byte[] rawData;
	public int size;
	public int payloadSize;
	public long timestamp;
	public int microseconds;
	
	/*
	 * Static helper method to create type for itself in OrientDb
	 * Check this.getArguments() to ensure completeness.
	 */
	public static void createType(OrientGraphNoTx og) {
		OrientVertexType ethernetFrameType = og.createVertexType("EthernetFrame", "V");
		ethernetFrameType.createProperty("sourceMac", OType.STRING);
		ethernetFrameType.createProperty("targetMac", OType.STRING);
		ethernetFrameType.createProperty("rawData", OType.BINARY);
		ethernetFrameType.createProperty("size", OType.INTEGER);
		ethernetFrameType.createProperty("payloadSize", OType.INTEGER);
		ethernetFrameType.createProperty("timestamp", OType.LONG);
		ethernetFrameType.createProperty("microseconds", OType.INTEGER);
	}
	
	public EthernetFrameModel(EthernetPacket ether, long ts, int ms) {
		this.ts = ts;
		this.ms = ms;
		this.sourceMac = ether.getHeader().getSrcAddr().toString();
		this.targetMac = ether.getHeader().getDstAddr().toString();
		this.rawData = ether.getRawData();
		this.size = ether.getRawData().length;
		this.payloadSize = ether.getRawData().length - ether.getHeader().length();
		this.timestamp = ts;
		this.microseconds = ms;
	}
	
	public EthernetFrameModel(Vertex v) {
		this.ts = v.getProperty("timestamp");
		this.ms = v.getProperty("microseconds");
		this.sourceMac = v.getProperty("sourceMac");
		this.targetMac = v.getProperty("targetMac");
		this.rawData = v.getProperty("rawData");
		this.size = v.getProperty("size");
		this.payloadSize = v.getProperty("payloadSize");
		this.timestamp = ts;
		this.microseconds = ms;
	}

	public Object[] getArguments() {
		Object[] arguments = {
			"sourceMac", this.sourceMac,
			"targetMac", this.targetMac,
		    "rawData", this.rawData,
		    "size", this.size,
		    "payloadSize", this.payloadSize,
		    "timestamp", this.timestamp,
		    "microseconds", this.microseconds,
		};
		return arguments;
	}
	
	
}
