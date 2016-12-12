package de.hsh.inform.orientdb_project.repository;

import java.util.List;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.HostModel;

public class HostRepository {

		private OrientGraphNoTx ogf;
		
		public HostRepository(OrientGraphNoTx ogf) {
			this.ogf = ogf;
		}
		
		public List<HostModel> findByConnectionsTo(String ipAddress, int port) {
			// TODO!
			return null;
		}

		public List<HostModel> findAllByConnectionsToOutsideHosts() {
			// TODO!
			return null;
		}

		
		public List<HostModel> findByIncomingConnectionOnPort(int port) {
			return null;
		}
		

		
}
