package de.hsh.inform.orientdb_project.model;

import com.orientechnologies.orient.core.metadata.schema.OType;
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
	
	public Object[] getArguments() {
		Object[] arguments = {
				"ipAddress", this.ipAddress,
				"internal", this.internal,
		};
		return arguments;
	}

}
