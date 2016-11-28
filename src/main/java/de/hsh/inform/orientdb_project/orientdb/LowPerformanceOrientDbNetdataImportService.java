package de.hsh.inform.orientdb_project.orientdb;

import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;

/**
 * This service is incomplete! DO NOT USE!
 */
public class LowPerformanceOrientDbNetdataImportService extends AbstractNetdataImportService {

	private OrientGraphNoTx og;
	
	public LowPerformanceOrientDbNetdataImportService(String filename, OrientGraphNoTx orientGraph) {
		super(filename);
		this.og = orientGraph;
	}
	
	public void handleEthernetPacket(EthernetPacket ether, long ts, int ms) {
		Vertex ethernetFrame = this.og.addVertex("class:EthernetFrame");
		ethernetFrame.setProperty("sourceMac", ether.getHeader().getSrcAddr().toString());
		ethernetFrame.setProperty("targetMac", ether.getHeader().getDstAddr().toString());
		ethernetFrame.setProperty("rawData", ether.getRawData());
		ethernetFrame.setProperty("size", ether.getRawData().length);
		ethernetFrame.setProperty("payloadSize", ether.getRawData().length - ether.getHeader().length());
		ethernetFrame.setProperty("timestamp", ts);
		ethernetFrame.setProperty("microseconds", ms);
		super.handleEthernetPacket(ether, ts, ms);
	}
	
	public void handleArpPacket(ArpPacket arp, long ts, int ms) {
		Vertex arpPacket = this.og.addVertex("class:ArpPacket");
		arpPacket.setProperty("size", arp.getRawData().length);
		// TODO: Not finished yet!
		arpPacket.setProperty("payloadSize", arp.getRawData().length - arp.getHeader().length());
		// Wire up to its ethernet frame
		Iterable<Vertex> result = this.og.getVertices("EthernetFrame", new String[]{"microseconds", "timestamp"}, new Object[]{ms, ts});
		Vertex ethernetFrame = result.iterator().next();
		Edge containsEdge = this.og.addEdge("class:contains", ethernetFrame, arpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", arpPacket, ethernetFrame, "isContainedIn");
	}
	
	public void handleIpV4Packet(IpV4Packet ipv4, long ts, int ms) {
		Vertex ipPacket = this.og.addVertex("class:IpPacket");
		ipPacket.setProperty("sourceIp", ipv4.getHeader().getSrcAddr().toString().split("/")[1]);
		ipPacket.setProperty("targetIp", ipv4.getHeader().getDstAddr().toString().split("/")[1]);
		ipPacket.setProperty("size", ipv4.getRawData().length);
		ipPacket.setProperty("payloadSize", ipv4.getRawData().length - ipv4.getHeader().length());
		// Wire up to its ethernet frame
		Iterable<Vertex> result = this.og.getVertices("EthernetFrame", new String[]{"microseconds", "timestamp"}, new Object[]{ms, ts});
		Vertex ethernetFrame = result.iterator().next();
		Edge containsEdge = this.og.addEdge("class:contains", ethernetFrame, ipPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", ipPacket, ethernetFrame, "isContainedIn");
		super.handleIpV4Packet(ipv4, ts, ms);
	}
	
	public void handleUdpPacket(UdpPacket udp, long ts, int ms) {
		Vertex udpPacket = this.og.addVertex("class:UdpPacket");
		udpPacket.setProperty("sourcePort", udp.getHeader().getSrcPort().valueAsInt());
		udpPacket.setProperty("targetPort", udp.getHeader().getDstPort().valueAsInt());
		udpPacket.setProperty("size", udp.getRawData().length);
		udpPacket.setProperty("payloadSize", udp.getRawData().length - udp.getHeader().length());
		// Wire up to its ip packet
		Iterable<Vertex> result = this.og.getVertices("EthernetFrame", new String[]{"microseconds", "timestamp"}, new Object[]{ms, ts});
		Vertex ethernetFrame = result.iterator().next();
		Vertex ipPacket = ethernetFrame.getEdges(Direction.OUT, "contains").iterator().next().getVertex(Direction.IN);
		Edge containsEdge = this.og.addEdge("class:contains", ipPacket, udpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", udpPacket, ipPacket, "isContainedIn");
	}
	
	public void handleTcpPacket(TcpPacket tcp, long ts, int ms) {
		Vertex tcpPacket = this.og.addVertex("class:TcpPacket");
		tcpPacket.setProperty("sourcePort", tcp.getHeader().getSrcPort().valueAsInt());
		tcpPacket.setProperty("targetPort", tcp.getHeader().getDstPort().valueAsInt());
		tcpPacket.setProperty("size", tcp.getRawData().length);
		tcpPacket.setProperty("payloadSize", tcp.getRawData().length - tcp.getHeader().length());
		// Wire up to its ip packet
		Iterable<Vertex> result = this.og.getVertices("EthernetFrame", new String[]{"microseconds", "timestamp"}, new Object[]{ms, ts});
		Vertex ethernetFrame = result.iterator().next();
		Vertex ipPacket = ethernetFrame.getEdges(Direction.OUT, "contains").iterator().next().getVertex(Direction.IN);
		Edge containsEdge = this.og.addEdge("class:contains", ipPacket, tcpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", tcpPacket, ipPacket, "isContainedIn");
	}
	
	public void handleIcmpPacket(IcmpV4CommonPacket icmp, long ts, int ms) {
		Vertex icmpPacket = this.og.addVertex("class:IcmpPacket");
		icmpPacket.setProperty("size", icmp.getRawData().length);
		icmpPacket.setProperty("payloadSize", icmp.getRawData().length - icmp.getHeader().length());
		// Wire up to its ip packet
		Iterable<Vertex> result = this.og.getVertices("EthernetFrame", new String[]{"microseconds", "timestamp"}, new Object[]{ms, ts});
		Vertex ethernetFrame = result.iterator().next();
		Vertex ipPacket = ethernetFrame.getEdges(Direction.OUT, "contains").iterator().next().getVertex(Direction.IN);
		Edge containsEdge = this.og.addEdge("class:contains", ipPacket, icmpPacket, "contains");
		Edge isContainedInEdge = this.og.addEdge("class:isContainedIn", icmpPacket, ipPacket, "isContainedIn");
	}

	@Override
	public void afterImport() {
		// TODO Auto-generated method stub
	}


}
