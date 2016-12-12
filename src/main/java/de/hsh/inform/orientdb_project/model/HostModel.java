package de.hsh.inform.orientdb_project.model;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class HostModel {

	public String ipAddress;
	public boolean internal;
	
	/*
	 * Static helper method to create type for itself in OrientDb
	 * Check this.getArguments() to ensure completeness.
	 */
	public static void createType(OrientGraphNoTx og) {
		OrientVertexType hostType = og.createVertexType("Host", "V");
		hostType.createProperty("ipAddress", OType.STRING);
		hostType.createProperty("internal", OType.BOOLEAN);
	}
	
	public HostModel(String ipAddress, boolean internal) {
		this.ipAddress = ipAddress;
		this.internal = internal;
	}
	
	public HostModel(Vertex v) {
		this.internal = v.getProperty("internal");
		this.ipAddress = v.getProperty("ipAddress");
	}

	public Object[] getArguments() {
		Object[] arguments = {
				"ipAddress", this.ipAddress,
				"internal", this.internal,
		};
		return arguments;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[Host ipAddress=");
		sb.append(this.ipAddress);
		sb.append(", internal=");
		sb.append(this.internal);
		sb.append("]");
		return sb.toString();
	}

}
