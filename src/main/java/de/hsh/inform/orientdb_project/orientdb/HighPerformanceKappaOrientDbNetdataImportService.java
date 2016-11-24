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

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;

public class HighPerformanceKappaOrientDbNetdataImportService extends AbstractNetdataImportService {

	private OrientGraphNoTx og;
	
	// HashMap that contains all already known hosts (aka already inserted into database)
	private HashMap<String, Vertex> knownHosts;
	
	// To keep track of tcp connections
	private HashMap<String, LinkedList<TcpConnection>> knownTcpConnections;
	
	private Vertex ethernetFrame;
	private Vertex arpPacket;
	private Vertex ipPacket;
	private Vertex udpPacket;
	private Vertex tcpPacket;
	private Vertex icmpPacket;
	
	private long packetCounter;
	
	public HighPerformanceKappaOrientDbNetdataImportService(String filename, OrientGraphNoTx orientGraph) {
		super(filename);
		this.og = orientGraph;
		this.knownHosts = new HashMap<String, Vertex>();
		this.knownTcpConnections = new HashMap<String, LinkedList<TcpConnection>>();
	}
	
	public void handleEthernetPacket(EthernetPacket ether, long ts, int ms) {
		this.packetCounter++;
		if(this.packetCounter > 2000) {
			for(LinkedList<TcpConnection> connList : this.knownTcpConnections.values()) {
				for(TcpConnection conn : connList) {
					System.out.println(conn.toString());
				}
			}
			System.exit(0);
		}
		// Clean up state vars before processing the next ethernet frame
		this.ethernetFrame = null;
		this.arpPacket = null;
		this.ipPacket = null;
		this.udpPacket = null;
		this.tcpPacket = null;
		this.icmpPacket = null;
		// Okay, let's go!
		Object[] arguments = {
			"sourceMac", ether.getHeader().getSrcAddr().toString(),
			"targetMac", ether.getHeader().getDstAddr().toString(),
		    "rawData", ether.getRawData(),
		    "size", ether.getRawData().length,
		    "payloadSize", ether.getRawData().length - ether.getHeader().length(),
		    "timestamp", ts,
		    "microseconds", ms,
		};
		this.ethernetFrame = this.og.addVertex("class:EthernetFrame", arguments);
		super.handleEthernetPacket(ether, ts, ms);
	}
	
	public void handleArpPacket(ArpPacket arp, long ts, int ms) {
		Object[] arguments = {
			"size", arp.getRawData().length,
			"payloadSize", arp.getRawData().length - arp.getHeader().length(),
		};
		this.arpPacket = this.og.addVertex("class:ArpPacket", arguments);
		// Wire up to its ethernet frame
		Edge containsEdge = this.og.addEdge("class:contains", this.ethernetFrame, this.arpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.arpPacket, this.ethernetFrame, "isContainedIn");
	}
	
	public void handleIpV4Packet(IpV4Packet ipv4, long ts, int ms) {
		Inet4Address sourceIp = ipv4.getHeader().getSrcAddr();
		Inet4Address targetIp = ipv4.getHeader().getDstAddr();
		// Add hosts to database if new
		this.addHostIfNew(sourceIp);
		this.addHostIfNew(targetIp);
		Object[] arguments = {
			"sourceIp", sourceIp.toString().split("/")[1],
			"targetIp", targetIp.toString().split("/")[1],
			"size", ipv4.getRawData().length,
			"payloadSize", ipv4.getRawData().length - ipv4.getHeader().length(),
		};
		this.ipPacket = this.og.addVertex("class:IpPacket", arguments);
		// Wire up to its ethernet frame
		Edge containsEdge = this.og.addEdge("class:contains", this.ethernetFrame, this.ipPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.ipPacket, this.ethernetFrame, "isContainedIn");
		super.handleIpV4Packet(ipv4, ts, ms);
	}
	
	public void handleUdpPacket(UdpPacket udp, long ts, int ms) {
		Object[] arguments = {
			"sourcePort", udp.getHeader().getSrcPort().valueAsInt(),
			"targetPort", udp.getHeader().getDstPort().valueAsInt(),
			"size", udp.getRawData().length,
			"payloadSize", udp.getRawData().length - udp.getHeader().length(),
		};
		this.udpPacket = this.og.addVertex("class:UdpPacket");
		// Wire up to its ip packet
		Edge containsEdge = this.og.addEdge("class:contains", this.ipPacket, this.udpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.udpPacket, this.ipPacket, "isContainedIn");
	}
	
