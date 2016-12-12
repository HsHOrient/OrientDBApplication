package de.hsh.inform.orientdb_project.orientdb;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.LinkedList;

import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.ArpPacketModel;
import de.hsh.inform.orientdb_project.model.EthernetFrameModel;
import de.hsh.inform.orientdb_project.model.HostModel;
import de.hsh.inform.orientdb_project.model.IcmpPacketModel;
import de.hsh.inform.orientdb_project.model.IpPacketModel;
import de.hsh.inform.orientdb_project.model.TcpConnectionModel;
import de.hsh.inform.orientdb_project.model.TcpPacketModel;
import de.hsh.inform.orientdb_project.model.UdpPacketModel;
import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;

public class NodeBasedImportService extends AbstractNetdataImportService {

	private OrientGraphNoTx og;
	
	// HashMap that contains all already known hosts (aka already inserted into database)
	private HashMap<String, Vertex> knownHosts;
	
	// To keep track of tcp connections
	private HashMap<String, LinkedList<TcpConnectionModel>> knownTcpConnections;
	
	// References to already created model instances (these are reseted before processing a new ethernetFrame)
	private EthernetFrameModel ethernetFrameModel;
	private ArpPacketModel arpPacketModel;
	private IpPacketModel ipPacketModel;
	private TcpPacketModel tcpPacketModel;
	private UdpPacketModel udpPacketModel;
	private IcmpPacketModel icmpPacketModel;

	// References to already created vertices (these are reseted before processing a new ethernetFrame)
	private Vertex ethernetFrameVertex;
	private Vertex arpPacketVertex;
	private Vertex ipPacketVertex;
	private Vertex udpPacketVertex;
	private Vertex tcpPacketVertex;
	private Vertex icmpPacketVertex;
	
	public NodeBasedImportService(String filename, OrientGraphNoTx orientGraph) {
		super(filename);
		this.og = orientGraph;
		this.knownHosts = new HashMap<String, Vertex>();
		this.knownTcpConnections = new HashMap<String, LinkedList<TcpConnectionModel>>();
	}
	
	public void handleEthernetPacket(EthernetPacket ether, long ts, int ms) {
		// Clean up state vars before processing the next ethernet frame
		this.ethernetFrameVertex = null;
		this.arpPacketVertex = null;
		this.ipPacketVertex = null;
		this.udpPacketVertex = null;
		this.tcpPacketVertex = null;
		this.icmpPacketVertex = null;
		// Also clean model instances
		this.ethernetFrameModel = null;
		this.arpPacketModel = null;
		this.ipPacketModel = null;
		this.tcpPacketModel = null;
		this.udpPacketModel = null;
		this.icmpPacketModel = null;
		// Okay, let's go!
		this.ethernetFrameModel = new EthernetFrameModel(ether, ts, ms);
		this.ethernetFrameVertex = this.og.addVertex("class:EthernetFrame", this.ethernetFrameModel.getArguments());
		super.handleEthernetPacket(ether, ts, ms);
	}
	
	public void handleArpPacket(ArpPacket arp, long ts, int ms) {
		this.arpPacketModel = new ArpPacketModel(arp, ts, ms);
		this.arpPacketVertex = this.og.addVertex("class:ArpPacket", this.arpPacketModel.getArguments());
		// Wire up to its ethernet frame
		Edge containsEdge = this.og.addEdge("class:contains", this.ethernetFrameVertex, this.arpPacketVertex, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.arpPacketVertex, this.ethernetFrameVertex, "isContainedIn");
	}
	
	public void handleIpV4Packet(IpV4Packet ipv4, long ts, int ms) {
		this.ipPacketModel = new IpPacketModel(ipv4, ts, ms);
		this.ipPacketVertex = this.og.addVertex("class:IpPacket", this.ipPacketModel.getArguments());
		// Add hosts to database if new
		this.addHostIfNew(ipv4.getHeader().getSrcAddr());
		this.addHostIfNew(ipv4.getHeader().getDstAddr());
		// Wire up to its ethernet frame
		Edge containsEdge = this.og.addEdge("class:contains", this.ethernetFrameVertex, this.ipPacketVertex, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.ipPacketVertex, this.ethernetFrameVertex, "isContainedIn");
		super.handleIpV4Packet(ipv4, ts, ms);
	}
	
