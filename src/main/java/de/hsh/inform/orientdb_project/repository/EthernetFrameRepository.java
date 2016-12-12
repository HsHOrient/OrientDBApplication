package de.hsh.inform.orientdb_project.repository;

import java.util.List;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.EthernetFrameModel;

public class EthernetFrameRepository {
	private OrientGraphNoTx ogf;
	
	public EthernetFrameRepository(OrientGraphNoTx ogf) {
		this.ogf = ogf;
	}
	
	public List<EthernetFrameModel> findAllByRawData(byte[] content) {
		// TODO!
		return null;
	}
	
}
