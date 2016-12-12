package de.hsh.inform.orientdb_project.repository;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.sql.OCommandSQL;
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
	
	public long getTotalDataVolumePerMinuteBetweenHosts(String ipA, String ipB) {
		String sql = "SELECT FROM TcpConnection "
				+ "WHERE (sourceIp = '" + ipA + "' AND targetIp = '" + ipB + "') "
				+ "OR (sourceIp = '" + ipB + "' AND targetIp = '" + ipA + "') ";		
		@SuppressWarnings("unchecked") // We know.
		Iterable<Vertex> vertices = (Iterable<Vertex>) this.ogf.command(new OCommandSQL(sql)).execute();
		List<TcpConnectionModel> result = this.getListFromVertices(vertices);
		long earliestStartTs = -1;
		long latestEndTs = -1;
		long totalVolume = 0;
		for(TcpConnectionModel model : result) {
			System.out.println(model.toString());
			totalVolume += model.getTotalVolume();
			if(earliestStartTs == -1 || earliestStartTs > model.startTs) {
				earliestStartTs = model.startTs;
			}
			if(latestEndTs == -1 || latestEndTs < model.endTs) {
				latestEndTs = model.endTs;
			}
		}
		return (long) ((1.0*totalVolume) / (1.0*(latestEndTs - earliestStartTs)));
	}
	
	private List<TcpConnectionModel> getListFromVertices(Iterable<Vertex> vertices) {
		List<TcpConnectionModel> result = new ArrayList<TcpConnectionModel>();
		for(Vertex v : vertices) {
			result.add(new TcpConnectionModel(v));
		}
		return result;
	}
	
}