	public void handleUdpPacket(UdpPacket udp, long ts, int ms) {
		this.udpPacketModel = new UdpPacketModel(udp, ts, ms);
		this.udpPacketVertex = this.og.addVertex("class:UdpPacket", this.udpPacketModel.getArguments());
		// Wire up to its ip packet
		Edge containsEdge = this.og.addEdge("class:contains", this.ipPacketVertex, this.udpPacketVertex, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.udpPacketVertex, this.ipPacketVertex, "isContainedIn");
	}
	
	public void handleTcpPacket(TcpPacket tcp, long ts, int ms) {
		this.tcpPacketModel = new TcpPacketModel(tcp, ts, ms);
		this.tcpPacketVertex = this.og.addVertex("class:TcpPacket", this.tcpPacketModel.getArguments());
		// Wire up to its ip packet
		Edge containsEdge = this.og.addEdge("class:contains", this.ipPacketVertex, this.tcpPacketVertex, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.tcpPacketVertex, this.ipPacketVertex, "isContainedIn");
		// Track tcp connections
		TcpConnectionModel tcpConnection = this.getTcpConnectionFor(tcp);
		if(tcpConnection != null) { // If connection exists ...
			if(ts - tcpConnection.endTs < 2) { // ... and still "up to date" aka time difference < 2s
				if(tcpConnection.sourceIp.equals(this.ipPacketModel.sourceIp)) {
					// Update connection data in direction SourceIp -> TargetIp
					tcpConnection.addVolumeSourceToTarget(this.tcpPacketModel.payloadSize);
				} else {
					// Update connection data in direction TargetIp -> SourceIp
					tcpConnection.addVolumeTargetToSource(this.tcpPacketModel.payloadSize);
				}
				tcpConnection.setEnd(ts, ms);
			}
		} else {
			// Else create a new one and add it to the list.
			String sourceIp = this.ipPacketVertex.getProperty("sourceIp");
			String targetIp = this.ipPacketVertex.getProperty("targetIp");
			tcpConnection = new TcpConnectionModel(tcp, sourceIp, targetIp, ts, ms);
			this.addKnownTcpConnectionFor(tcpConnection, tcp);
		}
		// Remember tcpPacketVertex in tcpConnection for later edges
		tcpConnection.addKnownTcpPacketVertex(this.tcpPacketVertex);
	}
	
	public void handleIcmpPacket(IcmpV4CommonPacket icmp, long ts, int ms) {
		this.icmpPacketModel = new IcmpPacketModel(icmp, ts, ms);
		this.icmpPacketVertex = this.og.addVertex("class:IcmpPacket", this.icmpPacketModel.getArguments());
		// Wire up to its ip packet
		Edge containsEdge = this.og.addEdge("class:contains", this.ipPacketVertex, this.icmpPacketVertex, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.icmpPacketVertex, this.ipPacketVertex, "isContainedIn");
	}
	
	private void addHostIfNew(Inet4Address ipAddress) {
		String ipAddressStr = ipAddress.toString().split("/")[1];
		if(this.knownHosts.containsKey(ipAddressStr)) {
			return; // Host already known, nothing to do!
		} else {
			// Check internal/external by IP
			boolean isInternal = ipAddress.isSiteLocalAddress(); // TODO: VERIFY IF THIS IS CORRECT!
			// Create Vertex and add to HashMap
			HostModel hostModel = new HostModel(ipAddressStr, isInternal);
			Vertex host = this.og.addVertex("class:Host", hostModel.getArguments());
			this.knownHosts.put(ipAddressStr, host);
		}
	}
	
	private String buildConnectionKey(String source, String target) {
		int comp = source.compareTo(target);
		if(comp > 0) {
			return source + "-" + target;
		} else if(comp < 0) {
			return target + "-" + source;
		} else {
			// This should NEVER happen!
			throw new RuntimeException("I told you so, this was not impossible!");
		}
	}
	