	public void handleTcpPacket(TcpPacket tcp, long ts, int ms) {
		Object[] arguments = {
			"sourcePort", tcp.getHeader().getSrcPort().valueAsInt(),
			"targetPort", tcp.getHeader().getDstPort().valueAsInt(),
			"size", tcp.getRawData().length,
			"payloadSize", tcp.getRawData().length - tcp.getHeader().length(),
		};
		this.tcpPacket = this.og.addVertex("class:TcpPacket", arguments);
		// Wire up to its ip packet
		Edge containsEdge = this.og.addEdge("class:contains", this.ipPacket, this.tcpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.tcpPacket, this.ipPacket, "isContainedIn");
		// Track tcp connections
		TcpConnection tcpConnection = this.getTcpConnectionFor(tcp);
		// If connection exists and still "up to date" aka time difference < 1s
		if(tcpConnection != null && (ts - tcpConnection.endTs <= 1)) {
			// Update tcpConnection data
			if(tcpConnection.sourceIp.equals(this.ipPacket.getProperty("sourceIp"))) {
				// SourceIp -> TargetIp
				tcpConnection.addVolumeSourceToTarget(tcp.getRawData().length - tcp.getHeader().length());
			} else {
				// TargetIp -> SourceIp
				tcpConnection.addVolumeTargetToSource(tcp.getRawData().length - tcp.getHeader().length());
			}
			tcpConnection.setEnd(ts, ms);
		} else {
			// Else create a new one and add it to the list.
			String sourceIp = this.ipPacket.getProperty("sourceIp");
			String targetIp = this.ipPacket.getProperty("targetIp");
			tcpConnection = new TcpConnection(tcp, sourceIp, targetIp, ts, ms);
			this.addKnownTcpConnectionFor(tcpConnection, tcp);
		}
		
	}
	
	public void handleIcmpPacket(IcmpV4CommonPacket icmp, long ts, int ms) {
		Object[] arguments = {
			"size", icmp.getRawData().length,
			"payloadSize", icmp.getRawData().length - icmp.getHeader().length(),
		};
		this.icmpPacket = this.og.addVertex("class:IcmpPacket");
		// Wire up to its ip packet
		Edge containsEdge = this.og.addEdge("class:contains", this.ipPacket, this.icmpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", this.icmpPacket, this.ipPacket, "isContainedIn");
	}
	
	private void addHostIfNew(Inet4Address ipAddress) {
		if(this.knownHosts.containsKey(ipAddress)) {
			// Host already known, nothing to do!
			return;
		} else {
			// Check internal/external by IP
			boolean isInternal = ipAddress.isSiteLocalAddress(); // TODO: VERIFY IF THIS IS CORRECT!
			// Create Vertex and add to HashMap
			String ipAddressStr = ipAddress.toString().split("/")[1];
			Object[] arguments = {
				"ipAddress", ipAddressStr,
				"internal", isInternal,
			};
			Vertex host = this.og.addVertex("class:Host", arguments);
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
	
	private TcpConnection getTcpConnectionFor(TcpPacket tcp) {
		String source = "";
		String target = "";
		String sourceIp = this.ipPacket.getProperty("sourceIp");
		String targetIp = this.ipPacket.getProperty("targetIp");
		String sourcePort = tcp.getHeader().getSrcPort().valueAsString();
		String targetPort = tcp.getHeader().getDstPort().valueAsString();
		source = sourceIp + ":" + sourcePort;
		target = targetIp + ":" + targetPort;
		String connectionKey = this.buildConnectionKey(source, target);
		TcpConnection tcpConnection = null;
		LinkedList<TcpConnection> connectionList = null;
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
	
	private void addKnownTcpConnectionFor(TcpConnection tcpConnection, TcpPacket tcp) {
		String source = "";
		String target = "";
		String sourceIp = this.ipPacket.getProperty("sourceIp");
		String targetIp = this.ipPacket.getProperty("targetIp");
		String sourcePort = tcp.getHeader().getSrcPort().valueAsString();
		String targetPort = tcp.getHeader().getDstPort().valueAsString();
		source = sourceIp + ":" + sourcePort;
		target = targetIp + ":" + targetPort;
		String connectionKey = this.buildConnectionKey(source, target);
		LinkedList<TcpConnection> connectionList = null;
		if(this.knownTcpConnections.containsKey(connectionKey)) {
			connectionList = this.knownTcpConnections.get(connectionKey);
		} else {
			connectionList = new LinkedList<TcpConnection>();
			this.knownTcpConnections.put(connectionKey, connectionList);
		}
		// Put connection into list of known tcp connections
		connectionList.addLast(tcpConnection);
	}
	
	public void afterImport() {
		// TODO: Insert all TcpConnections!
		System.out.println("Fertig!");
	}


}
