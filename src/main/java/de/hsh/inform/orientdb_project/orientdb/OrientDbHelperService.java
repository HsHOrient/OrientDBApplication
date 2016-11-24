package de.hsh.inform.orientdb_project.orientdb;

import java.io.IOException;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import com.tinkerpop.blueprints.impls.orient.OrientConfigurableGraph.THREAD_MODE;

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
	
	public OrientGraphFactory getOrientGraphFactory() {
		if(this.factory == null) {
			this.factory = new OrientGraphFactory(getDbUri(true), this.user, this.pass);
			this.factory.declareIntent(new OIntentMassiveInsert());
			this.factory.setThreadMode(THREAD_MODE.ALWAYS_AUTOSET);
		}
		return this.factory;
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
		// Drop old database and re-create it
		OServerAdmin admin = null;
		try {
			admin = new OServerAdmin(getDbUri(false));
			admin.connect(this.user, this.pass);
			admin.dropDatabase(this.db, storageType);
			admin.createDatabase(this.db, "graph", storageType);
		} catch (IOException e) {
			try {
				admin.createDatabase(this.db, "graph", storageType);
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public void setupSchema() {
		this.createClasses();
		this.createClusters();
	}
	
	private void createClasses() {
		OrientGraphNoTx og = this.getOrientGraphFactory().getNoTx();
		
		OrientVertexType ethernetFrameType = og.createVertexType("EthernetFrame", "V");
		ethernetFrameType.createProperty("sourceMac", OType.STRING);
		ethernetFrameType.createProperty("targetMac", OType.STRING);
		ethernetFrameType.createProperty("rawData", OType.BINARY);
		ethernetFrameType.createProperty("size", OType.INTEGER);
		ethernetFrameType.createProperty("payloadSize", OType.INTEGER);
		ethernetFrameType.createProperty("timestamp", OType.LONG);
		ethernetFrameType.createProperty("microseconds", OType.INTEGER);

		OrientVertexType arpPacketType = og.createVertexType("ArpPacket", "V");
		// TODO: Not finished!
		arpPacketType.createProperty("askedForIp", OType.STRING);
		arpPacketType.createProperty("hasIp", OType.STRING);
		arpPacketType.createProperty("size", OType.INTEGER);
		arpPacketType.createProperty("payloadSize", OType.INTEGER);
		
		OrientVertexType ipPacketType = og.createVertexType("IpPacket", "V");
		ipPacketType.createProperty("sourceIp", OType.STRING);
		ipPacketType.createProperty("targetIp", OType.STRING);
		ipPacketType.createProperty("size", OType.INTEGER);
		ipPacketType.createProperty("payloadSize", OType.INTEGER);
		
		OrientVertexType udpPacketType = og.createVertexType("UdpPacket", "V");
		udpPacketType.createProperty("sourcePort", OType.INTEGER);
		udpPacketType.createProperty("targetPort", OType.INTEGER);
		udpPacketType.createProperty("size", OType.INTEGER);
		udpPacketType.createProperty("payloadSize", OType.INTEGER);
		
		OrientVertexType tcpPacketType = og.createVertexType("TcpPacket", "V");
		tcpPacketType.createProperty("sourcePort", OType.INTEGER);
		tcpPacketType.createProperty("targetPort", OType.INTEGER);
		tcpPacketType.createProperty("size", OType.INTEGER);
		tcpPacketType.createProperty("payloadSize", OType.INTEGER);

		OrientVertexType icmpPacketType = og.createVertexType("IcmpPacket", "V");
		icmpPacketType.createProperty("size", OType.INTEGER);
		icmpPacketType.createProperty("payloadSize", OType.INTEGER);
		
		OrientVertexType hostType = og.createVertexType("Host", "V");
		hostType.createProperty("ipAddress", OType.STRING);
		hostType.createProperty("internal", OType.BOOLEAN);
		
		OrientVertexType tcpConnectionType = og.createVertexType("TcpConnection", "V");
		tcpConnectionType.createProperty("start", OType.DATETIME);
		tcpConnectionType.createProperty("end", OType.DATETIME);
		tcpConnectionType.createProperty("sourcePort", OType.INTEGER);
		tcpConnectionType.createProperty("targetPort", OType.INTEGER);
		tcpConnectionType.createProperty("volumeSourceToTarget", OType.LONG);
		tcpConnectionType.createProperty("volumeTargetToSource", OType.LONG);
		tcpConnectionType.createProperty("totalVolume", OType.LONG);
		
		OrientEdgeType isContainedInType = og.createEdgeType("isContainedIn", "E");
		isContainedInType.setDescription("isContainedIn");
		OrientEdgeType containsType = og.createEdgeType("contains", "E");
		containsType.setDescription("contains");

		og.shutdown();
	}

	private void createClusters() {
		OServerAdmin admin = null;
		try {
			admin = new OServerAdmin(getDbUri(false));
			admin.connect(this.user, this.pass);
		} catch (IOException e) {
			try {
				admin.createDatabase(this.db, "graph", "plocal");
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
	}
	
}
