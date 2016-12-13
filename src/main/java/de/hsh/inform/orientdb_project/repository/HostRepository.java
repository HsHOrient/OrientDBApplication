package de.hsh.inform.orientdb_project.repository;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.HostModel;

public class HostRepository {

	private OrientGraphNoTx ogf;
	
	public HostRepository(OrientGraphNoTx ogf) {
		this.ogf = ogf;
	}
	
	public List<HostModel> findByConnectionsTo(String ipAddress, int port) {
		String sql = "" +
		"SELECT EXPAND(out('isTargetHostFor')[targetPort=" + port + "].out('hasSourceHost')) " +
		"FROM Host WHERE ipAddress = '" + ipAddress + "';";
		
		@SuppressWarnings("unchecked") // We know.
		Iterable<Vertex> vertices = (Iterable<Vertex>) this.ogf.command(new OCommandSQL(sql)).execute();
		return this.getListFromVertices(vertices);
	}

	public List<HostModel> findAllByConnectionsToOutsideHosts() {
		String sql = "" +
		"SELECT EXPAND(DISTINCT(out)) FROM (SELECT out('hasSourceHost') AS out FROM TcpConnection WHERE out('hasTargetHost').internal = false);";
		@SuppressWarnings("unchecked") // We know.
		Iterable<Vertex> vertices = (Iterable<Vertex>) this.ogf.command(new OCommandSQL(sql)).execute();
		return this.getListFromVertices(vertices);
	}

	
	public List<HostModel> findAllByIncomingConnectionOnWellKnownPort() {
		String sql = "" +
		"SELECT EXPAND(DISTINCT(out)) FROM (SELECT out('hasTargetHost') FROM TcpConnection WHERE targetPort IN (SELECT port FROM WellKnownPort))";
		@SuppressWarnings("unchecked") // We know.
		Iterable<Vertex> vertices = (Iterable<Vertex>) this.ogf.command(new OCommandSQL(sql)).execute();
		return this.getListFromVertices(vertices);
	}
	
	private List<HostModel> getListFromVertices(Iterable<Vertex> vertices) {
		List<HostModel> result = new ArrayList<HostModel>();
		for(Vertex v : vertices) {
			result.add(new HostModel(v));
		}
		return result;
	}
	
}
