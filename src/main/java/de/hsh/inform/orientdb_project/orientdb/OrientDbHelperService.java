package de.hsh.inform.orientdb_project.orientdb;

import java.io.IOException;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientConfigurableGraph.THREAD_MODE;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.ArpPacketModel;
import de.hsh.inform.orientdb_project.model.EthernetFrameModel;
import de.hsh.inform.orientdb_project.model.HostModel;
import de.hsh.inform.orientdb_project.model.IcmpPacketModel;
import de.hsh.inform.orientdb_project.model.IpPacketModel;
import de.hsh.inform.orientdb_project.model.TcpConnectionModel;
import de.hsh.inform.orientdb_project.model.TcpPacketModel;
import de.hsh.inform.orientdb_project.model.UdpPacketModel;
import de.hsh.inform.orientdb_project.model.WellKnownPortModel;

public class OrientDbHelperService {

	private String host;
	private String db;
	private String user;
	private String pass;
	
	private OrientGraphFactory factory;
	
	
	public OrientDbHelperService(String host, String db, String user, String pass) {
		this.host = host;
		this.db = db;
		this.user = user;
		this.pass = pass;
		this.factory = null;
	}
	
	public ODatabaseDocumentTx getDatabaseDocument() {
		return this.factory.getDatabase();
	}
	
	
	public OrientGraphFactory getOrientGraphFactory() {
		if(this.factory == null) {
			this.factory = new OrientGraphFactory(getDbUri(true), this.user, this.pass);
			// Settings concerning import performance (?)
			this.factory.declareIntent(new OIntentMassiveInsert());
			this.factory.setThreadMode(THREAD_MODE.MANUAL);
			this.factory.setAutoStartTx(false);
			this.factory.setKeepInMemoryReferences(false);
			this.factory.setRequireTransaction(false);
			this.factory.setUseLog(false);
			this.factory.setupPool(1, 1);
		}
		// Return the factory
		return this.factory;
	}

	public void close() {
		this.getOrientGraphFactory().close();
	}

	public OrientGraphNoTx getOrientGraphNoTx() {
		OrientGraphNoTx ogf = this.getOrientGraphFactory().getNoTx();
		return ogf;

	}
	
	public String getDbUri(boolean withDb) {
		String uri = "remote:" + this.host;
		if(withDb) {
			uri += "/" + this.db;
		}
		return uri;
	}
	
	public void cleanUpServer() {
		//String storageType = "plocal";
		String storageType = "memory";
		// Drop old database ...
		OServerAdmin admin = null;
		try {
			admin = new OServerAdmin(getDbUri(false));
			admin.connect(this.user, this.pass);
			admin.dropDatabase(this.db, storageType);
		} catch (IOException e) {
			System.err.println("Could not drop database: " + this.getDbUri(true));
			e.printStackTrace();
		} finally {
			admin.close();
		}
		// Create new database ...
		try {
			admin = new OServerAdmin(getDbUri(false));
			admin.connect(this.user, this.pass);
			admin.createDatabase(this.db, "graph", storageType);
		} catch (IOException e) {
			System.err.println("Could not create new database: " + this.getDbUri(true));
			e.printStackTrace();
		} finally {
			admin.close();
		}
	}
	
	public void setupSchema() {
		this.createClasses();
		this.createIndexes();
		this.importStaticData();
	}

	private void importStaticData() {
		int[] ports = {22, 25, 53, 80, 443};
		String[] descriptions = {"SSH", "SMTP", "DNS", "HTTP", "HTTPS"};
		OrientGraphNoTx og = this.getOrientGraphFactory().getNoTx();
		for(int i = 0; i < ports.length; i++) {
			WellKnownPortModel m = new WellKnownPortModel(ports[i], descriptions[i]);
			og.addVertex("class:WellKnownPort", m.getArguments());
		}
	}

	private void createIndexes() {
		OrientGraphNoTx og = this.getOrientGraphFactory().getNoTx();
		String[] createIndexQueries = {
			"CREATE INDEX EthernetFrameTime ON EthernetFrame (timestamp,microseconds) UNIQUE;",
			"CREATE INDEX IpPacketPath ON IpPacket (sourceIp, targetIp) NOTUNIQUE_HASH_INDEX;",
			"CREATE INDEX UdpPacketPath ON UdpPacket (sourcePort, targetPort) NOTUNIQUE_HASH_INDEX;",
			"CREATE INDEX TcpPacketPath ON TcpPacket (sourcePort, targetPort) NOTUNIQUE_HASH_INDEX;",
			"CREATE INDEX TcpConnectionTimes ON TcpConnection (startTs, startMs, endTs, endMs) UNIQUE;",
			"CREATE INDEX Host.ipAddress UNIQUE;",
			"CREATE INDEX WellKnownPort.port UNIQUE;"
		};
		for(String createIndexQuery : createIndexQueries) {
			og.command(new OCommandSQL(createIndexQuery)).execute();
		}
		og.shutdown();
	}
	
	private void createClasses() {
		OrientGraphNoTx og = this.getOrientGraphFactory().getNoTx();
		
		// Use methods integrated into the models to create their classes
		// These are the vertex types used in our model
		EthernetFrameModel.createType(og);
		ArpPacketModel.createType(og);
		IpPacketModel.createType(og);
		UdpPacketModel.createType(og);
		TcpPacketModel.createType(og);
		IcmpPacketModel.createType(og);

		HostModel.createType(og);	
		TcpConnectionModel.createType(og);
		WellKnownPortModel.createType(og);
		
		// Edges do not really need their own model (yet), they connect the vertex types
		OrientEdgeType isContainedInType = og.createEdgeType("isContainedIn", "E");
		isContainedInType.setDescription("isContainedIn");
		OrientEdgeType containsType = og.createEdgeType("contains", "E");
		containsType.setDescription("contains");

		OrientEdgeType hasSourceHostType = og.createEdgeType("hasSourceHost", "E");
		hasSourceHostType.setDescription("hasSourceHost");
		OrientEdgeType hasTargetHostType = og.createEdgeType("hasTargetHost", "E");
		hasTargetHostType.setDescription("hasTargetHost");

		OrientEdgeType isSourceHostForType = og.createEdgeType("isSourceHostFor", "E");
		isSourceHostForType.setDescription("isSourceHostFor");
		OrientEdgeType isTargetHostForType = og.createEdgeType("isTargetHostFor", "E");
		isTargetHostForType.setDescription("isTargetHostFor");
		
		OrientEdgeType belongsToTcpConnectionType = og.createEdgeType("belongsToTcpConnection", "E");
		belongsToTcpConnectionType.setDescription("belongsToTcpConnection");
		
		OrientEdgeType hasRelatedTcpPacketType = og.createEdgeType("hasRelatedTcpPacket", "E");
		hasRelatedTcpPacketType.setDescription("hasRelatedTcpPacket");
		
		// We're done creating classes and types, shut down the database graph.
		og.shutdown();
	}
	
}
