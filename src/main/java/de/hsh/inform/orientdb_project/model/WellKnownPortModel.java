package de.hsh.inform.orientdb_project.model;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class WellKnownPortModel implements Model {

	public int port;
	public String description;
	
	/*
	 * Static helper method to create type for itself in OrientDb
	 * Check this.getArguments() to ensure completeness.
	 */
	public static void createType(OrientGraphNoTx og) {
		OrientVertexType portType = og.createVertexType("WellKnownPort", "V");
		portType.createProperty("port", OType.INTEGER);
		portType.createProperty("description", OType.STRING);
	}
	
	public WellKnownPortModel(int port, String description) {
		this.port = port;
		this.description = description;
	}
	
	public Object[] getArguments() {
		Object[] arguments = {
				"port", this.port,
				"description", this.description,
		};
		return arguments;
	}

}
