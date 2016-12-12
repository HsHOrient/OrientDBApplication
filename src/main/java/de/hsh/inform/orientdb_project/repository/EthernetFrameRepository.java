package de.hsh.inform.orientdb_project.repository;

import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.EthernetFrameModel;

public class EthernetFrameRepository {
	private OrientGraphNoTx ogf;
	
	public EthernetFrameRepository(OrientGraphNoTx ogf) {
		this.ogf = ogf;
	}
	
	public List<EthernetFrameModel> findAllByRawData(byte[] content) {
		GraphQuery gq = this.ogf.query();
		gq = gq.has("@class", "EthernetFrame");
		// TODO
		return this.getListFromVertices(gq.vertices());
	}
	
	private List<EthernetFrameModel> getListFromVertices(Iterable<Vertex> vertices) {
		List<EthernetFrameModel> result = new ArrayList<EthernetFrameModel>();
		for(Vertex v : vertices) {
			result.add(new EthernetFrameModel(v));
		}
		return result;
	}
	
}