	private TcpConnectionModel getTcpConnectionFor(TcpPacket tcp) {
		String source = "";
		String target = "";
		String sourceIp = this.ipPacketVertex.getProperty("sourceIp");
		String targetIp = this.ipPacketVertex.getProperty("targetIp");
		String sourcePort = tcp.getHeader().getSrcPort().valueAsString();
		String targetPort = tcp.getHeader().getDstPort().valueAsString();
		source = sourceIp + ":" + sourcePort;
		target = targetIp + ":" + targetPort;
		String connectionKey = this.buildConnectionKey(source, target);
		TcpConnectionModel tcpConnection = null;
		LinkedList<TcpConnectionModel> connectionList = null;
		// Get or create tcp connection list for connection key
		if(this.knownTcpConnections.containsKey(connectionKey)) {
			connectionList = this.knownTcpConnections.get(connectionKey);
			// Get last connection from list
			if(!connectionList.isEmpty()) {
				// Use existing connection if not ended yet
				tcpConnection = connectionList.getLast();
			}
		}
		return tcpConnection;
	}
	
	private void addKnownTcpConnectionFor(TcpConnectionModel tcpConnection, TcpPacket tcp) {
		String source = "";
		String target = "";
		String sourceIp = this.ipPacketVertex.getProperty("sourceIp");
		String targetIp = this.ipPacketVertex.getProperty("targetIp");
		String sourcePort = tcp.getHeader().getSrcPort().valueAsString();
		String targetPort = tcp.getHeader().getDstPort().valueAsString();
		source = sourceIp + ":" + sourcePort;
		target = targetIp + ":" + targetPort;
		String connectionKey = this.buildConnectionKey(source, target);
		LinkedList<TcpConnectionModel> connectionList = null;
		if(this.knownTcpConnections.containsKey(connectionKey)) {
			connectionList = this.knownTcpConnections.get(connectionKey);
		} else {
			connectionList = new LinkedList<TcpConnectionModel>();
			this.knownTcpConnections.put(connectionKey, connectionList);
		}
		// Put connection into list of known tcp connections
		connectionList.addLast(tcpConnection);
	}
	
	public void afterImport() {
		System.out.println(System.currentTimeMillis()/1000L + ": All done. Processing collected TcpConnections ...");
		for(LinkedList<TcpConnectionModel> connList : this.knownTcpConnections.values()) {
			for(TcpConnectionModel conn : connList) {
				Vertex currentTcpConnection = this.og.addVertex("class:TcpConnection", conn.getArguments());
				// Look up already created source and target host vertices
				Vertex sourceHost = this.knownHosts.get(conn.sourceIp);
				Vertex targetHost = this.knownHosts.get(conn.targetIp);
				// Link them up with the tcpConnection
				// class, from, to, label
				Edge hasSourceHost = this.og.addEdge("class:hasSourceHost", currentTcpConnection, sourceHost, "hasSourceHost");
				Edge hasTargetHost = this.og.addEdge("class:hasTargetHost", currentTcpConnection, targetHost, "hasTargetHost");

				Edge isSourceHostFor = this.og.addEdge("class:isSourceHostFor", sourceHost, currentTcpConnection, "isSourceHostFor");
				Edge isTargetHostFor = this.og.addEdge("class:isTargetHostFor", targetHost, currentTcpConnection, "isTargetHostFor");
				// Now link it up to all related tcpPackets
				for(Vertex tcpPacketVertex : conn.knownTcpPacketVertices) {
					Edge hasRelatedTcpPacket = this.og.addEdge("class:hasRelatedTcpPacket", currentTcpConnection, tcpPacketVertex, "hasRelatedTcpPacket");
					Edge belongsToTcpConnection = this.og.addEdge("class:belongsToTcpConnection", tcpPacketVertex, currentTcpConnection, "belongsToTcpConnection");
				}
			}
		}
		System.out.println(System.currentTimeMillis()/1000L + ": Done importing TcpConnections. End of afterImport() routine.");
	}


}
