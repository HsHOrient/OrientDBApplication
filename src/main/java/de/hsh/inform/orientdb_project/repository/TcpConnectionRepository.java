package de.hsh.inform.orientdb_project.repository;

import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.TcpConnectionModel;

public class TcpConnectionRepository {

	private OrientGraphNoTx ogf;
	
	public TcpConnectionRepository(OrientGraphNoTx ogf) {
		this.ogf = ogf;
	}
	
	public Object findByActiveWhen(long ts) {
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
		
		System.out.println("Ergebnisse:");
		for(Vertex v : gq.vertices()) {
			for(String key : v.getPropertyKeys()) {
				System.out.print(key + ": " + v.getProperty(key) + ", ");
			}
			System.out.println();
		}
		System.out.println("----");
		return null;
	} 
	
}
