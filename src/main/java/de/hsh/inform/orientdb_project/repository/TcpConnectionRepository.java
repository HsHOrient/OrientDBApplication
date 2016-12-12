package de.hsh.inform.orientdb_project.repository;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.TcpConnectionModel;

public class TcpConnectionRepository {

	private OrientGraphNoTx ogf;
	
	public TcpConnectionRepository(OrientGraphNoTx ogf) {
		this.ogf = ogf;
	}
	
	public List<TcpConnectionModel> findByActiveWhen(long ts) {
		/*
		GraphQuery gq = this.ogf.query();
		gq = gq.has("@class", "TcpConnection");
		
		gq = gq.has("startTs", new Predicate() {
			@Override
			public boolean evaluate(Object seen, Object given) {
				return (Long) seen <= (Long) given;
			}
		}, ts);
		
		gq = gq.has("endTs", new Predicate() {
			@Override
			public boolean evaluate(Object seen, Object given) {
				return (Long) seen >= (Long) given;
			}
		}, ts);

		return this.getListFromVertices(gq.vertices());*/
		
		String sql = "SELECT FROM TcpConnection WHERE startTs <= " + ts + " AND endTs >= " + ts + "";
		@SuppressWarnings("unchecked") // We know.
		Iterable<Vertex> vertices = (Iterable<Vertex>) this.ogf.command(new OCommandSQL(sql)).execute();
		return this.getListFromVertices(vertices);
	}
	
	public List<TcpConnectionModel> getTotalDataVolumeBetweenHosts(String ipA, String ipB) {
		GraphQuery gq = this.ogf.query();
		gq = gq.has("@class", "TcpConnection");
		// TODO
		return this.getListFromVertices(gq.vertices());
	}
	
	private List<TcpConnectionModel> getListFromVertices(Iterable<Vertex> vertices) {
		List<TcpConnectionModel> result = new ArrayList<TcpConnectionModel>();
		for(Vertex v : vertices) {
			result.add(new TcpConnectionModel(v));
		}
		return result;
	}
	
}
